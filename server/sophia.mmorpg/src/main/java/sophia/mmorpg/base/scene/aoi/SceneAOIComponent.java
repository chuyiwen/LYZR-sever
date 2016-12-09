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
package sophia.mmorpg.base.scene.aoi;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.foundation.util.Position;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIEnter_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIJumpTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOILeave_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIMoveTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIMove_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIPropertyChg_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOISilentMove_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIStopMove_GE;
import sophia.mmorpg.base.scene.tick.SceneTick_GE;
import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.aoi.PositionInfo;
import sophia.mmorpg.base.sprite.aoi.SpriteInfo;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.player.Player;

import com.google.common.base.Preconditions;

public final class SceneAOIComponent extends ConcreteComponent<GameScene> {

	private static final Logger logger = Logger.getLogger(SceneAOIComponent.class);

	private static final String SceneTick_GE_Id = SceneTick_GE.class.getSimpleName();
	private static final String AOIEnter_GE_Id = AOIEnter_GE.class.getSimpleName();
	private static final String AOILeave_GE_Id = AOILeave_GE.class.getSimpleName();
	private static final String AOIMove_GE_Id = AOIMove_GE.class.getSimpleName();
	private static final String AOIMoveTo_GE_Id = AOIMoveTo_GE.class.getSimpleName();
	private static final String AOIStopMove_GE_Id = AOIStopMove_GE.class.getSimpleName();
	private static final String AOIJumpTo_GE_Id = AOIJumpTo_GE.class.getSimpleName();
	private static final String AOIPropertyChg_GE_Id = AOIPropertyChg_GE.class.getSimpleName();
	private static final String AOISilentMove_GE_Id = AOISilentMove_GE.class.getSimpleName();

	public SceneAOIComponent() {

	}

	@Override
	public void ready() {
		addInterGameEventListener(SceneTick_GE_Id);
		addInterGameEventListener(AOIEnter_GE_Id);
		addInterGameEventListener(AOILeave_GE_Id);
		addInterGameEventListener(AOIMove_GE_Id);
		addInterGameEventListener(AOIMoveTo_GE_Id);
		addInterGameEventListener(AOIStopMove_GE_Id);
		addInterGameEventListener(AOIJumpTo_GE_Id);
		addInterGameEventListener(AOIPropertyChg_GE_Id);
		addInterGameEventListener(AOISilentMove_GE_Id);
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(SceneTick_GE_Id);
		removeInterGameEventListener(AOIEnter_GE_Id);
		removeInterGameEventListener(AOILeave_GE_Id);
		removeInterGameEventListener(AOIMove_GE_Id);
		removeInterGameEventListener(AOIMoveTo_GE_Id);
		removeInterGameEventListener(AOIStopMove_GE_Id);
		removeInterGameEventListener(AOIJumpTo_GE_Id);
		removeInterGameEventListener(AOIPropertyChg_GE_Id);
		removeInterGameEventListener(AOISilentMove_GE_Id);
	}

	private final void joinPiece(Sprite sprite, List<SceneAOIPiece> pieceList) {
		AOIEnter_GE enterGameEvent = new AOIEnter_GE();
		GameEvent<AOIEnter_GE> ge = GameEvent.getInstance(AOIEnter_GE_Id, enterGameEvent);
		SpriteInfo spriteInfo = new SpriteInfo(sprite);
		int pieceSize = pieceList.size();
		for (int j = 0; j < pieceSize; j++) {
			SceneAOIPiece piece = pieceList.get(j);
			List<SceneAOIGrid> grids = piece.getGridList();
			int size = grids.size();
			for (int i = 0; i < size; i++) {
				SceneAOIGrid sceneAOIGrid = grids.get(i);
				Set<Sprite> spriteSet = sceneAOIGrid.getSpriteSet();
				if (spriteSet == null || spriteSet.isEmpty()) {
					continue;
				}
				for (Sprite sp : spriteSet) {
					enterGameEvent.setSprite(sprite);
					enterGameEvent.setSpriteInfo(spriteInfo);
					sendGameEventToPlayer(sp, ge);
					SpriteInfo spInfo = new SpriteInfo(sp);
					enterGameEvent.setSprite(sp);
					enterGameEvent.setSpriteInfo(spInfo);
					sendGameEventToPlayer(sprite, ge);
				}
			}
		}
		GameEvent.pool(ge);
	}

