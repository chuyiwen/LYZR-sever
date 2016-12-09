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
package sophia.mmorpg.base.scene.mgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.pluck.Pluck;
import sophia.mmorpg.pluck.PluckConst;
import sophia.mmorpg.pluck.PluckRef;
import sophia.mmorpg.pluck.gameEvent.PluckRefresh_GE;
import sophia.mmorpg.pluck.gameEvent.PluckSuccess_GE;

import com.google.common.base.Preconditions;

public final class PluckMgrComponent extends ConcreteComponent<GameScene> {
	private static Logger logger = Logger.getLogger(PluckMgrComponent.class);
	private CopyOnWriteArrayList<Pluck> pluckRefreshList = new CopyOnWriteArrayList<Pluck>();
	private ConcurrentHashMap<String, Pluck> pluckMap = new ConcurrentHashMap<String, Pluck>();
	private SFTimer pluckTimer;
	private SFTimer refreshTimer;
	public static final String PluckSuccess_GE_ID = PluckSuccess_GE.class.getSimpleName();
	public static final String PluckRefresh_GE_ID = PluckRefresh_GE.class.getSimpleName();

	@Override
	public void ready() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		pluckTimer = timerCreater.secondInterval(new SFTimeChimeListener() {

			@Override
			public void handleServiceShutdown() {
			}

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				pluckTick();
			}

		});

		refreshTimer = timerCreater.secondInterval(new SFTimeChimeListener() {

			@Override
			public void handleServiceShutdown() {
			}

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				refreshTick();

			}

		});
	}

	@Override
	public void suspend() {
		if (pluckTimer != null) {
			pluckTimer.cancel();
		}
		if (refreshTimer != null) {
			refreshTimer.cancel();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("pluckTimer,refreshTimer stopped");
		}
	}

	@Override
	public void destroy() {
		suspend();
		clear();

		super.destroy();
	}

	private void clear() {
		for (String id : pluckMap.keySet()) {
			Pluck pluck = pluckMap.get(id);
			GameScene crtScene = pluck.getCrtScene();
			GameRoot.getGameObjectManager().removeGameObject(pluck);
			pluck.getAoiComponent().leaveScene(crtScene);
		}
		pluckMap.clear();

		for (Pluck pluck1 : pluckRefreshList) {
			GameScene crtScene = pluck1.getCrtScene();
			GameRoot.getGameObjectManager().removeGameObject(pluck1);
			pluck1.getAoiComponent().leaveScene(crtScene);
		}
		pluckRefreshList.clear();
	}

	public Pluck createpluck(String pluckRefId) {
		return GameObjectFactory.getPluck(pluckRefId);
	}

	/**
	 * 返回正在被该玩家采集的采集物
	 */
	public Pluck getPlucking(Player player) {
		for (Entry<String, Pluck> entry : pluckMap.entrySet()) {
			if (entry.getValue().isOwner(player)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 采集物刷新
	 * 
	 * @param pluck
	 */
	public void enterRevive(Pluck pluck) {
		pluck.setPluckRefreshBeginTime(System.currentTimeMillis());
		pluckRefreshList.add(pluck);
	}

	public void enterWorld(Pluck pluck, GameScene gameScene, int x, int y) {
		if (logger.isDebugEnabled()) {
			logger.debug("enterWorld " + pluck);
		}

		addPluck(pluck);
		pluck.clear();
		pluckRefreshList.remove(pluck);
		GameRoot.getGameObjectManager().addGameObject(pluck);
		pluck.getAoiComponent().enterScene(gameScene, x, y);
		sendLeaveOrEnterWorldGameEvent(pluck, PluckRefresh_GE.RefreshType_End);
	}

	public void leaveWorld(Pluck pluck) {
		Preconditions.checkNotNull(pluck);
		byte shareType = pluck.getShareType();
		int pluckRefreshTime = pluck.getPluckRefreshTime();
		if (shareType == PluckConst.MultiShareType || pluckRefreshTime == 0) {
			return;
		}

		removePluck(pluck.getId());
		enterRevive(pluck);
		GameScene crtScene = pluck.getCrtScene();
		GameRoot.getGameObjectManager().removeGameObject(pluck);
		pluck.getAoiComponent().leaveScene(crtScene);

		sendLeaveOrEnterWorldGameEvent(pluck, PluckRefresh_GE.RefreshType_Begin);
	}

	private void sendLeaveOrEnterWorldGameEvent(Pluck pluck, byte refrshType) {
		Preconditions.checkArgument(pluck != null);
		if (logger.isDebugEnabled()) {
			logger.debug("采集物刷新通知 refrshType = " + refrshType);
		}

		PluckRefresh_GE pluckRefresh_GE = new PluckRefresh_GE();
		PluckRef pluckRef = pluck.getPluckRef();
		String pluckRefId = pluckRef.getId();
		List<ItemPair> itemPairs = pluckRef.getItemPairs();
		byte pluckType = pluck.getType();

		pluckRefresh_GE.setPluckRefId(pluckRefId);
		pluckRefresh_GE.setItemPairs(itemPairs);
		pluckRefresh_GE.setPluckType(pluckType);
		pluckRefresh_GE.setRefreshType(refrshType);

		GameEvent<?> ge = GameEvent.getInstance(PluckRefresh_GE_ID, pluckRefresh_GE);

		Map<String, Player> playerMap = this.getConcreteParent().getPlayerMgrComponent().getPlayerMap();

		for (Player player : playerMap.values()) {
			sendGameEvent(ge, player.getId());
		}
	}

	public void pluckTick() {
		for (Pluck pluck : getCopyPluckMap()) {
			pluck.removeSuccessPluckPlayer();
		}
	}

	public void refreshTick() {
		if (pluckRefreshList.isEmpty()) {
			return;
		}
		
		long now = System.currentTimeMillis();
		for (Pluck pluck : pluckRefreshList) {
			if (now - pluck.getPluckRefreshBeginTime() >= pluck.getPluckRefreshTime() * 1000) {
				enterWorld(pluck, pluck.getCrtScene(), pluck.getCrtPosition().getX(), pluck.getCrtPosition().getY());
			}
		}
	}

	public void addPluck(Pluck pluck) {
		pluckMap.put(pluck.getId(), pluck);
	}

	public Pluck removePluck(String pluckId) {
		return pluckMap.remove(pluckId);
	}

	public Pluck getPluck(String pluckId) {
		return pluckMap.get(pluckId);
	}

	public List<Pluck> getCopyPluckMap() {
		List<Pluck> copyPluckList = new ArrayList<Pluck>();

		for (Pluck pluck : pluckMap.values()) {
			copyPluckList.add(pluck);
		}

		return copyPluckList;
	}

	public boolean hasAlivePluck(byte mineType) {
		for (Pluck pluck : pluckMap.values()) {
			byte type = pluck.getType();
			if (type == mineType) {
				return true;
			}
		}

		return false;
	}

	public void sceneTick(GameEvent<?> event) {
		if (pluckMap.isEmpty()) {
			return;
		}

		for (Pluck pluck : getCopyPluckMap()) {
			try {
				sendGameEvent(event, pluck.getId());
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	public List<Pluck> getPluckRefreshList() {

		List<Pluck> pluckList = new ArrayList<Pluck>();
		synchronized (pluckRefreshList) {
			for (Pluck pluck : pluckRefreshList) {
				pluckList.add(pluck);
			}
		}

		return pluckList;
	}

}
