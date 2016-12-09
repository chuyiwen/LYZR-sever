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

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.task.Task;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.Position;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.aoi.SceneAOIGrid;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.ai.MonsterPerceiveComponent;
import sophia.mmorpg.monster.gameEvent.MonsterEnterWorld_GE;
import sophia.mmorpg.monster.gameEvent.MonsterLeaveWorld_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.worldBossMsg.WorldBoss;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service.State;

public final class MonsterMgrComponent extends ConcreteComponent<GameScene> {

	private static final Logger logger = Logger.getLogger(MonsterMgrComponent.class);
	// 1s
	private static final long TickInterval = 1000;
	// monsterId -> Monster
	private Map<String, Monster> monsterMap = new ConcurrentHashMap<>();
	private CopyOnWriteArrayList<Monster> monsterReviveList = new CopyOnWriteArrayList<>();
	private MonsterReviveTimeService monsterReviveTimeService = new MonsterReviveTimeService();

	@Override
	public void ready() {
		State state = monsterReviveTimeService.startAndWait();
		if (state == State.RUNNING) {
			logger.debug("MonsterReviveTimeService started");
		}
		super.ready();
	}

	@Override
	public void suspend() {
		State state = monsterReviveTimeService.stopAndWait();
		if (state == State.TERMINATED) {
			logger.debug("MonsterReviveTimeService stopped");
		}
		super.suspend();
	}

	@Override
	public void destroy() {
		suspend();
		clear();
		super.destroy();
	}

	private void clear() {
		for (String id : monsterMap.keySet()) {
			Monster monster = monsterMap.get(id);
			GameScene crtScene = monster.getCrtScene();
			GameRoot.getGameObjectManager().removeGameObject(monster);
			monster.getAoiComponent().leaveScene(crtScene);
			monster.setOnline(false);
		}

		monsterMap.clear();

		for (Monster reviveMonster : monsterReviveList) {
			GameScene crtScene = reviveMonster.getCrtScene();
			GameRoot.getGameObjectManager().removeGameObject(reviveMonster);
			reviveMonster.getAoiComponent().leaveScene(crtScene);
			reviveMonster.setOnline(false);
		}

		monsterReviveList.clear();
	}
	
	public void clearPerceiveComponentReference() {
		for (Monster monster : monsterMap.values()) {
			monster.getPerceiveComponent().clearReference();
		}

		for (Monster monster : monsterReviveList) {
			monster.getPerceiveComponent().clearReference();
		}
	}

	public void addMonster(Monster monster) {
		monsterMap.put(monster.getId(), monster);
	}

	public void removeMonster(Monster monster) {
		monsterMap.remove(monster.getId());
	}

	public Monster getMonster(String monsterId) {
		return monsterMap.get(monsterId);
	}

	public Collection<Monster> getAllMonsters() {
		return monsterMap.values();
	}

	public Monster getErrorMonster() {
		Collection<Monster> monsterValues = monsterMap.values();
		for (Monster monster : monsterValues) {
			if (monster.isDead()) {
				return monster;
			}
		}

		return null;
	}

	public Monster getMonsterByRefId(String monsterRefId) {
		Collection<Monster> monsterValues = monsterMap.values();
		for (Monster monster : monsterValues) {
			if (StringUtils.equals(monsterRefId, monster.getMonsterRef().getId())) {
				return monster;
			}
		}

		return null;
	}

	public boolean checkErrorMonster(Monster monster) {
		if (!monster.isDead() && monster.isReviveReady()) {
			return false;
		}

		if (monsterReviveList.contains(monster)) {
			return false;
		}

		long now = System.currentTimeMillis();

		synchronized (this) {
			long lastCheckTime = monster.getLastCheckTime();
			if (lastCheckTime == 0) {
				lastCheckTime = now;
				monster.setLastCheckTime(now);
			}

			if (now - lastCheckTime >= 10000) {
				logger.error("checkErrorMonster " + monster);
				enterRevive(monster);
			}
		}

		return true;
	}

	public boolean clearErrorMonsterByRefId(String monsterRefId) {
		Monster monster = getMonsterByRefId(monsterRefId);
		if (monster != null) {
			return clearErrorMonster(monster);
		}

		for (Monster tmp : monsterReviveList) {
			if (StringUtils.equals(monsterRefId, tmp.getMonsterRef().getId())) {
				monster = tmp;
				return clearErrorMonster(monster);
			}
		}

		logger.error("clearErrorMonsterByRefId error can not find monster, monsterRefId=" + monsterRefId + ", sceneRefId=" + concreteParent.getRef().getId());

		return false;
	}

	public boolean clearErrorByMonsterId(String monsterId) {
		logger.error("clearErrorByMonsterId, monsterId=" + monsterId + ", sceneRefId=" + concreteParent.getRef().getId());

		Monster monster = getMonster(monsterId);
		if (monster == null) {
			logger.error("clearErrorByMonsterId error can not find monster, monsterId=" + monsterId + ", sceneRefId=" + concreteParent.getRef().getId());
			return false;
		}

		logger.error("clearErrorByMonsterId, " + monster);

		return clearErrorMonster(monster);
	}