	private final void removePiece(Sprite sprite, List<SceneAOIPiece> pieceList) {
		AOILeave_GE leaveGameEvent = new AOILeave_GE();
		GameEvent<AOILeave_GE> ge = GameEvent.getInstance(AOILeave_GE_Id, leaveGameEvent);
		SpriteInfo spriteInfo = new SpriteInfo(sprite);
		int pieceSize = pieceList.size();
		for (int j = 0; j < pieceSize; j++) {
			SceneAOIPiece piece = pieceList.get(j);
			List<SceneAOIGrid> grids = piece.getGridList();
			int size = grids.size();
			for (int i = 0; i < size; i++) {
				SceneAOIGrid sceneAOIGrid = grids.get(i);
				Set<Sprite> spriteSet = sceneAOIGrid.getSpriteSet();
				if (spriteSet == null || spriteSet.isEmpty()) {
					continue;
				}
				for (Sprite sp : spriteSet) {
					leaveGameEvent.setSprite(sprite);
					leaveGameEvent.setSpriteInfo(spriteInfo);
					sendGameEventToPlayer(sp, ge);
					SpriteInfo spInfo = new SpriteInfo(sp);
					leaveGameEvent.setSprite(sp);
					leaveGameEvent.setSpriteInfo(spInfo);
					sendGameEventToPlayer(sprite, ge);
				}
			}
		}
		GameEvent.pool(ge);
	}
	
	private final boolean sendGameEventToPlayer(Sprite sprite, GameEvent<?> event) {
		if (sprite.getSpriteType() == SpriteTypeDefine.GameSprite_Player) {
			sendGameEvent(event, sprite.getId());
			return true;
		}
		
		return false;
	}
	
	private final void sendGameEventToOthers(Sprite sprite, GameEvent<?> event, List<SceneAOIPiece> pieceList) {
		int count = MMORPGContext.getMaxBoradcastAOIPlayerCount();
		int pieceSize = pieceList.size();
		for (int j = 0; j < pieceSize; j++) {
			SceneAOIPiece piece = pieceList.get(j);
			List<SceneAOIGrid> grids = piece.getGridList();
			int size = grids.size();
			for (int i = 0; i < size; i++) {
				
				SceneAOIGrid sceneAOIGrid = grids.get(i);
				Set<Sprite> spriteSet = sceneAOIGrid.getSpriteSet();
				if (spriteSet == null || spriteSet.isEmpty()) {
					continue;
				}
				
				for (Sprite sp : spriteSet) {
					if (sp == sprite) {
						continue;
					}
					
					if (!sendGameEventToPlayer(sp, event)) {
						continue;
					}
					
					if (count-- <= 0) {
						return;
					}
				}
			}
		}
	}

