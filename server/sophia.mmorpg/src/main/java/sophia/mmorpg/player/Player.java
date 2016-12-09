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
package sophia.mmorpg.player;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.game.utils.eventBuf.IDoer;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.PlayerMailComponent;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent;
import sophia.mmorpg.base.sprite.state.FSMStateFactory;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.base.sprite.state.global.PKState;
import sophia.mmorpg.base.sprite.state.movement.StopState;
import sophia.mmorpg.base.sprite.state.posture.MountedState;
import sophia.mmorpg.base.sprite.state.posture.StandedState;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.ai.PlayerPerceiveComponent;
import sophia.mmorpg.player.chat.PlayerChatComponent;
import sophia.mmorpg.player.equipment.PlayerEquipBodyConponent;
import sophia.mmorpg.player.exp.PlayerExpComponent;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillComponent;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.gameEvent.PlayerFightPowerChange_GE;
import sophia.mmorpg.player.itemBag.PlayerItemBagComponent;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.player.persistence.PlayerSaveComponent;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateSaveComponent;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.player.property.PlayerCoreComponent;
import sophia.mmorpg.player.property.PlayerFightPower;
import sophia.mmorpg.player.property.event.G2C_Player_Attribute;
import sophia.mmorpg.player.property.event.PlayerEventDefines;
import sophia.mmorpg.player.quest.PlayerQuestComponent;
import sophia.mmorpg.player.ref.PlayerProfessionRef;
import sophia.mmorpg.player.scene.PlayerAOIComponent;
import sophia.mmorpg.player.scene.PlayerPathComponent;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.player.scene.event.G2C_Scene_State_Change;
import sophia.mmorpg.player.scene.event.SceneEventDefines;
import sophia.mmorpg.player.state.PlayerStateMgr;
import sophia.mmorpg.player.team.PlayerTeamComponent;
import sophia.mmorpg.player.worldBossMsg.PlayerWorldBossMsgComponent;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public class Player extends FightSprite implements IDoer {
	public static final String Player_GameSPrite_Type = Player.class.getSimpleName();
	private static final Logger logger = Logger.getLogger(Player.class);
	private PropertyDictionary senceProperty = new PropertyDictionary();
	public static final String PlayerDead_GE_Id = PlayerDead_GE.class.getSimpleName();

	private Identity identity;

	private PlayerProfessionRef playerProfessionRef;

	private PlayerSaveComponent playerSaveComponent;

	private PlayerImmediateSaveComponent playerImmediateSaveComponent;

	private PlayerExpComponent expComponent;

	private PlayerEquipBodyConponent playerEquipBodyConponent;

	private PlayerItemBagComponent itemBagComponent;

	private PlayerQuestComponent playerQuestComponent;

	private PlayerFightSkillComponent playerFightSkillComponent;

	private PlayerMountComponent playerMountComponent;

	private PlayerMoneyComponent playerMoneyComponent;

	private PlayerMailComponent playerMailComponent;

	private PlayerSceneComponent playerSceneComponent;

	private PlayerTeamComponent playerTeamComponent;

	private PlayerChatComponent playerChatComponent;

	private PlayerWorldBossMsgComponent playerWorldBossMsgComponent;

	private PlayerStateMgr playerStateMgr = new PlayerStateMgr(this);
	
	private PlayerFightPower fightPower;
	// 玩家死亡未完成，屏蔽客户端发过来的复活请求
	private volatile boolean deadReady = false;
	
	// 是否正在enterWorld
	private AtomicBoolean isEnterWorlding = new AtomicBoolean(false);
	// 是否正在leaveWorld
	private AtomicBoolean isLeaveWorlding = new AtomicBoolean(false);

	// 初始和平模式
	private short crtAttackModel = 3;

	private long lastHeartbeatTime;
	
	private boolean isUseFeixue = false;
	
	public Player() {

	}

	public void reset() {
		fightSpriteStateMgr.setDefaultMovementState(StopState.StopState_Id);
		fightSpriteStateMgr.setCrtMovementState(fightSpriteStateMgr.getDefaultMovementState());
		fightSpriteStateMgr.setDefaultPostureState(StandedState.StandedState_Id);
		fightSpriteStateMgr.setCrtPostureState(fightSpriteStateMgr.getDefaultPostureState());
		fightSpriteStateMgr.setDefaultActionState(IdleState.IdleState_Id);
		fightSpriteStateMgr.setCrtActionState(fightSpriteStateMgr.getDefaultActionState());
	}

	public Player(PlayerProfessionRef playerProfessionRef) {
		this.playerProfessionRef = playerProfessionRef;
		registComponents();
	}

	@Override
	public String getGameSpriteType() {
		return Player_GameSPrite_Type;
	}

	@Override
	public byte getSpriteType(){
		return SpriteTypeDefine.GameSprite_Player;
	}
	
	public Object[] getPlayerSaveData() {
		return null;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public final PlayerProfessionRef getPlayerProfessionRef() {
		return playerProfessionRef;
	}

	public final void setPlayerProfessionRef(PlayerProfessionRef playerProfessionRef) {
		this.playerProfessionRef = playerProfessionRef;
	}

	public synchronized PlayerExpComponent getExpComponent() {
		return expComponent;
	}

	public final PlayerEquipBodyConponent getPlayerEquipBodyConponent() {
		return playerEquipBodyConponent;
	}

	public final PlayerItemBagComponent getItemBagComponent() {
		return itemBagComponent;
	}

	public final PlayerQuestComponent getPlayerQuestComponent() {
		return playerQuestComponent;
	}

	public final PlayerFightSkillComponent getPlayerFightSkillComponent() {
		return playerFightSkillComponent;
	}

	public final PlayerSaveComponent getPlayerSaveComponent() {
		return playerSaveComponent;
	}

	public PlayerImmediateSaveComponent getPlayerImmediateSaveComponent() {
		return playerImmediateSaveComponent;
	}

	public PlayerMountComponent getPlayerMountComponent() {
		return playerMountComponent;
	}

	public PlayerSceneComponent getPlayerSceneComponent() {
		return playerSceneComponent;
	}

	public PlayerTeamComponent getPlayerTeamComponent() {
		return playerTeamComponent;
	}

	@SuppressWarnings("unchecked")
	public void registComponents() {
		playerSaveComponent = (PlayerSaveComponent) createComponent(PlayerSaveComponent.class);
		playerImmediateSaveComponent = (PlayerImmediateSaveComponent) createComponent(PlayerImmediateSaveComponent.class);
		expComponent = (PlayerExpComponent) createComponent(PlayerExpComponent.class);
		playerEquipBodyConponent = (PlayerEquipBodyConponent) createComponent(PlayerEquipBodyConponent.class);
		itemBagComponent = (PlayerItemBagComponent) createComponent(PlayerItemBagComponent.class);
		playerQuestComponent = (PlayerQuestComponent) createComponent(PlayerQuestComponent.class);
		playerMoneyComponent = (PlayerMoneyComponent) createComponent(PlayerMoneyComponent.class);
		playerSceneComponent = (PlayerSceneComponent) createComponent(PlayerSceneComponent.class);
		playerFightSkillComponent = (PlayerFightSkillComponent) createComponent(PlayerFightSkillComponent.class);
		fightSkillRuntimeComponent = (FightSkillRuntimeComponent<Player>) createComponent(FightSkillRuntimeComponent.class);

		setAoiComponent((PlayerAOIComponent) createComponent(PlayerAOIComponent.class));
		setPathComponent((PlayerPathComponent) createComponent(PlayerPathComponent.class));
		setPerceiveComponent((PlayerPerceiveComponent) createComponent(PlayerPerceiveComponent.class));
		playerMountComponent = (PlayerMountComponent) createComponent(PlayerMountComponent.class);
		playerMailComponent = (PlayerMailComponent) createComponent(PlayerMailComponent.class);
		createComponent(PlayerCoreComponent.class);
		playerTeamComponent = (PlayerTeamComponent) createComponent(PlayerTeamComponent.class);
		playerChatComponent = (PlayerChatComponent) createComponent(PlayerChatComponent.class);
		playerWorldBossMsgComponent = (PlayerWorldBossMsgComponent) createComponent(PlayerWorldBossMsgComponent.class);
	}

	public int getFightPower(PropertyDictionary pd) {
		int minPAtk = MGPropertyAccesser.getMinPAtk(pd) < 0 ? 0 : MGPropertyAccesser.getMinPAtk(pd);
		int maxPAtk = MGPropertyAccesser.getMaxPAtk(pd) < 0 ? 0 : MGPropertyAccesser.getMaxPAtk(pd);
		int minMAtk = MGPropertyAccesser.getMinMAtk(pd) < 0 ? 0 : MGPropertyAccesser.getMinMAtk(pd);
		int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd) < 0 ? 0 : MGPropertyAccesser.getMaxMAtk(pd);
		int minTao = MGPropertyAccesser.getMinTao(pd) < 0 ? 0 : MGPropertyAccesser.getMinTao(pd);
		int maxTao = MGPropertyAccesser.getMaxTao(pd) < 0 ? 0 : MGPropertyAccesser.getMaxTao(pd);
		int minPDef = MGPropertyAccesser.getMinPDef(pd) < 0 ? 0 : MGPropertyAccesser.getMinPDef(pd);
		int maxPDef = MGPropertyAccesser.getMaxPDef(pd) < 0 ? 0 : MGPropertyAccesser.getMaxPDef(pd);
		int minMDef = MGPropertyAccesser.getMinMDef(pd) < 0 ? 0 : MGPropertyAccesser.getMinMDef(pd);
		int maxMDef = MGPropertyAccesser.getMaxMDef(pd) < 0 ? 0 : MGPropertyAccesser.getMaxMDef(pd);
		int hp = MGPropertyAccesser.getMaxHP(pd) < 0 ? 0 : MGPropertyAccesser.getMaxHP(pd);
		int mp = MGPropertyAccesser.getMaxMP(pd) < 0 ? 0 : MGPropertyAccesser.getMaxMP(pd);
		int hit = MGPropertyAccesser.getHit(pd) < 0 ? 0 : MGPropertyAccesser.getHit(pd);
		int dodge = MGPropertyAccesser.getDodge(pd) < 0 ? 0 : MGPropertyAccesser.getDodge(pd);
		int pDodge = MGPropertyAccesser.getPDodge(pd) < 0 ? 0 : MGPropertyAccesser.getPDodge(pd);
		int mDodge = MGPropertyAccesser.getMDodge(pd) < 0 ? 0 : MGPropertyAccesser.getMDodge(pd);
		int fortune = MGPropertyAccesser.getFortune(pd) < 0 ? 0 : MGPropertyAccesser.getFortune(pd);
		int crit = MGPropertyAccesser.getCrit(pd) < 0 ? 0 : MGPropertyAccesser.getCrit(pd);
		int critInjure = MGPropertyAccesser.getCritInjure(pd) < 0 ? 0 : MGPropertyAccesser.getCritInjure(pd);
		int pImmunityPer = MGPropertyAccesser.getPImmunityPer(pd) < 0 ? 0 : MGPropertyAccesser.getPImmunityPer(pd);
		int mImmunityPer = MGPropertyAccesser.getMImmunityPer(pd) < 0 ? 0 : MGPropertyAccesser.getMImmunityPer(pd);
		int ignorePDef = MGPropertyAccesser.getIgnorePDef(pd) < 0 ? 0 : MGPropertyAccesser.getIgnorePDef(pd);
		int ignoreMDef = MGPropertyAccesser.getIgnoreMDef(pd) < 0 ? 0 : MGPropertyAccesser.getIgnoreMDef(pd);
		int atkSpeedPer = MGPropertyAccesser.getAtkSpeedPer(pd) < 0 ? 0 : MGPropertyAccesser.getAtkSpeedPer(pd);

		int level = getExpComponent().getLevel();

		double n = 2;
		double levelCo = Math.pow(level / 100.0, n);
		byte professionId = MGPropertyAccesser.getProfessionId(getProperty());
		double fightPower = 0;
		if (PlayerConfig.isEnchanter(professionId)) {
			fightPower = minPAtk * 0.5 + maxPAtk * 0.5 + minMAtk * 2 + maxMAtk * 3 + minTao * 0.5 + maxTao * 0.5 + minPDef * 1.25 + maxPDef * 1.25 + minMDef * 1.25
					+ maxMDef * 1.25 + hp * 0.2 + mp * 0.2 + hit * 100 * levelCo + dodge * 100 * levelCo + pDodge * 25 * levelCo + mDodge * 25 * levelCo + fortune * 500 * levelCo
					+ crit * 50 * levelCo + critInjure * 0.5 + pImmunityPer * 25 * levelCo + mImmunityPer * 25 * levelCo + ignorePDef * 25 * levelCo + ignoreMDef * 25 * levelCo
					+ atkSpeedPer * 50 * levelCo;
		} else if (PlayerConfig.isWarlock(professionId)) {
			fightPower = minPAtk * 0.5 + maxPAtk * 0.5 + minMAtk * 0.5 + maxMAtk * 0.5 + minTao * 2 + maxTao * 3 + minPDef * 1.25 + maxPDef * 1.25 + minMDef * 1.25
					+ maxMDef * 1.25 + hp * 0.2 + mp * 0.2 + hit * 100 * levelCo + dodge * 100 * levelCo + pDodge * 25 * levelCo + mDodge * 25 * levelCo + fortune * 500 * levelCo
					+ crit * 50 * levelCo + critInjure * 0.5 + pImmunityPer * 25 * levelCo + mImmunityPer * 25 * levelCo + ignorePDef * 25 * levelCo + ignoreMDef * 25 * levelCo
					+ atkSpeedPer * 50 * levelCo;
		} else if (PlayerConfig.isWarrior(professionId)) {
			fightPower = minPAtk * 2 + maxPAtk * 3 + minMAtk * 0.5 + maxMAtk * 0.5 + minTao * 0.5 + maxTao * 0.5 + minPDef * 1.25 + maxPDef * 1.25 + minMDef * 1.25
					+ maxMDef * 1.25 + hp * 0.2 + mp * 0.2 + hit * 100 * levelCo + dodge * 100 * levelCo + pDodge * 25 * levelCo + mDodge * 25 * levelCo + fortune * 500 * levelCo
					+ crit * 50 * levelCo + critInjure * 0.5 + pImmunityPer * 25 * levelCo + mImmunityPer * 25 * levelCo + ignorePDef * 25 * levelCo + ignoreMDef * 25 * levelCo
					+ atkSpeedPer * 50 * levelCo;
		}

		return (int) Math.ceil(fightPower);
	}

	public int getFightPower() {
		int fightValue = fightPower.getFightPower();
		sendPlayerFightPowerChangeGameEvent(fightValue);
		return fightValue;
	}
	
	private void sendPlayerFightPowerChangeGameEvent(int fightPower) {
		PlayerFightPowerChange_GE playerFightPowerChangeGe = new PlayerFightPowerChange_GE(fightPower);
		GameEvent<PlayerFightPowerChange_GE> event = GameEvent.getInstance(PlayerFightPowerChange_GE.class.getSimpleName(), playerFightPowerChangeGe);
		handleGameEvent(event);
		GameEvent.pool(event);
	}

	public void notifyPorperty(PropertyDictionary property) {
		G2C_Player_Attribute res = MessageFactory.getConcreteMessage(PlayerEventDefines.G2C_Player_Attribute);
		// fightPower is updated frequently, so it might be fine to append here
		// every call of notifyPorperty

		MGPropertyAccesser.setOrPutFightValue(property, getFightPower());
		res.setProperty(property);
		GameRoot.sendMessage(getIdentity(), res);
		if (logger.isDebugEnabled()) {
			logger.debug("notifyPorperty: " + res + ", player=" + this);
		}
	}

	@Override
	public boolean modifyHP(final FightSprite attacker, final int hp) {
		byte result = super.changeHP(attacker, hp);
		if (result == MODIFY_HP_FAILURE) {
			return false;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("modifyHP hp=" + hp);
		}

		// 死亡
		if (result == MODIFY_HP_DEAD) {
			PlayerDead_GE playerDeadGE = new PlayerDead_GE();
			playerDeadGE.setAttacker(attacker);
			playerDeadGE.setPlayer(this);
			GameEvent<?> event = GameEvent.getInstance(PlayerDead_GE_Id, playerDeadGE);
			this.handleGameEvent(event);
			attacker.handleGameEvent(event);
			GameEvent.pool(event);
			if (isDead()) {
				setDeadReady(true);
			}
		}

		PropertyDictionary pd = new PropertyDictionary();
		// 注意，这边必须再次拿当前血量，可能法宝导致玩家复活
		MGPropertyAccesser.setOrPutHP(pd, getHP());
		notifyPorperty(pd);
		return true;
	}

	@Override
	public boolean modifyMP(final int mp) {
		if (!super.modifyMP(mp)) {
			return false;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("modifyMP mp=" + mp);
		}

		int curMP = getMP();
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMP(pd, curMP);
		notifyPorperty(pd);
		return true;
	}

	@Override
	public boolean applyHP(FightSprite attacker, final int hp) {
		return modifyHP(attacker, hp);
	}

	@Override
	public boolean applyMP(final int mp) {
		return modifyMP(mp);
	}

	/**
	 * 回城
	 */
	public void goHome() {
		playerSceneComponent.goHome();
	}

	/**
	 * 复活
	 */
	@Override
	public void revive() {
		if (!isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("revive failure, player not dead, player=" + this);
			}
			return;
		}
		
		if (!changeState(IdleState.IdleState_Id)) {
			if (logger.isDebugEnabled()) {
				logger.debug("revive failure, can't changeState to IdleState, player=" + this);
			}
			return;
		}
		
		FightPropertyMgr fightPropertyMgr = fightPropertyMgrComponent.getFightPropertyMgr();
		int maxHP = fightPropertyMgr.getSnapshotValueById(MGPropertySymbolDefines.MaxHP_Id);
		int maxMP = fightPropertyMgr.getSnapshotValueById(MGPropertySymbolDefines.MaxMP_Id);
		fightPropertyMgr.setSnapshotValueById(MGPropertySymbolDefines.HP_Id, maxHP);
		fightPropertyMgr.setSnapshotValueById(MGPropertySymbolDefines.MP_Id, maxMP);
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutHP(pd, maxHP);
		MGPropertyAccesser.setOrPutMP(pd, maxMP);
		getAoiComponent().broadcastProperty(pd);
		notifyPorperty(pd);
		getExpComponent().revive();
		setDeadReady(false);
		super.revive();
	}

	@Override
	public String toString() {
		return "Player [name=" + name + ", crtScene=" + crtScene + ", crtPosition=" + crtPosition + ", getId()=" + getId() + "]";
	}

	public PlayerMoneyComponent getPlayerMoneyComponent() {
		return playerMoneyComponent;
	}

	public void setPlayerMoneyComponent(PlayerMoneyComponent playerMoneyComponent) {
		this.playerMoneyComponent = playerMoneyComponent;
	}

	public PlayerMailComponent getPlayerMailComponent() {
		return playerMailComponent;
	}

	public String getReviveSceneRefId() {
		return playerSceneComponent.getReviveSceneRefId();
	}

	public void setReviveSceneRefId(String reviveSceneRefId) {
		playerSceneComponent.setReviveSceneRefId(reviveSceneRefId);
	}

	@Override
	public boolean isEnemyTo(FightSprite fightSprite) {
		if (fightSprite instanceof Monster) {
			Monster monster = (Monster) fightSprite;
			FightSprite owner = monster.getOwner();
			if (owner instanceof Player && owner != null) {
				return !StringUtils.equals(getId(), owner.getId());
			}
			PropertyDictionary pd1 = getProperty();
			PropertyDictionary pd2 = ((Monster) fightSprite).getProperty();

			if (pd1.contains(MGPropertySymbolDefines.UnionName_Id) && pd2.contains(MGPropertySymbolDefines.UnionName_Id)) {
				String unionName1 = MGPropertyAccesser.getUnionName(pd1);
				String unionName2 = MGPropertyAccesser.getUnionName(pd2);
				if (unionName1 != null && unionName2 != null && StringUtils.equals(unionName1, unionName2)) {
					return false;
				}
			}
			
			
		}

		if (StringUtils.equals(getId(), fightSprite.getId())) {
			return false;
		}

		if (fightSprite instanceof Player) {
			Player target = (Player) fightSprite;
			
			if (getCrtAttackModel() == 3) { // 如果是和平模式，那么目标都是右方
				return false;
			} else if (getCrtAttackModel() == 4) { // 组队模式
				boolean isSameTeam = MMORPGContext.playerTeamManagerComponent().isSameTeam(target, this);
				if (isSameTeam) {
					return false;
				}

			} else if (getCrtAttackModel() == 5) { // 公会模式
				String unionName2 = MGPropertyAccesser.getUnionName(target.getProperty());
				String unionName1 = MGPropertyAccesser.getUnionName(getProperty());
				if (unionName1 != null && unionName2 != null && unionName1.equals(unionName2)) {
					return false;
				}

			} else if (getCrtAttackModel() == 6) { // 善恶模式
				int pkValue = MGPropertyAccesser.getPkValue(target.getProperty());
				if (!target.getFightSpriteStateMgr().isState(PKState.PKState_Id) && pkValue < 200) {
					return false;
				}
			} else if (getCrtAttackModel() == 7) { // 全体模式
				return true;
			}
			
		}
		
		return true;
	}

	@Override
	public void broadcastState() {
		G2C_Scene_State_Change res = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_State_Change);
		res.setAimType(this.getSpriteType());
		res.setCharId(getId());
		res.setStateList(getStateList());
		GameSceneHelper.broadcastMessageToAOI(this, res);
	}

	public long getBirthDay() {
		return MGPropertyAccesser.getBirthday(getProperty());
	}

	public long getLastLoginTime() {
		long time = MGPropertyAccesser.getLastLoginTime(getProperty());
		return time;
	}

	public long getLastLoginOutTime() {
		return MGPropertyAccesser.getLastLogoutTime(getProperty());
	}
	
	public boolean isVip() {
		return MGPropertyAccesser.getVipType(getProperty()) != 0;
	}

	public void setVipType(byte vipType) {
		MGPropertyAccesser.setOrPutVipType(getProperty(), vipType);
	}

	public byte getVipType() {
		return MGPropertyAccesser.getVipType(getProperty());
	}

	public PropertyDictionary getSenceProperty() {
		MGPropertyAccesser.setOrPutAtkSpeed(senceProperty, getAttackSpeed());
		return senceProperty;
	}

	public void setSenceProperty(PropertyDictionary senceProperty) {
		this.senceProperty = senceProperty;
	}

	public int getPeriodSaveIndex() {
		return hashCode() & 0x7FFFFFFF;
	}

	public short getCrtAttackModel() {
		return crtAttackModel;
	}

	public void setCrtAttackModel(short crtAttackModel) {
		this.crtAttackModel = crtAttackModel;
	}

	public PlayerChatComponent getPlayerChatComponent() {
		return playerChatComponent;
	}

	public void setPlayerChatComponent(PlayerChatComponent playerChatComponent) {
		this.playerChatComponent = playerChatComponent;
	}

	public PlayerWorldBossMsgComponent getPlayerWorldBossMsgComponent() {
		return playerWorldBossMsgComponent;
	}

	public void setPlayerWorldBossMsgComponent(PlayerWorldBossMsgComponent playerWorldBossMsgComponent) {
		this.playerWorldBossMsgComponent = playerWorldBossMsgComponent;
	}

	public PlayerStateMgr getPlayerStateMgr() {
		return playerStateMgr;
	}

	public void setPlayerStateMgr(PlayerStateMgr playerStateMgr) {
		this.playerStateMgr = playerStateMgr;
	}

	public boolean isOnMount() {
		return this.getFightSpriteStateMgr().isState(FSMStateFactory.getPostureState(MountedState.MountedState_Id));
	}
	
	public boolean isDeadReady() {
		return deadReady;
	}
	
	public void setDeadReady(boolean deadReady) {
		this.deadReady = deadReady;
	}
	
	public boolean isSceneReady() {
		return playerSceneComponent.isSceneReady();
	}

	public void setSceneReady(boolean sceneReady) {
		playerSceneComponent.setSceneReady(sceneReady);
	}

	public void setFightPower(PlayerFightPower fightPower) {
		this.fightPower = fightPower;
	}

	public AtomicBoolean getIsEnterWorlding() {
		return isEnterWorlding;
	}

	public AtomicBoolean getIsLeaveWorlding() {
		return isLeaveWorlding;
	}
	
	public long getLastHeartbeatTime() {
		return lastHeartbeatTime;
	}

	public void setLastHeartbeatTime(long lastHeartbeatTime) {
		this.lastHeartbeatTime = lastHeartbeatTime;
	}
	
	public Connection getConnection() {
		return GameRoot.getSimulatorCommunicationService().getSession(getIdentity());
	}

	public boolean isUseFeixue() {
		return isUseFeixue;
	}

	public void setUseFeixue(boolean isUseFeixue) {
		this.isUseFeixue = isUseFeixue;
	}
}
