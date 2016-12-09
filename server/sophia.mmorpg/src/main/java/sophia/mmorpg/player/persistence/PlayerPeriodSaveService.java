/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.mmorpg.player.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.data.ObjectManager.SaveMode;
import sophia.foundation.util.DebugUtil;
import sophia.game.GameContext;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;

import com.google.common.util.concurrent.AbstractIdleService;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public final class PlayerPeriodSaveService extends AbstractIdleService {

	private static final Logger logger = Logger.getLogger(PlayerPeriodSaveService.class);

	public static final int BatchNumber = 20;

	public static final long Save_Interval_Time = 5 * 60 * 1000l / BatchNumber;

	public static final long IMSave_Interval_Time = 5 * 1000l;

	public static final long Insert_Interval_Time = 1000l;

	private static final PlayerPeriodSaveActionEvent saveActionEvent = new PlayerPeriodSaveActionEvent();

	private long saveIntervalTime = Save_Interval_Time;

	private long imSaveIntervalTime = IMSave_Interval_Time;

	private long insertIntervalTime = Insert_Interval_Time;

	private ScheduledFuture<?> scheduledFuture;

	private Object mutex = this;

	private int crtIndex = 0;

	private int playerSaveNumber;

	private int crtSaveTokenNumber;

	private List<PlayerSaveComponent> saveComponentList = new ArrayList<PlayerSaveComponent>();

	private Set<Player> saveImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	private Set<Player> saveImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());

	private Set<Player> insertImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	private Set<Player> insertImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());

	private long lastUpdateTime = 0l;

	private long lastIMUpdateTime = 0l;

	private static PlayerPeriodSaveService instance = new PlayerPeriodSaveService();
	
	private PlayerPeriodSaveService() {
	}
	
	public static PlayerPeriodSaveService getInstance() {
		return instance;
	}

	private void createSaveJob(Collection<Player> collection) {
		if (logger.isDebugEnabled()) {
			logger.debug("createSaveJob " + crtIndex);
		}

		Collection<Player> pcs = new HashSet<Player>(collection);
		Collection<Player> saveCollection = new HashSet<Player>();
		for (Player p : pcs) {
			if (p.isOnline() && p.getPeriodSaveIndex() % BatchNumber == crtIndex) {
				saveCollection.add(p);
			}
		}

		crtIndex++;
		if (crtIndex >= BatchNumber) {
			crtIndex = 0;
		}

		int tempPlayerSaveNumber = 0;
		synchronized (mutex) {
			// 上一批强制保存
			doPeriodSave();
			crtSaveTokenNumber = 0;
			playerSaveNumber = saveCollection.size();
			tempPlayerSaveNumber = playerSaveNumber;
		}

		for (Player player : saveCollection) {
			player.handleActionEvent(saveActionEvent);
		}

		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n-------------------------------------------------\ncreate saveJob\n");
			sb.append("PlayerSaveNumber = " + tempPlayerSaveNumber).append(";\n");
			sb.append("PeriodSaveBeginTime = " + System.currentTimeMillis()).append(";\n");
			sb.append("-------------------------------------------------\n");
			logger.debug(sb.toString());
		}
	}

	private void doImmediatelyInsert() {
		if (insertImmediateSetPrimary.isEmpty()) {
			return;
		}

		insertImmediateSetSecondary.addAll(insertImmediateSetPrimary);
		insertImmediateSetPrimary.removeAll(insertImmediateSetSecondary);

		for (Player player : insertImmediateSetSecondary) {

			try {
				player.getPlayerSaveComponent().snapshot();
				player.getPlayerSaveComponent().getPlayerSaveableObject().applyNew();
				save(SaveMode.ImmediatelyAndWaitSave, player.getPlayerSaveComponent());
				player.getPlayerSaveComponent().getPlayerSaveableObject().applySaved();
			} catch (Exception e) {
				insertImmediateSetPrimary.add(player);
				logger.error("doImmediatelyInsert error, player =" + player);
				logger.error("doImmediatelyInsert error, Data rollBacked!" + DebugUtil.printStack(e));
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("doImmediatelyInsert, " + player);
			}
		}

		insertImmediateSetSecondary.clear();
	}

	private void doSave(SaveMode saveMode) {
		if (saveImmediateSetPrimary.isEmpty()) {
			return;
		}

		saveImmediateSetSecondary.addAll(saveImmediateSetPrimary);
		saveImmediateSetPrimary.removeAll(saveImmediateSetSecondary);

		for (Player player : saveImmediateSetSecondary) {

			// 捕获保存数据出错
			try {
				player.getPlayerSaveComponent().snapshot();
				save(saveMode, player.getPlayerSaveComponent());
			} catch (MySQLIntegrityConstraintViolationException e) {
				logger.error("doSave error, player =" + player);
				logger.error("doSave error, " + DebugUtil.printStack(e));
			} catch (Exception e) {
				saveImmediateSetPrimary.add(player);
				logger.error("doSave error, player =" + player);
				logger.error("doSave error, Data rollBacked!" + DebugUtil.printStack(e));
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("doSave, " + player);
			}
		}

		saveImmediateSetSecondary.clear();
	}

	private void doPeriodSave() {
		for (PlayerSaveComponent component : saveComponentList) {
			try {
				save(SaveMode.PeriodBatchSave, component);
			} catch (Exception e) {
				logger.error("doPeriodSave error, " + DebugUtil.printStack(e));
			}
		}

		saveComponentList.clear();
	}

	private void save(SaveMode saveMode, PlayerSaveComponent saveComponent) throws Exception {
		GameContext.getDataService().getObjectManager().save(saveMode, PlayerSaveableObject.class, saveComponent.getPlayerSaveableObject());
	}

	@Override
	protected void startUp() throws Exception {
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					doImmediatelyInsert();

					// immediateSave(period real)
					long now = System.currentTimeMillis();
					if (now - lastIMUpdateTime >= imSaveIntervalTime) {
						lastIMUpdateTime = now;
						doSave(SaveMode.PeriodBatchSave);
					}

					// periodSave
					if (now - lastUpdateTime < saveIntervalTime) {
						return;
					}

					if (PlayerManager.getOnlineTotalCount() <= 0) {
						return;
					}

					Collection<Player> playerList = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerList();
					lastUpdateTime = now;
					createSaveJob(playerList);
				} catch (Throwable e) {
					logger.error("PlayerPeriodicSaveTask Exception:" + DebugUtil.printStack(e));
				}
			}
		}, insertIntervalTime, insertIntervalTime, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void shutDown() throws Exception {
		scheduledFuture.cancel(false);

		try {
			if (logger.isInfoEnabled()) {
				logger.info("waiting for period save future stop");
			}

			scheduledFuture.get();
		} catch (Exception e) {
		}

		if (logger.isInfoEnabled()) {
			logger.info("period save future was terminated");
		}
	}

	public void shutDownDoInsert() {
		doImmediatelyInsert();
		if (!insertImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("shutDownDoInsert error!!");
		}
	}

	public void shutDownDoSave() {
		doSave(SaveMode.ImmediatelyAndWaitSave);
		if (!saveImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("shutDownDoSave error!!");
		}
	}

	public long getSaveIntervalTime() {
		return saveIntervalTime;
	}

	public void setSaveIntervalTime(long saveIntervalTime) {
		this.saveIntervalTime = saveIntervalTime;
	}

	public void handlePlayerSaveActionEvent(Player player) {
		PlayerSaveComponent saveComponent = player.getPlayerSaveComponent();
		synchronized (mutex) {
			saveComponentList.add(saveComponent);
			crtSaveTokenNumber++;
			if (crtSaveTokenNumber >= playerSaveNumber) {
				doPeriodSave();
			}
		}
	}

	public void saveImmediateData(Player player) {
		saveImmediateSetPrimary.add(player);
	}

	public void insertImmediateData(Player player) {
		insertImmediateSetPrimary.add(player);
	}

	public Set<Player> getSaveImmediateSetPrimary() {
		return saveImmediateSetPrimary;
	}

	public Set<Player> getInsertImmediateSetPrimary() {
		return insertImmediateSetPrimary;
	}

	public void insertPlayer(Player player) {
		try {
			player.getPlayerSaveComponent().snapshot();
			player.getPlayerSaveComponent().getPlayerSaveableObject().applyNew();
			save(SaveMode.ImmediatelyAndWaitSave, player.getPlayerSaveComponent());
			player.getPlayerSaveComponent().getPlayerSaveableObject().applySaved();
		} catch (Exception e) {
			logger.error("insertPlayer error, player =" + player);
			logger.error("insertPlayer error, Data rollBacked!" + DebugUtil.printStack(e));

		}
	}

	public void updatePlayer(Player player) {
		// 捕获保存数据出错
		try {
			player.getPlayerSaveComponent().snapshot();
			save(SaveMode.ImmediatelyAndWaitSave, player.getPlayerSaveComponent());
		} catch (Exception e) {
			logger.error("updatePlayer error, player =" + player);
			logger.error("updatePlayer error, Data rollBacked!" + DebugUtil.printStack(e));

		}
	}

	public Player getInsertPlayer(String playerId) {
		for (Player player : insertImmediateSetPrimary) {
			if (StringUtils.equals(playerId, player.getId())) {
				return player;
			}
		}
		return null;
	}

	public Player getUpdatePlayer(String playerId) {
		for (Player player : saveImmediateSetPrimary) {
			if (StringUtils.equals(playerId, player.getId())) {
				return player;
			}
		}
		return null;
	}

}