	private synchronized boolean clearErrorMonster(Monster monster) {

		int clientSendCount = monster.getClientSendCount();
		monster.setClientSendCount(++clientSendCount);

		if (clientSendCount >= 10) {

			monster.setClientSendCount(0);

			try {
				enterRevive(monster);
			} catch (Exception e) {
				logger.error("clearErrorMonster enterRevive error", e);
			}

			try {
				GameScene crtScene = monster.getCrtScene();
				SceneAOILayer aoiLayer = crtScene.getAoiLayer();
				SceneAOIGrid[][] matrix = aoiLayer.getMatrix();
				for (int i = 0; i < aoiLayer.getGridRows(); i++) {
					for (int j = 0; j < aoiLayer.getGridColumns(); j++) {
						SceneAOIGrid aoiGrid = matrix[i][j];
						crtScene.getAoiComponent().removeAOISprite(aoiGrid, monster);
					}
				}
			} catch (Exception e) {
				logger.error("clearErrorMonster enterRevive error", e);
			}

			logger.error("clearErrorMonster " + monster);

			return true;
		}

		return false;
	}

	public Monster createMonster(String monsterRefId) {
		Monster monster = GameObjectFactory.getMonster(monsterRefId);
		if (!monster.getMonsterRef().isSummonMonster() && !monster.getMonsterRef().isSkillSummon()) {
			monster.setPerceiveComponent((MonsterPerceiveComponent) monster.createComponent(MonsterPerceiveComponent.class));
		}
		monster.reset();
		return monster;
	}

	public void enterWorld(Monster monster, GameScene gameScene, int x, int y) {
		enterWorldImp(monster, gameScene, x, y);
		gameScene.getRefreshMonsterMgrComponent().monsterAriseListener(monster.getMonsterRef().getId(), 1);
		sendMonsterEnterWorldGameEvent(monster, getConcreteParent().getRef().getId());
	}
	
	private void sendMonsterEnterWorldGameEvent(Monster monster, String sceneRefId) {
		MonsterEnterWorld_GE enterWorld_GE = new MonsterEnterWorld_GE(sceneRefId, monster);
		GameEvent<?> ge = GameEvent.getInstance(MonsterEnterWorld_GE.class.getSimpleName(), enterWorld_GE);

		Map<String, Player> playerMap = this.getConcreteParent().getPlayerMgrComponent().getPlayerMap();

		for (Player player : playerMap.values()) {
			sendGameEvent(ge, player.getId());
		}
	}
	
	private void sendMonsterLeaveWorldGameEvent(Monster monster, String sceneRefId) {
		MonsterLeaveWorld_GE leaveWorld_GE = new MonsterLeaveWorld_GE(sceneRefId, monster);
		GameEvent<?> ge = GameEvent.getInstance(MonsterLeaveWorld_GE.class.getSimpleName(), leaveWorld_GE);

		Map<String, Player> playerMap = this.getConcreteParent().getPlayerMgrComponent().getPlayerMap();

		for (Player player : playerMap.values()) {
			sendGameEvent(ge, player.getId());
		}
	}

	public void enterWorld(List<Monster> monsterList, GameScene gameScene, List<Position> positionList) {
		for (int k = 0; k < monsterList.size(); k++) {

			Position position = MMORPGContext.getGameAreaComponent().getGameArea().getPositionNotNull(gameScene, k, positionList);
			if (position == null) {
				logger.error("This Area Hasn't Unblocked Position." + "   SceneRefId:" + gameScene.getRef().getId() + "   MonsterRefId:"
						+ monsterList.get(k).getMonsterRef().getId());
				continue;
			}

			enterWorldImp(monsterList.get(k), gameScene, position.getX(), position.getY());
		}

		if (monsterList.size() > 0) {
			String monsterRefId = monsterList.get(0).getMonsterRef().getId();
			gameScene.getRefreshMonsterMgrComponent().monsterAriseListener(monsterRefId, monsterList.size());
		}
	}

	private void enterWorldImp(Monster monster, GameScene gameScene, int x, int y) {
		if (logger.isDebugEnabled()) {
			logger.debug("enterWorldImp " + monster);
		}

		monster.getBirthPosition().setPosition(x, y);
		GameRoot.getGameObjectManager().addGameObject(monster);
		monster.getAoiComponent().enterScene(gameScene, x, y);
		addMonster(monster);
		monster.setOnline(true);
	}

	// 火墙调用leaveWorld, 存在线程安全的问题
	public synchronized void leaveWorldThreadSafe(Monster monster) {
		leaveWorld(monster);
	}

	public void leaveWorld(Monster monster) {
		if (logger.isDebugEnabled()) {
			logger.debug("leaveWorld " + monster);
		}

		GameScene crtScene = monster.getCrtScene();
		GameRoot.getGameObjectManager().removeGameObject(monster);
		monster.getAoiComponent().leaveScene(crtScene);
		removeMonster(monster);
		monster.setOnline(false);

		if (!monster.getMonsterRef().isRegularMonster() && monster.getOwner() != null) {
			monster.getOwner().setSummonMonster(null);
		}

		if (monster.getMonsterRef().isRegularMonster()) {
			crtScene.getRefreshMonsterMgrComponent().monsterDeadListener(monster.getMonsterRef().getId(), 1);
		}
		
		sendMonsterLeaveWorldGameEvent(monster, getConcreteParent().getRef().getId());
	}

