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
package sophia.mmorpg.player.property;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.mgr.MonsterMgrComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.event.C2G_Monster_ClearError;
import sophia.mmorpg.monster.event.C2G_Monster_OwnerTransfer;
import sophia.mmorpg.monster.event.G2C_Monster_OwnerTransfer;
import sophia.mmorpg.monster.event.MonsterEventDefines;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.gameEvent.PlayerRevive_GE;
import sophia.mmorpg.player.property.event.C2G_OtherPlayer_Attribute;
import sophia.mmorpg.player.property.event.C2G_OtherPlayer_EquipList;
import sophia.mmorpg.player.property.event.C2G_OtherPlayer_Simple_Attribute;
import sophia.mmorpg.player.property.event.C2G_Player_LeaveWorld;
import sophia.mmorpg.player.property.event.C2G_Player_Revive;
import sophia.mmorpg.player.property.event.G2C_OtherPlayer_Attribute;
import sophia.mmorpg.player.property.event.G2C_OtherPlayer_EquipList;
import sophia.mmorpg.player.property.event.G2C_OtherPlayer_Simple_Attribute;
import sophia.mmorpg.player.property.event.G2C_Player_Revive;
import sophia.mmorpg.player.property.event.PlayerEventDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Strings;

public class PlayerCoreComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(PlayerCoreComponent.class);
	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
	private byte tryReviveCount = 0;

	@Override
	public void ready() {
		addActionEventListener(PlayerEventDefines.C2G_Player_Revive);
		addActionEventListener(PlayerEventDefines.C2G_OtherPlayer_EquipList);
		addActionEventListener(PlayerEventDefines.C2G_OtherPlayer_Attribute);
		addActionEventListener(PlayerEventDefines.C2G_Player_LeaveWorld);
		addActionEventListener(PlayerEventDefines.C2G_OtherPlayer_Simple_Attribute);
		addActionEventListener(MonsterEventDefines.C2G_Monster_OwnerTransfer);
		addActionEventListener(MonsterEventDefines.C2G_Monster_ClearError);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(PlayerEventDefines.C2G_Player_Revive);
		removeActionEventListener(PlayerEventDefines.C2G_OtherPlayer_EquipList);
		removeActionEventListener(PlayerEventDefines.C2G_OtherPlayer_Attribute);
		removeActionEventListener(PlayerEventDefines.C2G_Player_LeaveWorld);
		removeActionEventListener(PlayerEventDefines.C2G_OtherPlayer_Simple_Attribute);
		removeActionEventListener(MonsterEventDefines.C2G_Monster_OwnerTransfer);
		removeActionEventListener(MonsterEventDefines.C2G_Monster_ClearError);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();

		switch (actionEventId) {
		case PlayerEventDefines.C2G_Player_Revive:
			handle_C2G_Player_Revive(event);
			break;
		case PlayerEventDefines.C2G_OtherPlayer_EquipList:
			handle_C2G_OtherPlayer_EquipList(event);
			break;
		case PlayerEventDefines.C2G_OtherPlayer_Attribute:
			handle_C2G_OtherPlayer_Attribute(event);
			break;
		case PlayerEventDefines.C2G_Player_LeaveWorld:
			handle_C2G_Player_LeaveWorld(event);
			break;
		case PlayerEventDefines.C2G_OtherPlayer_Simple_Attribute:
			handle_C2G_OtherPlayer_Simple_Attribute(event);
			break;
		case MonsterEventDefines.C2G_Monster_OwnerTransfer:
			handle_monster_OwnerTransfer((C2G_Monster_OwnerTransfer) event);
			break;
		case MonsterEventDefines.C2G_Monster_ClearError: {
			C2G_Monster_ClearError req = (C2G_Monster_ClearError) event;
			getConcreteParent().getCrtScene().getMonsterMgrComponent().clearErrorByMonsterId(req.getMonsterId());
			break;
		}
		default:
			break;
		}

		super.handleActionEvent(event);
	}
	
	private void handle_C2G_Player_Revive(ActionEventBase event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		
		C2G_Player_Revive req = (C2G_Player_Revive) event;
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_Player_Revive " + req.getReviveType());
		}
		
		Player player = getConcreteParent();
		byte reviveType = req.getReviveType();
		if (reviveType < PlayerReviveType.Revive_GoHome || reviveType > PlayerReviveType.Revive_Talisman) {
			logger.error("C2G_Player_Revive error, invalid reviveType=" + reviveType + ", player=" + player);
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_PLAYER_REVIVE_INVALID);
			return;
		}

		if (!player.isDead()) {
			logger.error("C2G_Player_Revive error, player not dead, player=" + player + ", hp=" + player.getHP());
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_PLAYER_REVIVE_INVALID_NOT_DEAD);
			return;
		}

		if (!player.isDeadReady()) {
			if (tryReviveCount++ < 10) {
				logger.error("C2G_Player_Revive error, dead not ready, player=" + player + ", hp=" + player.getHP() + ", reviveReady=" + player.isReviveReady());
				return;
			}
		}
		if (tryReviveCount >= 10) {
			tryReviveCount = 0;
		}

		if (!player.isSceneReady()) {
			logger.error("C2G_Player_Revive error, sceneReady is false, player=" + player);
			return;
		}

		sendPlayerReviveGameEvent(player, req.getReviveType());
		// 玩家没有复活成功，则不返回
		if (!player.isDead()) {
			G2C_Player_Revive res = MessageFactory.getConcreteMessage(PlayerEventDefines.G2C_Player_Revive);
			GameRoot.sendMessage(player.getIdentity(), res);
		}
	}
	
	private void handle_C2G_OtherPlayer_EquipList(ActionEventBase event) {
		C2G_OtherPlayer_EquipList req = (C2G_OtherPlayer_EquipList) event;
		String charId = req.getCharId();
		Player player = playerManager.getPlayer(charId);
		if (null == player) {
			logger.error("handle_C2G_OtherPlayer_EquipList error, can't find player");
			return;
		}
		
		G2C_OtherPlayer_EquipList res = (G2C_OtherPlayer_EquipList) MessageFactory.getConcreteMessage(PlayerEventDefines.G2C_OtherPlayer_EquipList);
		res.setCharId(charId);
		res.setEquipMgr(player.getPlayerEquipBodyConponent().getEquipMgr());
		GameRoot.sendMessage(event.getIdentity(), res);
	}
	
	private void handle_C2G_OtherPlayer_Attribute(ActionEventBase event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		
		C2G_OtherPlayer_Attribute req = (C2G_OtherPlayer_Attribute) event;
		String charId = req.getCharId();

		if (StringUtils.equals(charId, getConcreteParent().getId())) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_CHAT_CantWatchYourself);
			return;
		}
		
		Player player = playerManager.getPlayer(charId);
		if (null == player) {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家charId = " + charId + "对应的玩家不存在");
			}
			return;
		}

		G2C_OtherPlayer_Attribute res = (G2C_OtherPlayer_Attribute) MessageFactory.getConcreteMessage(PlayerEventDefines.G2C_OtherPlayer_Attribute);
		PropertyDictionaryModifyPhase snapshot = null;
		try {
			snapshot = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotFromPool();
			PropertyDictionary property = snapshot.getPropertyDictionary().clone();

			PropertyDictionary newPd = new PropertyDictionary();
			int level = player.getExpComponent().getLevel();
			long exp = player.getExpComponent().getExp();
			int fightValue = player.getFightPower();
			String name = player.getName();
			byte professionId = player.getProfession();
			byte knight = MGPropertyAccesser.getKnight(player.getProperty());
			byte wingLevel = MGPropertyAccesser.getWingLevel(player.getProperty());
			int wingModleId = MGPropertyAccesser.getWingModleId(player.getProperty());
			int pkValue = MGPropertyAccesser.getPkValue(player.getProperty());
			String unionName = MGPropertyAccesser.getUnionName(player.getProperty());

			newPd.copyFrom(property);
			MGPropertyAccesser.setOrPutLevel(newPd, level);
			MGPropertyAccesser.setOrPutFightValue(newPd, fightValue);
			MGPropertyAccesser.setOrPutExp(newPd, exp);
			MGPropertyAccesser.setOrPutName(newPd, name);
			MGPropertyAccesser.setOrPutProfessionId(newPd, professionId);
			MGPropertyAccesser.setOrPutKnight(newPd, knight);
			MGPropertyAccesser.setOrPutWingLevel(newPd, wingLevel);
			MGPropertyAccesser.setOrPutWingModleId(newPd, wingModleId);
			MGPropertyAccesser.setOrPutPkValue(newPd, pkValue);
			MGPropertyAccesser.setOrPutUnionName(newPd, unionName);

			res.setCharId(req.getCharId());
			res.setProperty(newPd);
			GameRoot.sendMessage(identity, res);
		} finally {
			FightPropertyMgr.recycleSnapshotToPool(snapshot);
		}
	}
	
	private void handle_C2G_Player_LeaveWorld(ActionEventBase event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		
		C2G_Player_LeaveWorld req = (C2G_Player_LeaveWorld) event;
		String charId = req.getCharId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getPlayer(charId);

		if (player == null) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_PLAYER_CANNOT_FIND);
			return;
		}

		playerManager.leaveWorld(charId);

		ResultEvent.sendResult(identity, actionEventId, MMORPGSuccessCode.CODE_SUCCESS);
	}
	
	private void handle_C2G_OtherPlayer_Simple_Attribute(ActionEventBase event) {
		C2G_OtherPlayer_Simple_Attribute req = (C2G_OtherPlayer_Simple_Attribute) event;
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_OtherPlayer_Simple_Attribute charId=" + req.getCharId());
		}

		if (Strings.isNullOrEmpty(req.getCharId())) {
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_OtherPlayer_Simple_Attribute error, playerId=" + req.getCharId());
			}
			return;
		}

		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getPlayer(req.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_OtherPlayer_Simple_Attribute can't find player, playerId=" + req.getCharId());
			}
			return;
		}

		G2C_OtherPlayer_Simple_Attribute res = MessageFactory.getConcreteMessage(PlayerEventDefines.G2C_OtherPlayer_Simple_Attribute);
		res.setCharId(req.getCharId());
		res.setHp(player.getHP());
		res.setMaxHP(player.getHPMax());
		GameRoot.sendMessage(event.getIdentity(), res);
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_OtherPlayer_Simple_Attribute success charId=" + req.getCharId());
		}
	}

	private void handle_monster_OwnerTransfer(C2G_Monster_OwnerTransfer event) {
		GameScene crtScene = this.getConcreteParent().getCrtScene();
		if (crtScene == null) {
			return;
		}
		
		MonsterMgrComponent monsterMgrComponent = crtScene.getMonsterMgrComponent();
		if (monsterMgrComponent == null) {
			return;
		}
		
		String monsterId = event.getMonsterId();
		Monster monster = this.getConcreteParent().getCrtScene().getMonsterMgrComponent().getMonster(monsterId);
		if (monster == null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_MONSTER_NULL);
			return;
		}
		
		FightSprite lootOwner = monster.getPerceiveComponent().getLootOwner();
		if (lootOwner == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("OwnerTransfer forsake loot owner monster " + monster);
			}
			
			G2C_Monster_OwnerTransfer response = new G2C_Monster_OwnerTransfer(monsterId, "");
			GameRoot.sendMessage(event.getIdentity(), response);
			return;
		}
		
		String ownerId = lootOwner.getId();
		G2C_Monster_OwnerTransfer response = new G2C_Monster_OwnerTransfer(monsterId, ownerId);
		GameRoot.sendMessage(event.getIdentity(), response);
		if (logger.isDebugEnabled()) {
			long lastAttackedTime = monster.getPerceiveComponent().getLastAttackedTime();
			long currentTimeMillis = System.currentTimeMillis();
			long diff = currentTimeMillis - lastAttackedTime;
			logger.debug("OwnerTransfer loot owner " + lootOwner + " monster " + monster + " lastAttackedTime " + lastAttackedTime + " currentTimeMillis " + currentTimeMillis
					+ " diff " + diff);
		}
	}

	private void sendPlayerReviveGameEvent(Player player, byte reviveType) {
		PlayerRevive_GE playerRevive_GE = new PlayerRevive_GE(reviveType);
		GameEvent<PlayerRevive_GE> event = GameEvent.getInstance(PlayerRevive_GE.class.getSimpleName(), playerRevive_GE);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}

}
