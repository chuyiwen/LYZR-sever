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
package sophia.mmorpg.gameArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.mgr.MonsterMgrComponent;
import sophia.mmorpg.base.scene.mgr.NpcMgrComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.SceneMonsterRefData;
import sophia.mmorpg.base.scene.ref.SceneNpcRefData;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.ai.MonsterAIHelper;
import sophia.mmorpg.monsterRefresh.RefreshMonsterRefData;
import sophia.mmorpg.npc.Npc;
import sophia.mmorpg.player.worldBossMsg.WorldBoss;
import sophia.mmorpg.pluck.Pluck;
import sophia.mmorpg.sceneActivities.SceneActivityMgrInterface;

import com.google.common.base.Preconditions;

public final class GameArea {

	private static final Logger logger = Logger.getLogger(GameArea.class);

	protected final ConcurrentMap<String, GameScene> idToSceneMap = new ConcurrentHashMap<>();

	private SceneActivityMgrInterface sceneActivityMgr = null;

	/** 新手村场景列表 */
	private List<GameScene> xinShouCunSceneList = new ArrayList<>();

	private void addXinShouCunGameScene(GameScene gameScene) {
		xinShouCunSceneList.add(gameScene);
	}

	public final List<GameScene> getXinShouCunSceneList() {
		return xinShouCunSceneList;
	}

	public final Collection<GameScene> getSceneCollection() {
		return idToSceneMap.values();
	}

	public final GameScene getSceneById(String sceneRefId) {
		return idToSceneMap.get(sceneRefId);
	}

	public final GameScene removeScene(String sceneRefId) {
		GameScene gameScene = getSceneById(sceneRefId);
		if (gameScene == null) {
			return null;
		}

		return removeScene(gameScene);
	}

	public final GameScene removeScene(GameScene gameScene) {
		GameRoot.getGameObjectManager().removeGameObject(gameScene);
		return idToSceneMap.remove(gameScene.getRef().getId());
	}

	public final GameScene addScene(String sceneRefId, GameScene scene) {
		return idToSceneMap.putIfAbsent(sceneRefId, scene);
	}

	public final GameScene removeGameInstanceScene(GameScene gameInstanceScene) {
		GameRoot.getGameObjectManager().removeGameObject(gameInstanceScene);
		gameInstanceScene.clear();
		return idToSceneMap.remove(gameInstanceScene.getId());
	}

	public final GameScene addGameInstanceScene(String sceneId, GameScene scene) {
		return idToSceneMap.putIfAbsent(sceneId, scene);
	}

	public final void clearAllGameScene() {
		for (GameScene gameScene : idToSceneMap.values()) {
			if (removeScene(gameScene) == null) {
				removeGameInstanceScene(gameScene);
			}
		}
	}
	
	public final void stopAllGameSceneTick() {
		for (GameScene gameScene : idToSceneMap.values()) {
			gameScene.getSceneTickComponent().stopTickService();
		}
	}

