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

import java.util.Collections;
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
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateSaveableObject;

import com.google.common.util.concurrent.AbstractIdleService;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class PlayerImmediateSaveService extends AbstractIdleService {

	private static final Logger logger = Logger.getLogger(PlayerImmediateSaveService.class);

	public static final long Insert_Interval_Time = 1000;
	// 5s
	public static final long Save_Interval_Time = 5000;

	private long saveIntervalTime = Save_Interval_Time;

	private long insertIntervalTime = Insert_Interval_Time;

	private long lastUpdateTime = 0l;

	private ScheduledFuture<?> scheduledFuture;

	private Set<Player> saveImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	private Set<Player> saveImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());

	private Set<Player> insertImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	private Set<Player> insertImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	private static PlayerImmediateSaveService instance = new PlayerImmediateSaveService();

	private PlayerImmediateSaveService() {
	}

	public static PlayerImmediateSaveService getInstance() {
		return instance;
	}

	private void doSave(SaveMode saveMode) {
		if (saveImmediateSetPrimary.isEmpty()) {
			return;
		}

		saveImmediateSetSecondary.addAll(saveImmediateSetPrimary);
		saveImmediateSetPrimary.removeAll(saveImmediateSetSecondary);

		for (Player player : saveImmediateSetSecondary) {

			player.getPlayerImmediateSaveComponent().getPlayerImmediateSaveableObject().applySaved();

			try {
				player.getPlayerImmediateSaveComponent().snapshot();
				save(saveMode, player);
			} catch (MySQLIntegrityConstraintViolationException e) {
				logger.error("doSave error, player =" + player);
				logger.error("doSave error, " + DebugUtil.printStack(e));
			} catch (Exception e) {
				saveImmediateSetPrimary.add(player);
				logger.error("doSave save error player =" + player);
				logger.error("doSave save error, Data rollBacked!" + DebugUtil.printStack(e));
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("doSave, " + player);
			}
		}

		saveImmediateSetSecondary.clear();
	}

	private void doImmediatelyInsert() {
		if (insertImmediateSetPrimary.isEmpty()) {
			return;
		}

		insertImmediateSetSecondary.addAll(insertImmediateSetPrimary);
		insertImmediateSetPrimary.removeAll(insertImmediateSetSecondary);

		for (Player player : insertImmediateSetSecondary) {

			try {
				player.getPlayerImmediateSaveComponent().snapshot();
				player.getPlayerImmediateSaveComponent().getPlayerImmediateSaveableObject().applyNew();
				save(SaveMode.ImmediatelyAndWaitSave, player);
			} catch (MySQLIntegrityConstraintViolationException e) {
				logger.error("doImmediatelyInsert error, player =" + player);
				logger.error("doImmediatelyInsert error, " + DebugUtil.printStack(e));
			} catch (Exception e) {
				insertImmediateSetPrimary.add(player);
				logger.error("doImmediatelyInsert insert error player =" + player);
				logger.error("doImmediatelyInsert insert error, Data rollBacked!" + e);
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("doImmediatelyInsert, " + player);
			}
		}

		insertImmediateSetSecondary.clear();
	}

	private void save(SaveMode saveMode, Player player) throws Exception {
		GameContext.getDataService().getObjectManager()
				.save(saveMode, PlayerImmediateSaveableObject.class, player.getPlayerImmediateSaveComponent().getPlayerImmediateSaveableObject());
	}

	@Override
	protected void startUp() throws Exception {
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					doImmediatelyInsert();

					long now = System.currentTimeMillis();
					long time = now - lastUpdateTime;
					if (time >= saveIntervalTime) {
						lastUpdateTime = now;
						doSave(SaveMode.PeriodBatchSave);
					}

				} catch (Throwable e) {
					logger.error("PlayerImmediateSaveTask Exception:" + DebugUtil.printStack(e));
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
			throw new RuntimeException("shutDownDoInsert error, ");
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

	public void insertImmediateData(Player player) {
		insertImmediateSetPrimary.add(player);
	}

	public void saveImmediateData(Player player) {
		saveImmediateSetPrimary.add(player);
	}

	public Set<Player> getSaveImmediateSetPrimary() {
		return saveImmediateSetPrimary;
	}

	public Set<Player> getInsertImmediateSetPrimary() {
		return insertImmediateSetPrimary;
	}

	public void insertPlayer(Player player) {
		try {
			player.getPlayerImmediateSaveComponent().snapshot();
			player.getPlayerImmediateSaveComponent().getPlayerImmediateSaveableObject().applyNew();
			save(SaveMode.ImmediatelyAndWaitSave, player);
			player.getPlayerImmediateSaveComponent().getPlayerImmediateSaveableObject().applySaved();
		} catch (Exception e) {
			logger.error("insertNoDelayPlayer error, noDelayPlayer =" + player);
			logger.error("insertNoDelayPlayer error, Data rollBacked!" + DebugUtil.printStack(e));

		}
	}

	public void updatePlayer(Player player) {
		// 捕获保存数据出错
		try {
			player.getPlayerImmediateSaveComponent().snapshot();
			save(SaveMode.ImmediatelyAndWaitSave, player);
		} catch (Exception e) {
			logger.error("updateNoDelayPlayer error, noDelayPlayer =" + player);
			logger.error("updateNoDelayPlayer error, Data rollBacked!" + DebugUtil.printStack(e));

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