	public boolean removeAOISprite(SceneAOIGrid aoiGrid, Sprite sprite) {
		Preconditions.checkArgument(aoiGrid != null && sprite != null);
		if (aoiGrid.removeSprite(sprite)) {
			SceneAOILayer aoiLayer = sprite.getCrtScene().getAoiLayer();
			List<SceneAOIPiece> pieceList = aoiLayer.getPieceSquare(aoiGrid);
			removePiece(sprite, pieceList);
			return true;
		}
		
		return false;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		/**
		 * 1 GameSprite[新看到的GameSprites] 2 GameSprite[以前可以看到，现在看不到的GameSprites]
		 * 3 [GameSprites新看到的]GameSprite 4 [GameSprites以前能看到，现在看不到]GameSprite
		 */

		if (event.isId(SceneTick_GE_Id)) {
			// 场景Tick
			GameScene gameScene = getConcreteParent();
			if (gameScene.getPlayerMgrComponent().hasPlayer()) {
				gameScene.getPlayerMgrComponent().sceneTick(event);
				gameScene.getMonsterMgrComponent().sceneTick(event);
				gameScene.getOtherSpriteMgrComponent().sceneTick(event);
			} else {
				gameScene.getMonsterMgrComponent().clearPerceiveComponentReference();
			}
		} else if (event.isId(AOIEnter_GE_Id)) {
			AOIEnter_GE enterGameEvent = (AOIEnter_GE) event.getData();
			// 角色进入场景，通知周围的玩家，角色进入
			// 玩家进入场景，获取周围的角色
			// send AOIEnter_GE game event to GameSpriteAOI
			Sprite sprite = enterGameEvent.getSprite();
			Position pos = sprite.getCrtPosition();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();
			SceneAOIGrid aoiGrid = layer.getAOIGrid(pos.getX(), pos.getY());
			if (aoiGrid == null) {
				logger.error("Enter Scene Error Position, SceneId:" + getConcreteParent().getId() + " " + pos);
				return;
			} else if (logger.isDebugEnabled()) {
				logger.debug("Enter Scene " + sprite.getName() + " Position " + sprite.getCrtPosition());
			}

			List<SceneAOIPiece> pieceList = layer.getPieceSquare(aoiGrid);
			joinPiece(sprite, pieceList);
			aoiGrid.addSprite(sprite);
		} else if (event.isId(AOILeave_GE_Id)) {
			AOILeave_GE leaveGameEvent = (AOILeave_GE) event.getData();
			// 角色离开场景，通知周围的玩家，角色离开
			// 玩家离开场景，移除周围的角色
			Sprite sprite = leaveGameEvent.getSprite();
			Position pos = sprite.getCrtPosition();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();
			SceneAOIGrid aoiGrid = layer.getAOIGrid(pos.getX(), pos.getY());
			if (aoiGrid == null) {
				logger.error("Leave Scene Error Position, SceneId:" + getConcreteParent().getId() + " " + pos);
				return;
			} else if (logger.isDebugEnabled()) {
				logger.debug("Leave Scene " + sprite.getName() + " Position " + sprite.getCrtPosition());
			}
			removeAOISprite(aoiGrid, sprite);
		} else if (event.isId(AOIMove_GE_Id)) {
			AOIMove_GE moveGameEvent = (AOIMove_GE) event.getData();
			// 角色移动，通知远离方向的玩家，角色离开
			// 角色移动，通知前进方向的玩家，角色进入
			// 玩家移动，移除远离方向的角色
			// 玩家移动，获取前进方向的角色
			Sprite sprite = moveGameEvent.getSprite();
			Position srcPos = moveGameEvent.getSrcPosition();
			Position pos = sprite.getCrtPosition();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();
			SceneAOIGrid srcAoiGrid = layer.getAOIGrid(srcPos.getX(), srcPos.getY());
			SceneAOIGrid dstAoiGrid = layer.getAOIGrid(pos.getX(), pos.getY());
			srcAoiGrid.removeSprite(sprite);
			
			if (layer.getPiece(srcAoiGrid) != layer.getPiece(dstAoiGrid)) {
				List<SceneAOIPiece> pieceList = layer.getRemovePieceSet(srcAoiGrid, dstAoiGrid);
				if (pieceList != null) {
					removePiece(sprite, pieceList);
				}

				pieceList = layer.getJoinPieceSet(srcAoiGrid, dstAoiGrid);
				if (pieceList != null) {
					joinPiece(sprite, pieceList);
				}
			}
			
			dstAoiGrid.addSprite(sprite);
		} else if (event.isId(AOIMoveTo_GE_Id)) {
			AOIMoveTo_GE moveToGameEvent = (AOIMoveTo_GE) event.getData();
			Sprite sprite = moveToGameEvent.getSprite();
			Position pos = sprite.getCrtPosition();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();
			SceneAOIGrid aoiGrid = layer.getAOIGrid(pos.getX(), pos.getY());
			List<SceneAOIPiece> pieceList = layer.getPieceSquare(aoiGrid);
			sendGameEventToOthers(sprite, event, pieceList);
		} else if (event.isId(AOIStopMove_GE_Id)) {
			AOIStopMove_GE stopMoveGameEvent = (AOIStopMove_GE) event.getData();
			PositionInfo posInfo = stopMoveGameEvent.getPositionInfo();
			Sprite sprite = stopMoveGameEvent.getSprite();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();
			Position srcPos = stopMoveGameEvent.getSrcPosition();
			SceneAOIGrid srcAoiGrid = layer.getAOIGrid(srcPos.getX(), srcPos.getY());
			srcAoiGrid.removeSprite(sprite);
			Position stopPos = posInfo.getPosition();
			SceneAOIGrid aoiGrid = layer.getAOIGrid(stopPos.getX(), stopPos.getY());
			
			List<SceneAOIPiece> pieceSquare = layer.getPieceSquare(aoiGrid);
			List<SceneAOIPiece> pieceList = layer.getRemovePieceSet(srcAoiGrid, aoiGrid);
			if (pieceList != null) {
				removePiece(sprite, pieceList);
				pieceSquare.removeAll(pieceList);
			}

			pieceList = layer.getJoinPieceSet(srcAoiGrid, aoiGrid);
			if (pieceList != null) {
				joinPiece(sprite, pieceList);
				pieceSquare.removeAll(pieceList);
			}
			
			aoiGrid.addSprite(sprite);
			sendGameEventToOthers(sprite, event, pieceSquare);
		} else if (event.isId(AOIJumpTo_GE_Id)) {
			AOIJumpTo_GE jumpToGameEvent = (AOIJumpTo_GE) event.getData();
			// 角色移动，通知远离方向的玩家，角色离开
			// 角色移动，通知前进方向的玩家，角色进入
			// 玩家移动，移除远离方向的角色
			// 玩家移动，获取前进方向的角色
			Sprite sprite = jumpToGameEvent.getSprite();
			Position srcPos = jumpToGameEvent.getSrcPosition();
			Position pos = sprite.getCrtPosition();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();

			// 当前的可视区域
			SceneAOIGrid aoiGrid = layer.getAOIGrid(pos.getX(), pos.getY());
			List<SceneAOIPiece> pieceSquare = layer.getPieceSquare(aoiGrid);

			SceneAOIGrid srcAoiGrid = layer.getAOIGrid(srcPos.getX(), srcPos.getY());
			srcAoiGrid.removeSprite(sprite);
			List<SceneAOIPiece> pieceList = layer.getRemovePieceSet(srcAoiGrid, aoiGrid);
			if (pieceList != null) {
				removePiece(sprite, pieceList);
				pieceSquare.removeAll(pieceList);
			}

			pieceList = layer.getJoinPieceSet(srcAoiGrid, aoiGrid);
			if (pieceList != null) {
				joinPiece(sprite, pieceList);
				pieceSquare.removeAll(pieceList);
			}
			
			aoiGrid.addSprite(sprite);
			sendGameEventToOthers(sprite, event, pieceSquare);
		} else if (event.isId(AOIPropertyChg_GE_Id)) {
			AOIPropertyChg_GE propertyChgGameEvent = (AOIPropertyChg_GE) event.getData();
			Sprite sprite = propertyChgGameEvent.getSprite();
			Position pos = sprite.getCrtPosition();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();
			SceneAOIGrid aoiGrid = layer.getAOIGrid(pos.getX(), pos.getY());
			List<SceneAOIPiece> pieceList = layer.getPieceSquare(aoiGrid);
			sendGameEventToOthers(sprite, event, pieceList);
		} else if (event.isId(AOISilentMove_GE_Id)) {
			AOISilentMove_GE silentMoveGameEvent = (AOISilentMove_GE) event.getData();
			Sprite sprite = silentMoveGameEvent.getSprite();
			Position srcPos = silentMoveGameEvent.getSrcPosition();
			Position pos = sprite.getCrtPosition();
			SceneAOILayer layer = getConcreteParent().getAoiLayer();
			SceneAOIGrid aoiGrid = layer.getAOIGrid(srcPos.getX(), srcPos.getY());
			if (logger.isDebugEnabled()) {
				logger.debug("AOISilentMove_GE src " + aoiGrid + " " + srcPos);
			}
			aoiGrid.removeSprite(sprite);
			aoiGrid = layer.getAOIGrid(pos.getX(), pos.getY());
			aoiGrid.addSprite(sprite);
			if (logger.isDebugEnabled()) {
				logger.debug("AOISilentMove_GE dst " + aoiGrid + " " + pos);
			}
			
			if (sprite instanceof Player) {
				Player player = (Player)sprite;
				player.cancelState(PluckingState.PluckingState_Id);
			}
		}
	}
}
