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
package sophia.mmorpg.player.scene;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIEnter_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIJumpTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOILeave_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIMoveTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIPropertyChg_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIStopMove_GE;
import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.aoi.PathInfo;
import sophia.mmorpg.base.sprite.aoi.PositionInfo;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.base.sprite.aoi.SpriteInfo;
import sophia.mmorpg.base.sprite.aoi.SpritePathComponent;
import sophia.mmorpg.base.sprite.aoi.SpriteSceneProperty;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.scene.event.G2C_Scene_AOI;
import sophia.mmorpg.pluck.Pluck;

public class PlayerAOIComponent extends SpriteAOIComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerAOIComponent.class);
	
	private static int maxSyncCacheCount = 50;
	// 最大同步精灵个数
	private static final int MaxSyncSpriteCount = 100;
	// 最大同步移除精灵个数
	private static final int MaxSyncRemoveSpriteCount = 100;
	// 最大同步停止行走个数
	private static final int MaxSyncStopMoveCount = 100;
	// 最大同步行走个数
	private static final int MaxSyncMoveToCount = 100;

	private Set<SpriteInfo> removeSet1 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
	private Set<SpriteInfo> addSet1 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
	private Set<SpriteInfo> removeSet2 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
	private Set<SpriteInfo> addSet2 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
	private Set<PathInfo> moveToSet1 = Collections.newSetFromMap(new ConcurrentHashMap<PathInfo, Boolean>());
	private Set<PathInfo> moveToSet2 = Collections.newSetFromMap(new ConcurrentHashMap<PathInfo, Boolean>());
	private Set<PositionInfo> stopMoveSet1 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
	private Set<PositionInfo> stopMoveSet2 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
	private Set<PositionInfo> jumpToSet1 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
	private Set<PositionInfo> jumpToSet2 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
	private Set<SpriteSceneProperty> spriteScenePropertySet1 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteSceneProperty, Boolean>());
	private Set<SpriteSceneProperty> spriteScenePropertySet2 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteSceneProperty, Boolean>());

	private volatile boolean set1 = true;

	private AtomicBoolean isBroadcasting = new AtomicBoolean(false);

	/**
	 * 获取当前没有在操作的RemoveSet
	 * 
	 * @return
	 */
	public final Set<SpriteInfo> getRemoveSet() {
		if (set1) {
			return removeSet2;
		} else {
			return removeSet1;
		}
	}

	/**
	 * 获取当前没有在操作的AddSet
	 * 
	 * @return
	 */
	public final Set<SpriteInfo> getAddSet() {
		if (set1) {
			return addSet2;
		} else {
			return addSet1;
		}
	}

	/**
	 * 获取当前没有在操作的MoveToSet
	 * 
	 * @return
	 */
	public final Set<PathInfo> getMoveToSet() {
		if (set1) {
			return moveToSet2;
		} else {
			return moveToSet1;
		}
	}

	public final Set<PositionInfo> getStopMoveSet() {
		if (set1) {
			return stopMoveSet2;
		} else {
			return stopMoveSet1;
		}
	}

	public Set<PositionInfo> getJumpToSet() {
		if (set1) {
			return jumpToSet2;
		} else {
			return jumpToSet1;
		}
	}

	public Set<SpriteSceneProperty> getSpriteScenePropertySet() {
		if (set1) {
			return spriteScenePropertySet2;
		} else {
			return spriteScenePropertySet1;
		}
	}

	public final boolean isSet1Changed() {
		if (moveToSet1.size() != 0) {
			return true;
		}
		if (spriteScenePropertySet1.size() != 0) {
			return true;
		}
		if (addSet1.size() != 0) {
			return true;
		}
		if (removeSet1.size() != 0) {
			return true;
		}
		if (stopMoveSet1.size() != 0) {
			return true;
		}
		if (jumpToSet1.size() != 0) {
			return true;
		}

		return false;
	}

	public final boolean isSet2Changed() {
		if (moveToSet2.size() != 0) {
			return true;
		}
		if (spriteScenePropertySet2.size() != 0) {
			return true;
		}
		if (addSet2.size() != 0) {
			return true;
		}
		if (removeSet2.size() != 0) {
			return true;
		}
		if (stopMoveSet2.size() != 0) {
			return true;
		}
		if (jumpToSet2.size() != 0) {
			return true;
		}

		return false;
	}

	public final boolean isChanged() {
		if (set1) {
			return isSet2Changed();
		}

		return isSet1Changed();
	}

	public final void switchSet() {
		set1 = !set1;
	}

	public final void clearSet1() {
		removeSet1.clear();
		addSet1.clear();
		moveToSet1.clear();
		stopMoveSet1.clear();
		jumpToSet1.clear();
		spriteScenePropertySet1.clear();
	}

	public final void clearSet2() {
		removeSet2.clear();
		addSet2.clear();
		moveToSet2.clear();
		stopMoveSet2.clear();
		jumpToSet2.clear();
		spriteScenePropertySet2.clear();
	}
	
	public final void reset() {
		removeSet1 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
		addSet1 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
		removeSet2 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
		addSet2 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteInfo, Boolean>());
		moveToSet1 = Collections.newSetFromMap(new ConcurrentHashMap<PathInfo, Boolean>());
		moveToSet2 = Collections.newSetFromMap(new ConcurrentHashMap<PathInfo, Boolean>());
		stopMoveSet1 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
		stopMoveSet2 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
		jumpToSet1 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
		jumpToSet2 = Collections.newSetFromMap(new ConcurrentHashMap<PositionInfo, Boolean>());
		spriteScenePropertySet1 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteSceneProperty, Boolean>());
		spriteScenePropertySet2 = Collections.newSetFromMap(new ConcurrentHashMap<SpriteSceneProperty, Boolean>());
	}

	public final void clearSet() {
		if (set1) {
			clearSet2();
		} else {
			clearSet1();
		}
	}

	public final void removeSprite(final SpriteInfo spriteInfo) {
		if (set1) {
			addSet1.remove(spriteInfo);
			removeSet1.add(spriteInfo);
		} else {
			addSet2.remove(spriteInfo);
			removeSet2.add(spriteInfo);
		}
	}

	public final void addSprite(final SpriteInfo spriteInfo) {
		if (set1) {
			removeSet1.remove(spriteInfo);
			addSet1.add(spriteInfo);
		} else {
			removeSet2.remove(spriteInfo);
			addSet2.add(spriteInfo);
		}
	}

	public final void addMoveTo(final PathInfo pathInfo) {
		if (set1) {
			moveToSet1.add(pathInfo);
		} else {
			moveToSet2.add(pathInfo);
		}
	}

	public final void addStopMove(final PositionInfo positionInfo) {
		if (set1) {
			stopMoveSet1.add(positionInfo);
		} else {
			stopMoveSet2.add(positionInfo);
		}
	}

	public final void addJumpTo(final PositionInfo positionInfo) {
		if (set1) {
			jumpToSet1.add(positionInfo);
		} else {
			jumpToSet2.add(positionInfo);
		}
	}

	public final void addSpriteSceneProperty(final SpriteSceneProperty spriteSceneProperty) {
		if (set1) {
			spriteScenePropertySet1.add(spriteSceneProperty);
		} else {
			spriteScenePropertySet2.add(spriteSceneProperty);
		}
	}
	
	public final void cycleAddSpriteSet(List<SpriteInfo> spriteInfoAddList) {
		if (set1) {
			addSet1.addAll(spriteInfoAddList);
		} else {
			addSet2.addAll(spriteInfoAddList);
		}
	} 
	
	public final void cycleRemoveSpriteSet(List<SpriteInfo> spriteInfoTempList) {
		if (set1) {
			removeSet1.addAll(spriteInfoTempList);
		} else {
			removeSet2.addAll(spriteInfoTempList);
		}
	}
	
	public final void cycleMoveToSet(List<PathInfo> pathInfoTempList) {
		if (set1) {
			moveToSet1.addAll(pathInfoTempList);
		} else {
			moveToSet2.addAll(pathInfoTempList);
		}
	}
	
	public final void cycleStopMoveSet(List<PositionInfo> positionInfoTempList) {
		if (set1) {
			stopMoveSet1.addAll(positionInfoTempList);
		} else {
			stopMoveSet2.addAll(positionInfoTempList);
		}
	}

	@Override
	public void ready() {
		clearSet();
		addInterGameEventListener(AOIEnter_GE_Id);
		addInterGameEventListener(AOILeave_GE_Id);
		addInterGameEventListener(AOIMoveTo_GE_Id);
		addInterGameEventListener(SceneTick_GE_Id);
		addInterGameEventListener(AOIStopMove_GE_Id);
		addInterGameEventListener(AOIJumpTo_GE_Id);
		addInterGameEventListener(AOIPropertyChg_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(AOIEnter_GE_Id);
		removeInterGameEventListener(AOILeave_GE_Id);
		removeInterGameEventListener(AOIMoveTo_GE_Id);
		removeInterGameEventListener(SceneTick_GE_Id);
		removeInterGameEventListener(AOIStopMove_GE_Id);
		removeInterGameEventListener(AOIJumpTo_GE_Id);
		removeInterGameEventListener(AOIPropertyChg_GE_Id);
		super.suspend();
	}
	
	private void enumAddSprite(G2C_Scene_AOI rep) {
		int count = 0;
		Set<SpriteInfo> addSet = getAddSet();
		if (addSet.isEmpty()) {
			return;
		}
		
		List<SpriteInfo> spriteTempList = rep.getSpriteTempList();
		List<Player> playerList = rep.getPlayerList();
		List<Monster> monsterList = rep.getMonsterList();
		List<Loot> lootList = rep.getLootList();
		List<Pluck> pluckList = rep.getPluckList();
		List<Sprite> otherSpriteList = rep.getOtherSpriteList();
		
		Iterator<SpriteInfo> iterator = addSet.iterator();
		while (iterator.hasNext()) {
			SpriteInfo spriteInfo = iterator.next();
			if (count >= MaxSyncSpriteCount) {
				spriteTempList.add(spriteInfo);
				continue;
			}
			
			Sprite sprite = getCrtSceneSprite(spriteInfo);
			if (sprite == null) {
				continue;
			}
			
			byte spriteType = spriteInfo.getSpriteType();
			switch (spriteType) {
			case SpriteTypeDefine.GameSprite_Player:
				playerList.add((Player) sprite);
				break;
			case SpriteTypeDefine.GameSprite_Monster:
				monsterList.add((Monster) sprite);
				break;
			case SpriteTypeDefine.GameSprite_Loot:
				lootList.add((Loot) sprite);
				break;
			case SpriteTypeDefine.GameSprite_Pluck:
				pluckList.add((Pluck) sprite);
				break;
			default:
				otherSpriteList.add(sprite);
				break;
			}

			++count;
		}

		addSet.clear();
		if (!spriteTempList.isEmpty()) {
			cycleAddSpriteSet(spriteTempList);
		}
	}
	
	private void enumRemoveSprite(G2C_Scene_AOI rep) {
		Set<SpriteInfo> removeSet = getRemoveSet();
		if (removeSet.isEmpty()) {
			return;
		}
		
		List<SpriteInfo> spriteTempList = rep.getSpriteTempList();
		spriteTempList.clear();
		
		List<SpriteInfo> removeList = rep.getRemoveList();
		int count = MaxSyncRemoveSpriteCount;
		Iterator<SpriteInfo> iterator = removeSet.iterator();
		while (iterator.hasNext()) {
			SpriteInfo spriteInfo = iterator.next();
			if (count > 0) {
				--count; 
				removeList.add(spriteInfo);
				continue;
			}
		
			spriteTempList.add(spriteInfo);
		}
		
		removeSet.clear();
		if (!spriteTempList.isEmpty()) {
			cycleRemoveSpriteSet(spriteTempList);
		}
	}
	
	private void enumPathInfo(G2C_Scene_AOI rep) {
		Set<PathInfo> moveToSet = getMoveToSet();
		if (moveToSet.isEmpty()) {
			return;
		}
		
		List<PathInfo> pathInfoTempList = rep.getPathInfoTempList();
		List<PathInfo> moveToList = rep.getMoveToList();
		int count = MaxSyncMoveToCount;
		int maxCount = getMaxSyncCacheCount();
		Iterator<PathInfo> iterPath = moveToSet.iterator();
		while (iterPath.hasNext()) {
			PathInfo pathInfo = iterPath.next();
			SpriteInfo spriteInfo = pathInfo.getSpriteInfo();
			Sprite sprite = getCrtSceneSprite(spriteInfo);
			if (sprite == null) {
				continue;
			}
			
			if (count > 0) {
				--count;
				moveToList.add(pathInfo);
				continue;
			}
			
			if (maxCount-- > 0) {
				pathInfoTempList.add(pathInfo);
			}
		}
		
		moveToSet.clear();
		if (!pathInfoTempList.isEmpty()) {
			cycleMoveToSet(pathInfoTempList);
		}
	}
	
	private void enumStopMove(G2C_Scene_AOI rep) {
		Set<PositionInfo> stopMoveSet = getStopMoveSet();
		if (stopMoveSet.isEmpty()) {
			return;
		}
		
		List<PositionInfo> positionInfoTempList = rep.getPositionInfoTempList();
		positionInfoTempList.clear();
		
		List<PositionInfo> stopMoveList = rep.getStopMoveList();
		int count = MaxSyncStopMoveCount;
		int maxCount = getMaxSyncCacheCount();
		Iterator<PositionInfo> iterator = stopMoveSet.iterator();
		while (iterator.hasNext()) {
			PositionInfo posInfo = iterator.next();
			SpriteInfo spriteInfo = posInfo.getSpriteInfo();
			Sprite sprite = getCrtSceneSprite(spriteInfo);
			if (sprite == null) {
				continue;
			}
			
			if (count > 0) {
				--count;
				stopMoveList.add(posInfo);
				continue;
			}
			
			if (maxCount-- > 0) {
				positionInfoTempList.add(posInfo);
			}
		}
		
		stopMoveSet.clear();
		if (!positionInfoTempList.isEmpty()) {
			cycleStopMoveSet(positionInfoTempList);
		}
	}
	
	private boolean checkSameGameScene(GameScene gameScene) {
		return getConcreteParent().getCrtScene().equals(gameScene);
	}
	
	private Sprite getCrtSceneSprite(SpriteInfo spriteInfo) {
		Player player = getConcreteParent();
		if (!player.isSceneReady()) {
			return null;
		}
		
		GameScene crtScene = player.getCrtScene();
		byte spriteType = spriteInfo.getSpriteType();
		String spriteId = spriteInfo.getSpriteId();
		switch (spriteType) {
		case SpriteTypeDefine.GameSprite_Player:
			return crtScene.getPlayerMgrComponent().getPlayer(spriteId);
		case SpriteTypeDefine.GameSprite_Monster:
			return crtScene.getMonsterMgrComponent().getMonster(spriteId);
		case SpriteTypeDefine.GameSprite_Loot:
			return crtScene.getLootMgrComponent().getLoot(spriteId);
		case SpriteTypeDefine.GameSprite_Pluck:
			return crtScene.getPluckMgrComponent().getPluck(spriteId);
		default:
			return null;
		}
	}
	
	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(SceneTick_GE_Id)) {
			broadcastImmediately();
			return;
		} 
		
		if (event.isId(AOIMoveTo_GE_Id)) {
			AOIMoveTo_GE moveToGameEvent = (AOIMoveTo_GE) event.getData();
			if (!checkSameGameScene(moveToGameEvent.getSprite().getCrtScene())) {
				return;
			}
			addMoveTo(moveToGameEvent.getPathInfo());
			if (logger.isDebugEnabled()) {
				logger.debug("Notify AOIMoveTo_GE " + moveToGameEvent.getPathInfo());
			}
		} else if (event.isId(AOIStopMove_GE_Id)) {
			AOIStopMove_GE stopMoveGameEvent = (AOIStopMove_GE) event.getData();
			if (!checkSameGameScene(stopMoveGameEvent.getSprite().getCrtScene())) {
				return;
			}
			addStopMove(stopMoveGameEvent.getPositionInfo());
			if (logger.isDebugEnabled()) {
				logger.debug("Notify AOIStopMove_GE " + stopMoveGameEvent.getPositionInfo());
			}
		} else if (event.isId(AOIEnter_GE_Id)) {
			AOIEnter_GE enterGameEvent = (AOIEnter_GE) event.getData();
			Sprite sprite = enterGameEvent.getSprite();
			if (!checkSameGameScene(sprite.getCrtScene())) {
				return;
			}
			
			addSprite(enterGameEvent.getSpriteInfo());
			if (logger.isDebugEnabled()) {
				logger.debug("Notify AOIEnter_GE " + sprite);
			}
			SpritePathComponent<?> pathComponent = sprite.getPathComponent();
			if (pathComponent != null) {
				PathInfo pathInfo = pathComponent.getPathInfo();
				if (pathInfo != null) {
					addMoveTo(pathInfo);
				}
			}
		} else if (event.isId(AOILeave_GE_Id)) {
			AOILeave_GE leaveGameEvent = (AOILeave_GE) event.getData();
			Sprite sprite = leaveGameEvent.getSprite();
			removeSprite(leaveGameEvent.getSpriteInfo());
			if (logger.isDebugEnabled()) {
				logger.debug("Notify AOILeave_GE " + sprite);
			}
		} else if (event.isId(AOIJumpTo_GE_Id)) {
			AOIJumpTo_GE jumpToGameEvent = (AOIJumpTo_GE) event.getData();
			addJumpTo(jumpToGameEvent.getPositionInfo());
			if (logger.isDebugEnabled()) {
				logger.debug("Notify AOIJumpTo_GE " + jumpToGameEvent.getPositionInfo().getSpriteInfo().getSpriteName());
			}
		} else if (event.isId(AOIPropertyChg_GE_Id)) {
			AOIPropertyChg_GE propertyChgGameEvent = (AOIPropertyChg_GE) event.getData();
			addSpriteSceneProperty(propertyChgGameEvent.getSpriteSceneProperty());
			if (logger.isDebugEnabled()) {
				logger.debug("Notify AOIPropertyChg_GE " + getSpriteScenePropertySet());
			}
		} else {
			return;
		}
		
