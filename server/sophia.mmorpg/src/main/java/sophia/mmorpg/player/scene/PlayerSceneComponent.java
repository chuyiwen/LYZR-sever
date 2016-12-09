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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.DebugUtil;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.base.scene.mgr.NpcMgrComponent;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransOutRegion;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.base.sprite.state.FSMStateHelper;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameInstance.ComeFromScene;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.npc.Npc;
import sophia.mmorpg.npc.ref.JobType;
import sophia.mmorpg.npc.ref.NpcTransfers;
import sophia.mmorpg.npc.ref.SingleTransfer;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerEnterSceneCheckFacade;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.gameEvent.PlayerSameSceneJumpTo_GE;
import sophia.mmorpg.player.gameEvent.PlayerSwitchScene_GE;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.scene.event.C2G_Npc_Transfer;
import sophia.mmorpg.player.scene.event.C2G_Scene_FindSprite;
import sophia.mmorpg.player.scene.event.C2G_Scene_PickUp;
import sophia.mmorpg.player.scene.event.C2G_Scene_Switch;
import sophia.mmorpg.player.scene.event.C2G_Scene_Transfer;
import sophia.mmorpg.player.scene.event.C2G_Use_TransferStone;
import sophia.mmorpg.player.scene.event.G2C_Scene_FightPower_NotEnought;
import sophia.mmorpg.player.scene.event.G2C_Scene_FindSprite;
import sophia.mmorpg.player.scene.event.G2C_Scene_InterruptPluck;
import sophia.mmorpg.player.scene.event.G2C_Scene_Ready;
import sophia.mmorpg.player.scene.event.G2C_Scene_Switch;
import sophia.mmorpg.player.scene.event.SceneEventDefines;
import sophia.mmorpg.pluck.Pluck;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.sceneActivities.SceneActivityHelper;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class PlayerSceneComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(PlayerSceneComponent.class);

	private Player player;
	// 用于判定是否第一次进入世界
	private boolean firstEnterScene;
	// 用于场景切换
	private boolean sceneReady = false;
	// 复活场景
	private String reviveSceneRefId;
	// 记录进入副本/活动场景前的所在普通场景的信息
	private ComeFromScene comeFromScene = new ComeFromScene("", 0, 0);
	
	public static final String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	public static final String PlayerSwitchScene_GE_Id = PlayerSwitchScene_GE.class.getSimpleName();
	public static final String PlayerSameSceneJumpTo_GE_Id = PlayerSameSceneJumpTo_GE.class.getSimpleName();
	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();
	public static final String LeaveWorld_GE_Id = LeaveWorld_GE.class.getSimpleName();

	@Override
	public void ready() {
		setFirstEnterScene(false);
		player = getConcreteParent();
		addActionEventListener(SceneEventDefines.C2G_Scene_Ready);
		addActionEventListener(SceneEventDefines.C2G_Scene_Switch);
		addActionEventListener(SceneEventDefines.C2G_Scene_Transfer);
		addActionEventListener(SceneEventDefines.C2G_Npc_Transfer);
		addActionEventListener(SceneEventDefines.C2G_Scene_PickUp);
		addActionEventListener(SceneEventDefines.C2G_Use_TransferStone);
		addActionEventListener(SceneEventDefines.C2G_Scene_FindSprite);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(SceneEventDefines.C2G_Scene_Ready);
		removeActionEventListener(SceneEventDefines.C2G_Scene_Switch);
		removeActionEventListener(SceneEventDefines.C2G_Scene_Transfer);
		removeActionEventListener(SceneEventDefines.C2G_Npc_Transfer);
		removeActionEventListener(SceneEventDefines.C2G_Scene_PickUp);
		removeActionEventListener(SceneEventDefines.C2G_Use_TransferStone);
		removeActionEventListener(SceneEventDefines.C2G_Scene_FindSprite);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		switch (event.getActionEventId()) {
		case SceneEventDefines.C2G_Scene_Ready:
			handle_Scene_Ready(event);
			break;
		case SceneEventDefines.C2G_Scene_Switch:
			handle_Scene_Switch(event);
			break;
		case SceneEventDefines.C2G_Scene_Transfer:
			handle_Scene_Transfer(event);
			break;
		case SceneEventDefines.C2G_Npc_Transfer:
			handle_Npc_Transfer(event);
			break;
		case SceneEventDefines.C2G_Use_TransferStone:
			handle_Use_TransferStone(event);
			break;
		case SceneEventDefines.C2G_Scene_PickUp:
			handle_Scene_PickUp(event);
			break;
		case SceneEventDefines.C2G_Scene_FindSprite:
			handle_C2G_Scene_FindSprite(event);
			break;
		}
	}
	
	private void handle_Scene_Ready(ActionEventBase event) {
		logger.debug("C2G_Scene_Ready");
		if (player.isSceneReady()) {
			return;
		}

		String sceneRefId = player.getSceneRefId();
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_Scene_Ready " + sceneRefId);
		}

		PlayerAOIComponent aoiComponent = (PlayerAOIComponent) player.getAoiComponent();
		aoiComponent.reset();

		int x = player.getCrtPosition().getX();
		int y = player.getCrtPosition().getY();
		player.getAoiComponent().enterScene(sceneRefId, x, y);
		player.setSceneReady(true);

		// 副本场景ready
		// 判定是否第一次进入世界
		if (isFirstEnterScene() == false) {
			setFirstEnterScene(true);
			sendPlayerEnterWorldSceneReadyGameEvent();
		}

		G2C_Scene_Ready rep = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_Ready);
		GameRoot.sendMessage(player.getIdentity(), rep);
	}
	
	private void handle_Scene_Switch(ActionEventBase event) {
		C2G_Scene_Switch req = (C2G_Scene_Switch) event;
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_Scene_Switch " + req.getSwitchId());
		}
		
		if (!player.isSceneReady()) {
			return;
		}

		if (req.getSwitchId() == 0) {
			return;
		}

		GameScene gameScene = player.getCrtScene();
		if (gameScene == null) {
			return;
		}

		AbstractGameSceneRef srcRef = gameScene.getRef();
		List<SceneTransOutRegion> transOutRegionLst = srcRef.getTransOutRegions();
		SceneTransOutRegion transOut = null;
		for (SceneTransOutRegion transOutRegion : transOutRegionLst) {
			if (transOutRegion.getId() == req.getSwitchId()) {
				transOut = transOutRegion;
				break;
			}
		}

		if (transOut == null) {
			logger.debug("transOut is null");
			return;
		}

		// 误差判定，目前误差范围是一个速度，也就是一秒跑动的距离
		// SceneTerrainGridSquare sceneGridSquare = (SceneTerrainGridSquare)
		// transOut.getRegion();
		// SceneGrid leftUp = sceneGridSquare.getLeftUp();
		// SceneGrid rightDown = sceneGridSquare.getRightDown();
		// int speed = player.getPathComponent().getMoveSpeed();
		// int leftUp_x = leftUp.getColumn() - speed;
		// if (leftUp_x < 0) {
		// leftUp_x = 0;
		// }
		// int leftUp_y = leftUp.getRow() - speed;
		// if (leftUp_y < 0) {
		// leftUp_y = 0;
		// }
		// int rightDown_x = rightDown.getColumn() + speed;
		// if (rightDown_x > gameScene.getAoiLayer().getColumns()) {
		// rightDown_x = gameScene.getAoiLayer().getColumns();
		// }
		// int rightDown_y = rightDown.getRow() + speed;
		// if (rightDown_y > gameScene.getAoiLayer().getRows()) {
		// rightDown_y = gameScene.getAoiLayer().getRows();
		// }
		//
		// int x = player.getCrtPosition().getX();
		// int y = player.getCrtPosition().getY();
		// if (x < leftUp_x || y < leftUp_y || x > rightDown_x || y >
		// rightDown_y) {
		// logger.error("C2G_Scene_Switch Invalid Distance");
		// break;
		// }

		logger.debug("C2G_Scene_Switch Switch Start");
		RuntimeResult switchToByTransId = switchToByTransId(transOut.getTargetSceneRefId(), transOut.getTargetRegionId());
		if (switchToByTransId.isError()) {
			ResultEvent.sendResult(player.getIdentity(), SceneEventDefines.C2G_Scene_Switch, switchToByTransId.getApplicationCode());
		}
	}
	
	private void handle_Scene_Transfer(ActionEventBase event) {
		C2G_Scene_Transfer req = (C2G_Scene_Transfer) event;
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_Scene_Transfer " + req.getSceneRefId() + " " + req.getX() + " " + req.getY());
		}
		
		if (!player.isSceneReady()) {
			return;
		}

		GameScene gameScene = player.getCrtScene();
		AbstractGameSceneRef srcRef = gameScene.getRef();
		if (!SceneActivityHelper.checkTransfer(srcRef)) {
			ResultEvent.sendResult(player.getIdentity(), SceneEventDefines.C2G_Scene_Transfer, MMORPGErrorCode.CODE_TRANSFER_ACTIVITY_BAND_TRANSFER);
			return;
		}

		String dstSceneRefId = req.getSceneRefId();
		AbstractGameSceneRef dstRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(dstSceneRefId);
		if (dstRef == null) {
			return;
		}

		// 副本不让传送
		if (dstRef.getType() == SceneRef.FuBen) {
			return;
		}

		// 非法的坐标
		if (!dstRef.getTerrainLayer().isInMatrixRange(req.getY(), req.getX())) {
			return;
		}

		// 阻挡点
		SceneGrid dstGrid = dstRef.getTerrainLayer().getSceneGrid(req.getY(), req.getX());
		if (dstGrid.isBlocked()) {
			logger.error("C2G_Scene_Transfer Position blocked " + req.getX() + " " + req.getY());
			return;
		}
		
		if (checkTransfer(dstSceneRefId, req.getType())) {
			switchTo(dstSceneRefId, req.getX(), req.getY());
			player.setUseFeixue(false);
		}
	}
	
	private void handle_Npc_Transfer(ActionEventBase event) {
		C2G_Npc_Transfer req = (C2G_Npc_Transfer) event;
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_Npc_Transfer " + req.getNpcRefId() + " " + req.getTargetScene() + " " + req.getTransferInId());
		}
		
		if (!player.isSceneReady()) {
			return;
		}
		
		GameScene gameScene = player.getCrtScene();
		AbstractGameSceneRef srcRef = gameScene.getRef();
		if (!SceneActivityHelper.checkTransfer(srcRef)) {
			ResultEvent.sendResult(player.getIdentity(), SceneEventDefines.C2G_Scene_Transfer, MMORPGErrorCode.CODE_TRANSFER_ACTIVITY_BAND_TRANSFER);
			return;
		}
		
		String sceneId = req.getTargetScene();
		int transferInId = req.getTransferInId();
		String npcRefId = req.getNpcRefId();
		NpcMgrComponent npcMgrComponent = player.getCrtScene().getNpcMgrComponent();
		Npc npc = npcMgrComponent.getNpc(npcRefId);
		if (npc == null) {
			return;
		}
		
		if (!StringUtils.equals(npc.getCrtScene().getRef().getId(), player.getCrtScene().getRef().getId())) {
			ResultEvent.sendResult(player.getIdentity(), SceneEventDefines.C2G_Npc_Transfer, MMORPGErrorCode.CODE_NPC_TRANSFER_NOT_SAME_SCENE);
			return;
		}
		
		/**
		 * 代码回滚，传送这里不加距离判断，让玩家传送 else if
		 * (GameSceneHelper.distance(player.getCrtScene(),
		 * player.getCrtPosition(), npc.getCrtPosition()) > 5) {
		 * ResultEvent.sendResult(player.getIdentity(),
		 * SceneEventDefines.C2G_Npc_Transfer,
		 * MMORPGErrorCode.CODE_NPC_TRANSFER_DISTANCE); break; }
		 **/
		NpcTransfers tranfers = (NpcTransfers) npc.getNpcRef().getNpcJobManager().getNpcJob(JobType.Job_Type_Transfer);
		List<SingleTransfer> transferList = tranfers.getTransferData();
		for (SingleTransfer single : transferList) {
			if (single.getTargetScene().equals(sceneId) && single.getTargetTransIn() == transferInId) {
				switchToByTransId(sceneId, transferInId);
			}
		}
	}
	
	private void handle_Use_TransferStone(ActionEventBase event) {
		C2G_Use_TransferStone req = (C2G_Use_TransferStone) event;
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_Use_TransferStone " + req.getGridId() + " " + req.getTargetSceneId() + " " + req.getTransferInId());
		}
		
		GameScene gameScene = player.getCrtScene();
		AbstractGameSceneRef srcRef = gameScene.getRef();
		if (!SceneActivityHelper.checkTransfer(srcRef)) {
			ResultEvent.sendResult(player.getIdentity(), SceneEventDefines.C2G_Scene_Transfer, MMORPGErrorCode.CODE_TRANSFER_ACTIVITY_BAND_TRANSFER);
			return;
		}
		
		short gridId = req.getGridId();
		String sceneId = req.getTargetSceneId();
		int transferInId = req.getTransferInId();
		Player player = getConcreteParent();
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			ResultEvent.sendResult(player.getIdentity(), req.getActionEventId(), MMORPGErrorCode.CODE_ITEM_USERSLOT);
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_TransferStone error, Illegal Index ");
			}
			return;
		}
		
		Item item = itemBag.getItemBySlot(gridId);
		AbstractGameSceneRef dstRef = (AbstractGameSceneRef) (GameRoot.getGameRefObjectManager().getManagedObject(sceneId));
		if (dstRef == null) {
			logger.error("the scene do not  exist :" + sceneId + "when try to use transferStone");
			return;
		}
		
		SceneTransInRegion transIn = null;
		List<SceneTransInRegion> transInRegionLst = dstRef.getTransInRegions();
		for (SceneTransInRegion transInRegion : transInRegionLst) {
			if (transInRegion.getId() == transferInId) {
				transIn = transInRegion;
				break;
			}
		}
		
		if (transIn == null) {
			logger.error("the transferInId do not match :" + transferInId + "when try to use transferStone");
			return;
		}
		
		if (item == null || !StringUtils.equals(ItemCode.TransferStone, item.getItemRefId())) {
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_TRANSFERSTONE_NOTENOUGH);
			return;
		}
		
		player.getItemBagComponent().removeEventNotify(gridId, 1, ItemOptSource.TransFer);

		switchToByTransId(sceneId, transferInId);
	}
	
	private void handle_Scene_PickUp(ActionEventBase event) {
		
		logger.debug("C2G_Scene_PickUp");
		
		if (!player.isSceneReady()) {
			return;
		}

		C2G_Scene_PickUp req = (C2G_Scene_PickUp) event;
		int size = req.getLootIdList().size();
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_Scene_PickUp Size=" + size);
		}

		if (size == 0 || size > 50) {
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_Scene_PickUp Invalid Size " + size);
			}
			return;
		}

		LootMgrComponent lootMgrComponent = player.getCrtScene().getLootMgrComponent();
		for (String lootId : req.getLootIdList()) {
			Loot loot = lootMgrComponent.getLoot(lootId);
			if (loot == null) {
				continue;
			}

			int code = loot.pickUp(player);
			if (logger.isDebugEnabled()) {
				logger.debug("pickUp " + CodeContext.description(code) + " Loot leftSeconds=" + loot.leftGuardSeconds() + " " + loot.getItemPair() + " " + loot.getItem());
			}
			
			ResultEvent.sendResult(player.getIdentity(), req.getActionEventId(), code);
		}
	}
	
	private void handle_C2G_Scene_FindSprite(ActionEventBase event) {
		logger.debug("C2G_Scene_FindSprite");
		
		if (!player.isSceneReady()) {
			logger.error("C2G_Scene_FindSprite error, sceneReady is false, player=" + player);
			return;
		}
		
		C2G_Scene_FindSprite req = (C2G_Scene_FindSprite) event;
		byte spriteType = req.getSpriteType();
		String charId = req.getCharId();
		GameScene crtScene = player.getCrtScene();
		
		switch (spriteType) {
		case SpriteTypeDefine.GameSprite_Loot:
			if (crtScene.getLootMgrComponent().getLoot(charId) != null) {
				return;
			}
			break;
		case SpriteTypeDefine.GameSprite_Monster:
			if (crtScene.getMonsterMgrComponent().getMonster(charId) != null) {
				return;
			}
			break;
		case SpriteTypeDefine.GameSprite_NPC:
			if (crtScene.getNpcMgrComponent().getNpc(charId) != null) {
				return;
			}
			break;
		case SpriteTypeDefine.GameSprite_Player:
			if (crtScene.getPlayerMgrComponent().getPlayer(charId) != null) {
				return;
			}
			break;
		case SpriteTypeDefine.GameSprite_PlayerAvatar:
			if (crtScene.getOtherSpriteMgrComponent().getSprite(charId) != null) {
				return;
			}
			break;
		case SpriteTypeDefine.GameSprite_Pluck:
			if (crtScene.getPluckMgrComponent().getPluck(charId) != null) {
				return;
			}
			break;
		default:
			logger.error("C2G_Scene_FindSprite error, invalid spriteType=" + spriteType);
			return;
		}
		
		G2C_Scene_FindSprite res = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_FindSprite);
		res.setSpriteType(spriteType);
		res.setCharId(charId);
		res.setCode(MMORPGErrorCode.CODE_SCENE_CANNOT_FINDSPRITE);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	private RuntimeResult isAllowToSwitch(AbstractGameSceneRef dstRef) {
		int sceneOpenLevel = MGPropertyAccesser.getOpenLevel(dstRef.getProperty());
		int sceneOpenFightPower = dstRef.getFightPower();
		if (player.getExpComponent().getLevel() < sceneOpenLevel) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TRANSFER_NOT_ENOUGHT_LEVEL);
		} else if (player.getFightPower() < sceneOpenFightPower) {
			G2C_Scene_FightPower_NotEnought fightPower = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_FightPower_NotEnought);
			fightPower.setFightPower(sceneOpenFightPower);
			GameRoot.sendMessage(player.getIdentity(), fightPower);
			return RuntimeResult.RuntimeError();
		} else if (!FSMStateHelper.canOperation(player)) {
			// 玩家在副本死亡时需要离开副本（特殊情况）
			AbstractGameSceneRef crtSceneRef = player.getCrtScene().getRef();
			if (crtSceneRef.getType() == SceneRef.FuBen && player.isDead()) {
				return RuntimeResult.OK();
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TRANSFER_WRONG_STATE);
		} else {
			return RuntimeResult.OK();
		}
	}

	private boolean checkTransfer(String sceneRefId, byte transforType) {
		Identity identity = getConcreteParent().getIdentity();
		short eventId = SceneEventDefines.C2G_Scene_Transfer;

		switch (transforType) {
			case 1: // 飞鞋
			{
				if (getConcreteParent().isVip()) {
					player.setUseFeixue(true);
					return true;
				}
	
				if (!ItemFacade.removeItem(player, ItemCode.FlyShoes, 1, true, ItemOptSource.TransFer)) {
					ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_ITEM_FEIXIE);
					return false;
				}
				break;
			}
			default:
				return false;
		}

		player.setUseFeixue(true);
		return true;
	}

	private RuntimeResult transferTo(GameScene gameScene, int x, int y) {
		Preconditions.checkNotNull(gameScene);

		// 同一个场景传送
		GameScene crtScene = player.getCrtScene();
		if (crtScene == gameScene) {
			if (!player.isSceneReady()) {
				logger.error("sceneReady is false, can't jumpTo, player=" + player + ", " + DebugUtil.printStack());
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TRANSFER_SCENE_NOT_READY);
			} else if (!player.getPathComponent().jumpTo(x, y)) {
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TRANSFER_WRONG_POSITION);
			}
			
			sendPlayerSameSceneJumpToGameEvent(gameScene,x,y);
			
		} else {
			// 停止行走
			player.getPathComponent().silentStop();
			SpriteAOIComponent<?> aoiComponent = player.getAoiComponent();
			aoiComponent.leaveScene(crtScene);
			int oldX = player.getCrtPosition().getX();
			int oldY = player.getCrtPosition().getY();
			aoiComponent.switchToScene(gameScene, x, y);
			saveComeFromScene(crtScene, oldX, oldY);
			sendPlayerSwitchSceneGameEvent(crtScene, oldX, oldY, player.getCrtScene(), x, y);
			sendChineseModeQuestGameEvent(player.getCrtScene());
		}

		sendSceneSwitchMessageToClient(gameScene.getRef().getId(), x, y);
		return RuntimeResult.OK();
	}
	
	public RuntimeResult transferTo(String dstSceneRefId, int x, int y) {
		SpriteAOIComponent<?> aoiComponent = player.getAoiComponent();
		GameScene gameScene = aoiComponent.getSceneByRefId(dstSceneRefId);
		return transferTo(gameScene, x, y);
	}
	
	public void sendSceneSwitchMessageToClient(final String sceneRefId, final int x, final int y) {
		G2C_Scene_Switch res = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_Switch);
		res.setSceneRefId(sceneRefId);
		res.setX(x);
		res.setY(y);
		GameRoot.sendMessage(player.getIdentity(), res);
	}

	public RuntimeResult switchToByTransId(String dstSceneRefId, int dstTransId) {
		AbstractGameSceneRef dstRef = (AbstractGameSceneRef) (GameRoot.getGameRefObjectManager().getManagedObject(dstSceneRefId));

		// 副本不让传送
		if (dstRef.getType() == SceneRef.FuBen) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TRANSFER_INVALID);
		}

		SceneTransInRegion transIn = null;
		List<SceneTransInRegion> transInRegionLst = dstRef.getTransInRegions();
		for (SceneTransInRegion transInRegion : transInRegionLst) {
			if (transInRegion.getId() == dstTransId) {
				transIn = transInRegion;
				break;
			}
		}

		if (transIn == null) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TRANSFER_WRONG_POSITION);
		}

		SceneGrid sceneGrid = transIn.getRegion().getRandomUnblockedGrid();
		return switchTo(dstSceneRefId, sceneGrid.getColumn(), sceneGrid.getRow());
	}

	public RuntimeResult switchTo(GameScene gameScene, int x, int y) {
		Preconditions.checkNotNull(gameScene);
		AbstractGameSceneRef dstRef = gameScene.getRef();
		if (!SceneActivityHelper.checkEnter(player, dstRef)) {
			return RuntimeResult.ParameterError();
		}
		
		RuntimeResult allowToSwitch = isAllowToSwitch(gameScene.getRef());
		if (allowToSwitch.isError()) {
			return allowToSwitch;
		}
		
		return transferTo(gameScene, x, y);
	}

	public RuntimeResult switchTo(String dstSceneRefId, int x, int y) {
		AbstractGameSceneRef dstRef = (AbstractGameSceneRef) (GameRoot.getGameRefObjectManager().getManagedObject(dstSceneRefId));
		if (!SceneActivityHelper.checkEnter(player, dstRef)) {
			return RuntimeResult.ParameterError();
		}
		
		RuntimeResult allowToSwitch = isAllowToSwitch(dstRef);
		if (allowToSwitch.isError()) {
			return allowToSwitch;
		}

		return transferTo(dstSceneRefId, x, y);
	}
	
	private void saveComeFromScene(GameScene fromScene, int x, int y) {
		if (fromScene == null) {
			return;
		}
		
		int sceneType = fromScene.getRef().getType();
		if (SceneRef.Activity == sceneType || SceneRef.FuBen == sceneType) {
			return;
		}
		
		comeFromScene.setComeFromSceneRefId(fromScene.getRef().getId());
		comeFromScene.setX(x);
		comeFromScene.setY(y);
	}
	
	public final ComeFromScene getComeFromScene() {
		return comeFromScene;
	}
	
	public void goBackComeFromSceneOrGoHome() {
		String comeFromSceneRefId = comeFromScene.getComeFromSceneRefId();
		if (Strings.isNullOrEmpty(comeFromSceneRefId)) {
			goHome();
			return;
		}
		
		int x = comeFromScene.getX();
		int y = comeFromScene.getY();
		if (!PlayerEnterSceneCheckFacade.isValidPosition(comeFromSceneRefId, x, y)) {
			logger.error("goBackComeFromSceneOrGoHome error, invalid position");
			goHome();
			return;
		}

		transferTo(comeFromSceneRefId, x, y);
	}

	private void sendPlayerEnterWorldSceneReadyGameEvent() {
		EnterWorld_SceneReady_GE enterWorld_SceneReady_GE = new EnterWorld_SceneReady_GE(player);
		GameEvent<EnterWorld_SceneReady_GE> event = GameEvent.getInstance(EnterWorld_SceneReady_GE_Id, enterWorld_SceneReady_GE);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}

	private void sendPlayerSwitchSceneGameEvent(GameScene fromScene, int oldX, int oldY, GameScene toScene, int newX, int newY) {
		PlayerSwitchScene_GE playerSwitchScene_GE = new PlayerSwitchScene_GE(player, fromScene, oldX, oldY, toScene, newX, newY);
		GameEvent<PlayerSwitchScene_GE> event = GameEvent.getInstance(PlayerSwitchScene_GE_Id, playerSwitchScene_GE);
		if (player.getSummonMonster() != null) {
			player.getSummonMonster().handleGameEvent(event);
		}
		
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}
	
	private void sendPlayerSameSceneJumpToGameEvent(GameScene scene, int x,int y) {
		PlayerSameSceneJumpTo_GE playerSameSceneJumpTo_GE = new PlayerSameSceneJumpTo_GE(player, scene, x,y);
		GameEvent<PlayerSameSceneJumpTo_GE> event = GameEvent.getInstance(PlayerSameSceneJumpTo_GE_Id, playerSameSceneJumpTo_GE);
		if (player.getSummonMonster() != null) {
			player.getSummonMonster().handleGameEvent(event);
		}
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}

	private void sendChineseModeQuestGameEvent(GameScene toScene) {
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setChineseModeValue(toScene.getRef().getId());
		GameEvent<ChineseModeQuest_GE> event = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}

	public void goHome() {
		SceneRef sceneRef = (SceneRef) GameRoot.getGameRefObjectManager().getManagedObject(reviveSceneRefId);
		SceneGrid sceneGrid = sceneRef.getRandomReviveGrid();
		transferTo(reviveSceneRefId, sceneGrid.getColumn(), sceneGrid.getRow());
	}
	
	public void interruptPluck() {
		Player player = getConcreteParent();
		GameScene crtScene = player.getCrtScene();
		if (crtScene == null) {
			return;
		}
		
		Pluck plucking = crtScene.getPluckMgrComponent().getPlucking(player);
		if (plucking != null) {
			plucking.interruptPluck(player);
			G2C_Scene_InterruptPluck res = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_InterruptPluck);
			GameRoot.sendMessage(player.getIdentity(), res);
		}
	}

	public boolean isSceneReady() {
		return sceneReady;
	}

	public void setSceneReady(boolean sceneReady) {
		this.sceneReady = sceneReady;
	}

	public boolean isFirstEnterScene() {
		return firstEnterScene;
	}

	public void setFirstEnterScene(boolean firstEnterScene) {
		this.firstEnterScene = firstEnterScene;
	}

	public String getReviveSceneRefId() {
		return reviveSceneRefId;
	}

	public void setReviveSceneRefId(String reviveSceneRefId) {
		this.reviveSceneRefId = reviveSceneRefId;
	}
}