	public void enterRevive(Monster monster) {
		if (logger.isDebugEnabled()) {
			logger.debug("enterRevive " + monster);
		}

		monster.setLastCheckTime(0);

		leaveWorld(monster);

		if (monster.getMonsterRefreshTime() > 0 || monster.getMonsterRefreshType() == 1) {
			monster.setLastDeadTime(System.currentTimeMillis());
		} else {
			return;
		}

		// 小偷
		monster = refreshThief(monster);

		monsterReviveList.addIfAbsent(monster);
	}

	private void checkRevive() {
		if (monsterReviveList.isEmpty()) {
			return;
		}

		long now = System.currentTimeMillis();
		for (Monster monster : monsterReviveList) {
			int monsterRefreshTime = monster.getMonsterRefreshTime();
			// 间隔刷新
			if (monster.getMonsterRefreshType() == 0) {
				if (monsterRefreshTime > 0 && now - monster.getLastDeadTime() >= monsterRefreshTime) {
					try {
						resetMonster(monster);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("checkRevive error, " + monster);
						logger.error("checkRevive error, ", e);
					}
				}
			}
			// 定时刷新
			else if (monster.getMonsterRefreshType() == 1) {
				if (isTimeToRefresh(monster)) {
					try {
						resetMonster(monster);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("checkRevive error, " + monster);
						logger.error("checkRevive error, ", e);
					}
				}
			}
		}
	}

	private Monster refreshThief(Monster monster) {
		if (!WorldBoss.isWorldThief(monster)) {
			return monster;
		}

		GameScene crtScene = monster.getCrtScene();
		int x = monster.getBirthPosition().getX();
		int y = monster.getBirthPosition().getY();
		monster = WorldBoss.getRandomWorldThief(crtScene);
		monster.setCrtScene(crtScene);
		SceneGrid randomWalkableGrid = GameSceneHelper.getRandomWalkableGrid(crtScene);
		if (randomWalkableGrid != null) {
			x = randomWalkableGrid.getColumn();
			y = randomWalkableGrid.getRow();

		}
		monster.getBirthPosition().setPosition(x, y);
		return monster;
	}

	private void resetMonster(Monster monster) {
		monster.reset();
		Position birthPosition = monster.getBirthPosition();
		enterWorld(monster, monster.getCrtScene(), birthPosition.getX(), birthPosition.getY());
		monsterReviveList.remove(monster);
		monster.revive();
		WorldBoss.sendWorldBossRefreshScrollNotice(monster);
		if (logger.isDebugEnabled()) {
			logger.debug("resetMonster " + monster);
		}
	}

	private boolean isTimeToRefresh(Monster monster) {
		boolean ret = false;
		String[] times = monster.getTimingRefresh().split("&");
		Calendar crtCalendar = Calendar.getInstance();
		for (String time : times) {
			String[] refreshTime = time.split(":");
			int hour = Integer.parseInt(refreshTime[0]);
			int minute = Integer.parseInt(refreshTime[1]);
			int second = Integer.parseInt(refreshTime[2]);
			if (crtCalendar.get(Calendar.HOUR_OF_DAY) != hour) {
				continue;
			} else if (crtCalendar.get(Calendar.MINUTE) != minute) {
				continue;
			} else if (crtCalendar.get(Calendar.SECOND) < second || crtCalendar.get(Calendar.SECOND) > second + 3) {
				continue;
			} else {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public void sceneTick(GameEvent<?> event) {
		if (monsterMap.isEmpty()) {
			return;
		}

		for (Monster monster : monsterMap.values()) {
			try {
				sendGameEvent(event, monster.getId());
				checkErrorMonster(monster);
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	private final class MonsterReviveTimeService extends AbstractIdleService {

		private Future<?> scheduleTask;

		@Override
		protected void startUp() throws Exception {
			logger.debug("MonsterReviveTimeService starting");
			try {
				scheduleTask = GameContext.getTaskManager().scheduleTask(new Task() {
					@Override
					public void run() throws Exception {
						try {
							checkRevive();
						} catch (Exception e) {
							logger.error("MonsterReviveTimeService error, " + DebugUtil.printStack(e));
						}

						try {
							scheduleTask = GameContext.getTaskManager().scheduleTask(this, TickInterval);
						} catch (Exception e) {
							logger.error("MonsterReviveTimeService error, " + DebugUtil.printStack(e));
						}
					}
				}, TickInterval);
			} catch (Exception e) {
				logger.error("MonsterReviveTimeService error, " + DebugUtil.printStack(e));
			}
		}

		@Override
		protected void shutDown() throws Exception {
			logger.debug("MonsterReviveTimeService stopping");
			if (scheduleTask != null) {
				scheduleTask.cancel(true);
			}
		}
	}

}