//		if (checkBroadcastImmediately()) {
//			broadcastImmediately();
//		}
	}

	@Override
	protected void enterScene(GameScene scene) {
		super.enterScene(scene);
		Player player = getConcreteParent();
		GameScene crtScene = player.getCrtScene();
		if (crtScene != null) {
			crtScene.getPlayerMgrComponent().addPlayer(player);
		}
		
		// 记录下最后的复活场景
		if (scene.getReviveRegion() != null) {
			player.setReviveSceneRefId(scene.getRef().getId());
		}
	}
	
	@Override
	public void leaveScene(GameScene scene) {
		Player player = getConcreteParent();
		GameScene crtScene = player.getCrtScene();
		if (crtScene != null) {
			crtScene.getPlayerMgrComponent().removePlayer(player);
		}

		reset();
		super.leaveScene(scene);
		player.setSceneReady(false);
	}
	
	@Override
	public void broadcastProperty(PropertyDictionary property) {
		//Preconditions.checkState(getConcreteParent().isSceneReady(), "sceneReady is false, can't broadcast " + getConcreteParent());
		// sceneReady is false, can't broadcast
		if (!getConcreteParent().isSceneReady()) {
			return;
		}
		
		super.broadcastProperty(property);
	}
	
	private void broadcastImmediately() {
		if (!getConcreteParent().isSceneReady()) {
			return;
		}
		
		if (!isBroadcasting.compareAndSet(false, true)) {
			return;
		}
		
		boolean firstTry = false;
		if (!isChanged()) {
			switchSet();
		} else {
			firstTry = true;
		}

		do {
			if (!firstTry && !isChanged()) {
				break;
			}

			G2C_Scene_AOI rep = G2C_Scene_AOI.pool.obtain();
			rep.setZiped((byte) 1);
			// 进入视野
			enumAddSprite(rep);

			// 离开视野
			enumRemoveSprite(rep);

			// 移动
			enumPathInfo(rep);

			// 停止移动
			enumStopMove(rep);

			if (!getJumpToSet().isEmpty()) {
				rep.getJumpToList().addAll(getJumpToSet());
			}

			if (!getSpriteScenePropertySet().isEmpty()) {
				rep.getScenePropertyList().addAll(getSpriteScenePropertySet());
			}

			clearSet();

			Player player = getConcreteParent();
			rep.setPlayer(player);
			GameRoot.sendMessage(player.getIdentity(), rep);
			G2C_Scene_AOI.pool.recycle(rep);
		} while (false);

		switchSet();
		isBroadcasting.set(false);
	}
	
	private boolean checkBroadcastImmediately() {
		GameScene crtScene = getConcreteParent().getCrtScene();
		if (crtScene != null && StringUtils.equals(crtScene.getRef().getId(), "S012")) {
			return true;
		}
		
		return false;
	}

	public static int getMaxSyncCacheCount() {
		return maxSyncCacheCount;
	}

	public static void setMaxSyncCacheCount(int maxSyncCacheCount) {
		PlayerAOIComponent.maxSyncCacheCount = maxSyncCacheCount;
	}
}