	public final void loadAllGameScene() {
		Collection<GameRefObject> allGameRefObject = GameRoot.getGameRefObjectManager().getAllGameRefObject();
		int sceneCount = 0;
		int monsterCount = 0;
		for (GameRefObject gameRefObject : allGameRefObject) {
			if (gameRefObject instanceof SceneRef) {
				SceneRef sceneRef = (SceneRef) gameRefObject;
				if (sceneRef.getType() != SceneRef.FuBen) {
					if (logger.isInfoEnabled()) {
						logger.info(sceneRef + " is loading");
					}

					GameScene createScene = createScene(sceneRef.getId());
					sceneCount++;
					monsterCount += createScene.getMonsterMgrComponent().getAllMonsters().size();
					if (sceneRef.getType() == SceneRef.XinShouCun) {
						addXinShouCunGameScene(createScene);
					}

					if (logger.isDebugEnabled()) {
						logger.debug(createScene.getAoiLayer());
					}
				}
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info("total gameScene count=" + sceneCount + ", total monster count=" + monsterCount);
		}
	}

	public final Position getPositionNotNull(GameScene gameScene, int i, List<Position> positionList) {
		for (int j = 0; j < positionList.size(); j++) {
			int temp = i + j;
			if (temp >= positionList.size()) {
				temp = temp - positionList.size();
			}
			Position position = positionList.get(temp);
			if (!GameSceneHelper.isBlocked(gameScene, position)) {
				return position;
			}
		}
		return null;
	}

	public final GameScene createScene(String sceneRefId) {

		GameScene gameScene = GameObjectFactory.getGameScene(sceneRefId);
		Preconditions.checkNotNull(gameScene, "createScene failure sceneRefId=" + sceneRefId);
		AbstractGameSceneRef sceneRef = gameScene.getRef();
		if (sceneRef.getType() == SceneRef.FuBen) {
			addGameInstanceScene(gameScene.getId(), gameScene);
		} else {
			addScene(sceneRefId, gameScene);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("createScene " + sceneRefId);
		}

		// 加载怪物数据
		loadMonster(gameScene);

		// 加载NPC和采集物数据
		loadNpc(gameScene);

		gameScene.getRefreshMonsterMgrComponent().sceneCreatedListener();

		return gameScene;
	}

	private void loadMonster(GameScene gameScene) {
		// 加载怪物数据
		logger.debug("loadMonster");
		int count = 0;
		AbstractGameSceneRef sceneRef = gameScene.getRef();
		List<SceneMonsterRefData> monsterRefDatas = sceneRef.getMonsterRefDatas();
		MonsterMgrComponent monsterManager = gameScene.getMonsterMgrComponent();
		for (SceneMonsterRefData refData : monsterRefDatas) {
			int monsterNum = refData.getMonsterCount();
			List<Position> positionList = RefreshMonsterRefData.getCenterPositionsCount(refData.getArea(), SceneAOILayer.AOIGRID_MULTIPLE);
			int gridCount = positionList.size();
			Preconditions.checkArgument(!(gridCount < monsterNum), "配置表：刷怪点数少于怪物数,[monsterRefID]:" + refData.getMonsterRefId() + "  ,[gridCount] = " + gridCount
					+ "  ,[monsterNum] = " + monsterNum + "  ,[sceneRefId] = " + sceneRef.getId());
			for (int i = 0; i < monsterNum && i < positionList.size(); i++) {
				Position position = getPositionNotNull(gameScene, i, positionList);
				if (position == null) {
					logger.error("This Area Hasn't Unblocked Position." + "   gameSceneRefId:" + gameScene.getRef().getId());
					continue;
				}

				Collection<FightSprite> spriteCollection = GameSceneHelper.getFightSprites(gameScene, position, 0);
				int size = spriteCollection.size();
				if (size != 0 && logger.isDebugEnabled()) {
					logger.debug("Monster has The same birth Position.MonsterRefId:" + refData.getMonsterRefId() + "  Position:" + position + "  Size:" + size + "  SceneRefId:"
							+ gameScene.getRef().getId());
				}

				Monster monster = monsterManager.createMonster(refData.getMonsterRefId());
				monster.setMonsterRefreshTime(refData.getRefreshTime() * 1000);
				monster.setMonsterRefreshType(refData.getRefreshType());
				monster.setTimingRefresh(refData.getTimingRefresh());
				count++;

				if (logger.isDebugEnabled()) {
					logger.debug("createScene Load Monster " + monster);
				}
				if (WorldBoss.isWorldThief(monster)) {
					if (!WorldBoss.isShouldRefreshThief(gameScene)) {
						monster.getBirthPosition().setPosition(position.getX(), position.getY());
						WorldBoss.putThief(gameScene, monster);
						continue;
					}
					WorldBoss.putThief(gameScene, monster);
				}

				monsterManager.enterWorld(monster, gameScene, position.getX(), position.getY());
				GameSceneHelper.checkInAOIGridCenter(position);
				MonsterAIHelper.checkMonsterHasUniquePosition(monster);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("load monster count=" + count);
		}
	}

	private void loadNpc(GameScene gameScene) {
		// 加载NPC和采集物数据
		logger.debug("loadNpc");
		int count = 0;
		AbstractGameSceneRef sceneRef = gameScene.getRef();
		List<SceneNpcRefData> SceneNpcRefDatas = sceneRef.getNpcRefDatas();
		PluckMgrComponent pluckMgrComponent = gameScene.getPluckMgrComponent();
		NpcMgrComponent npcMgrComponent = gameScene.getNpcMgrComponent();
		for (SceneNpcRefData refData : SceneNpcRefDatas) {
			if (refData.getNpcRefId().contains("collect")) { // 加载采集物数据
				Pluck pluck = pluckMgrComponent.createpluck(refData.getNpcRefId());
				int x = GameSceneHelper.getCenterCoordinate(refData.getX());
				int y = GameSceneHelper.getCenterCoordinate(refData.getY());
				pluckMgrComponent.enterWorld(pluck, gameScene, x, y);
			} else { // 加载NPC数据
				Npc npc = npcMgrComponent.createNpc(refData.getNpcRefId());
				int x = GameSceneHelper.getCenterCoordinate(refData.getX());
				int y = GameSceneHelper.getCenterCoordinate(refData.getY());
				npcMgrComponent.enterWorld(npc, gameScene, x, y);
			}
			count++;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("load npc count=" + count);
		}
	}

	public SceneActivityMgrInterface getSceneActivityMgr() {
		return sceneActivityMgr;
	}

	public void setSceneActivityMgr(SceneActivityMgrInterface sceneActivityMgr) {
		this.sceneActivityMgr = sceneActivityMgr;
	}

}
