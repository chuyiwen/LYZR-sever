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
package newbee.morningGlory.mmorpg.player.achievement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.AchievementEventDefines;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.C2G_Achievement_ExchangeMedal;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.C2G_Achievement_GetAllReward;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.C2G_Achievement_GetReward;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.C2G_Achievement_LevlUpMedal;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.C2G_Achievement_List;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.G2C_Achievement_ExchangeMedal;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.G2C_Achievement_Get;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.G2C_Achievement_GetReward;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.G2C_Achievement_LevlUpMedal;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.G2C_Achievement_List;
import newbee.morningGlory.mmorpg.player.achievement.gameEvent.ExchangeOrLevelUpMedal_GE;
import newbee.morningGlory.mmorpg.player.achievement.medal.MGMedalConfig;
import newbee.morningGlory.mmorpg.player.achievement.medal.MGMedalDataRef;
import newbee.morningGlory.mmorpg.player.achievement.persistence.MGAchievementPersistenceObject;
import newbee.morningGlory.mmorpg.player.peerage.gameEvent.MGPeerageLevelUp_GE;
import newbee.morningGlory.mmorpg.player.talisman.gameEvent.HeartLevelUP_GE;
import newbee.morningGlory.mmorpg.player.talisman.gameEvent.TalismanAcquire_GE;
import newbee.morningGlory.mmorpg.player.union.gameEvent.UnionOperateGE;
import newbee.morningGlory.mmorpg.player.union.gameEvent.UnionOperateType;
import newbee.morningGlory.mmorpg.player.wing.actionEvent.MGWingLevelUp_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.KillInCastleWar_GE;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.AchievePointStat;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.equipmentSmith.MGEquipmentSmithComponent;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea;
import sophia.mmorpg.player.equipment.event.EquipmentEventDefines;
import sophia.mmorpg.player.equipment.event.G2C_Equip_Update;
import sophia.mmorpg.player.gameEvent.PlayerFightPowerChange_GE;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.logs.StatEquip;
import sophia.mmorpg.utils.RuntimeResult;

public final class MGPlayerAchievementComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerAchievementComponent.class);

	public static final String Tag = "MGPlayerAchievementComponent";

	private final MGPlayerAchievementMgr achievementMgr = new MGPlayerAchievementMgr();

	private final MGAchievePointMgr achievePointMgr = new MGAchievePointMgr();

	public static final String MEDALSTARTREFID = "equip_10_9000";

	public String firstMedalRefId = MEDALSTARTREFID;

	private MGAchievementPersistenceObject achievementPersistenceObject;

	private NumberRecordMgr numberMgr = new NumberRecordMgr();

	private CastleWarRecord record = new CastleWarRecord();

	private Player player = null;

	private boolean isExchange = false;

	public MGPlayerAchievementComponent() {

	}

	public MGPlayerAchievementMgr getAchievementMgr() {
		return achievementMgr;
	}

	public void ready() {
		player = getConcreteParent();

		addActionEventListener(AchievementEventDefines.C2G_Achievement_List);
		addActionEventListener(AchievementEventDefines.C2G_Achievement_GetReward);
		addActionEventListener(AchievementEventDefines.C2G_Achievement_ExchangeMedal);
		addActionEventListener(AchievementEventDefines.C2G_Achievement_LevlUpMedal);
		addActionEventListener(AchievementEventDefines.C2G_Achievement_GetAllReward);

		addInterGameEventListener(AchievementTypeMacro.KILLMONSTER_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.COLLECTITEM_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.TEAM_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.PEERAGE_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.STRENGTHEN_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.WASHING_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.MOUNT_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.WING_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.MEDAL_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.PRACTICEHEART_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.JoinCastleWar_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.CastleWarEnd_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.KillInCastleWar_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.UnionOperateGE_ID);
		addInterGameEventListener(AchievementTypeMacro.PlayerFightPowerChange_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.TalismanAcquire_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.HeartLevelUP_GE_ID);
		addInterGameEventListener(AchievementTypeMacro.EquipPutOn_GE_ID);
		
	}

	public void suspend() {
		removeActionEventListener(AchievementEventDefines.C2G_Achievement_List);
		removeActionEventListener(AchievementEventDefines.C2G_Achievement_GetReward);
		removeActionEventListener(AchievementEventDefines.C2G_Achievement_ExchangeMedal);
		removeActionEventListener(AchievementEventDefines.C2G_Achievement_LevlUpMedal);
		removeActionEventListener(AchievementEventDefines.C2G_Achievement_GetAllReward);

		removeInterGameEventListener(AchievementTypeMacro.KILLMONSTER_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.COLLECTITEM_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.TEAM_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.PEERAGE_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.STRENGTHEN_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.WASHING_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.MOUNT_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.WING_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.MEDAL_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.PRACTICEHEART_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.JoinCastleWar_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.CastleWarEnd_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.KillInCastleWar_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.UnionOperateGE_ID);
		removeInterGameEventListener(AchievementTypeMacro.PlayerFightPowerChange_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.TalismanAcquire_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.HeartLevelUP_GE_ID);
		removeInterGameEventListener(AchievementTypeMacro.EquipPutOn_GE_ID);

	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		/** 杀怪 */
		if (event.isId(AchievementTypeMacro.KILLMONSTER_GE_ID)) {
			if (logger.isDebugEnabled()) {
				logger.debug("击杀怪物通知");
			}
			numberMgr.addKillMonsterNumber();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.KILLMONSTER, AchievementTypeMacro.KILLMONSTERBEGIN);
			targetNumGameEvent(numberMgr.getKillMonsterNumber(), AchievementTypeMacro.KILLMONSTER, playerAchievementRef);
		}
		
		/** 击杀BOSS */
		if (event.isId(AchievementTypeMacro.KILLMONSTER_GE_ID)) {   // 同样的  杀怪事件
			if (logger.isDebugEnabled()) {
				logger.debug("击杀BOSS通知");
			}
			MonsterDead_GE monsterDead = (MonsterDead_GE) event.getData();
			List<MGPlayerAchievement> achieves = achievementMgr.getAchieveByCategory(AchievementTypeMacro.KILLBOSS);
			String monsterRefId = monsterDead.getMonster().getMonsterRef().getId();
			if (logger.isDebugEnabled()) {
				logger.debug(monsterRefId);
			}
			for (MGPlayerAchievement achieve : achieves) {
				if (StringUtils.equals(monsterRefId, achieve.getAchievementRef().getTargetRefId()))
					return;
			}

			for (Entry<String, String> entry : AchievementTypeMacro.map.entrySet()) {
				if (entry.getValue().equals(monsterRefId)) {
					MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(entry.getKey());
					MGPlayerAchievement achievement = new MGPlayerAchievement(playerAchievementRef);
					achievementMgr.getCrtAchievementList().add(achievement);
					achievementMgr.getAllAchievementIdSet().add(playerAchievementRef.getId());
					achieveNotify(playerAchievementRef.getId());
				}

			}

		}
		
		/** 收集道具 */
		else if (event.isId(AchievementTypeMacro.COLLECTITEM_GE_ID)) {
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.COLLECTITEM, AchievementTypeMacro.COLLECTITEMBEGIN);
			targetNumGameEvent(1, AchievementTypeMacro.COLLECTITEM, playerAchievementRef);
		}
		/** 组队 */
		else if (event.isId(AchievementTypeMacro.TEAM_GE_ID)) {
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.TEAM, AchievementTypeMacro.TEAMBEGIN);
			targetNumGameEvent(1, AchievementTypeMacro.TEAM, playerAchievementRef);
		}
		/** 爵位 */
		else if (event.isId(AchievementTypeMacro.PEERAGE_GE_ID)) {
			MGPeerageLevelUp_GE ge = (MGPeerageLevelUp_GE) event.getData();
			String crtRefId = ge.getRefId();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.PEERAGE, AchievementTypeMacro.PEERAGEBEGIN);
			targetTypeGameEvent(crtRefId, AchievementTypeMacro.PEERAGE, playerAchievementRef);
		}

		else if (event.isId(AchievementTypeMacro.EquipPutOn_GE_ID)) {
			// 检查身上11件装备的最低强化等级
			int allEquipMinStrengthLevel = getAllEquipMinStrengthLevel();
			MGPlayerAchievementRef playerAchievementRef2 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.ALLEQUIPSTRENGH, AchievementTypeMacro.ALLEQUIPSTRENGHBEGIN);
			targetNumGameEvent(allEquipMinStrengthLevel, AchievementTypeMacro.ALLEQUIPSTRENGH, playerAchievementRef2);
		}

		/** 强化 */
		else if (event.isId(AchievementTypeMacro.STRENGTHEN_GE_ID)) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化通知");
			}
			numberMgr.addStrengthCount();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.STRENGTHEN, AchievementTypeMacro.STRENGTHENBEGIN);
			targetNumGameEvent(numberMgr.getStrengthCount(), AchievementTypeMacro.STRENGTHEN, playerAchievementRef);

			// 检查身上11件装备的最低强化等级
			int allEquipMinStrengthLevel = getAllEquipMinStrengthLevel();
			MGPlayerAchievementRef playerAchievementRef2 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.ALLEQUIPSTRENGH, AchievementTypeMacro.ALLEQUIPSTRENGHBEGIN);
			targetNumGameEvent(allEquipMinStrengthLevel, AchievementTypeMacro.ALLEQUIPSTRENGH, playerAchievementRef2);

		}
		/** 洗练 */
		else if (event.isId(AchievementTypeMacro.WASHING_GE_ID)) {
			if (logger.isDebugEnabled()) {
				logger.debug("洗练通知");
			}
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.WASHING, AchievementTypeMacro.WASHINGBEGIN);
			numberMgr.addWashCount();
			targetNumGameEvent(numberMgr.getWashCount(), AchievementTypeMacro.WASHING, playerAchievementRef);
		}
		/** 坐骑 */
		else if (event.isId(AchievementTypeMacro.MOUNT_GE_ID)) {
			if (logger.isDebugEnabled()) {
				logger.debug("坐骑通知");
			}
			MGMountLevelUp_GE ge = (MGMountLevelUp_GE) event.getData();
			String crtRefId = ge.getMountRefId();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.MOUNT, AchievementTypeMacro.MOUNTBEGIN);
			targetTypeGameEvent(crtRefId, AchievementTypeMacro.MOUNT, playerAchievementRef);

		}
		
		/** 翅膀 */
		else if (event.isId(AchievementTypeMacro.WING_GE_ID)) {
			if (logger.isDebugEnabled()) {
				logger.debug("翅膀通知");
			}
			MGWingLevelUp_GE ge = (MGWingLevelUp_GE) event.getData();
			String crtRefId = ge.getWingRefId();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.WING, AchievementTypeMacro.WINGBEGIN);
			targetTypeGameEvent(crtRefId, AchievementTypeMacro.WING, playerAchievementRef);
		}
		/** 勋章 */
		else if (event.isId(AchievementTypeMacro.MEDAL_GE_ID)) {
			ExchangeOrLevelUpMedal_GE ge = (ExchangeOrLevelUpMedal_GE) event.getData();
			String crtRefId = ge.getMedalRefId();
			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.MEDAL, AchievementTypeMacro.MEDALBEGIN);
			targetTypeGameEvent(crtRefId, AchievementTypeMacro.MEDAL, playerAchievementRef);
		}
		/** 参加攻城战 */
		else if (event.isId(AchievementTypeMacro.JoinCastleWar_GE_ID)) {

			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.JOINCASTLEWAR, AchievementTypeMacro.JOINCASTLEWARBEGIN);
			targetNumGameEvent(record.getJoinCastleWarCount(), AchievementTypeMacro.JOINCASTLEWAR, playerAchievementRef);
		}

		/** 攻城战结束 */
		else if (event.isId(AchievementTypeMacro.CastleWarEnd_GE_ID)) {
			// 赢得攻城战
			MGPlayerAchievementRef playerAchievementRef1 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.WINCASTLEWAR, AchievementTypeMacro.WINCASTLEWARBEGIN);
			targetNumGameEvent(record.getWinCastleWarCount(), AchievementTypeMacro.WINCASTLEWAR, playerAchievementRef1);

			// 连续赢得攻城战
			MGPlayerAchievementRef playerAchievementRef2 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.CASTLEWARSTREAK, AchievementTypeMacro.CASTLEWARSTREAKBEGIN);
			targetNumGameEvent(record.getConsecutiveCastelWarCount(), AchievementTypeMacro.CASTLEWARSTREAK, playerAchievementRef2);

		}
		
		/**攻城战击杀*/
		else if (event.isId(AchievementTypeMacro.KillInCastleWar_GE_ID)) {
			KillInCastleWar_GE ge = (KillInCastleWar_GE) event.getData();
			byte killType = ge.getKillType();
			// 击杀攻城战boss
			if (killType == KillInCastleWar_GE.Kill_Boss) {
				MGPlayerAchievementRef playerAchievementRef1 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.CASTLEWARKILLBOSS,
						AchievementTypeMacro.CASTLEWARKILLBOSSBEGING);
				targetNumGameEvent(record.getKillCastleWarBossCount(), AchievementTypeMacro.CASTLEWARKILLBOSS, playerAchievementRef1);
			}

			// 击杀其他公会成员
			if (killType == KillInCastleWar_GE.Kill_Enemy) {
				MGPlayerAchievementRef playerAchievementRef2 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.CASTLEWARKILLENMEY,
						AchievementTypeMacro.CASTLEWARKILLENMEYBEGIN);
				targetNumGameEvent(record.getKillEnemyInCastleWarCount(), AchievementTypeMacro.CASTLEWARKILLENMEY, playerAchievementRef2);
			}
		}

		/**公会操作*/
		else if (event.isId(AchievementTypeMacro.UnionOperateGE_ID)) {
			UnionOperateGE ge = (UnionOperateGE) event.getData();
			byte operateType = ge.getOperateType();
			boolean officer = ge.isOfficer();
			// 加入公会
			if (operateType == UnionOperateType.AddUnion) {
				record.addUnionCount();
				MGPlayerAchievementRef playerAchievementRef1 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.ADDUNION, AchievementTypeMacro.ADDUNIONBEGING);
				targetNumGameEvent(record.getAddUnionCount(), AchievementTypeMacro.ADDUNION, playerAchievementRef1);
			}

			// 创建公会
			if (operateType == UnionOperateType.CreatUnion) {
				record.createUnionCount();
				MGPlayerAchievementRef playerAchievementRef2 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.CREATEUNION, AchievementTypeMacro.CREATEUNIONBEGING);
				targetNumGameEvent(record.getCreateUnionCount(), AchievementTypeMacro.CREATEUNION, playerAchievementRef2);
			}

			// 公会满员
			if (operateType == UnionOperateType.FullUnion) {
				// // 招贤纳士，公会达到满员
				// if (officer) {
				// MGPlayerAchievementRef playerAchievementRef3 =
				// (MGPlayerAchievementRef)
				// targetGameEvent(AchievementTypeMacro.FULLUNION,
				// AchievementTypeMacro.FULLUNIONBEGING);
				// targetNumGameEvent(record.getKillCastleWarBossCount(),
				// AchievementTypeMacro.FULLUNION, playerAchievementRef3);
				// }

				// 群英荟萃，公会达到满员
				record.fullUnionCount();
				MGPlayerAchievementRef playerAchievementRef4 = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.FULLUNION, AchievementTypeMacro.FULLUNIONBEGING);
				targetNumGameEvent(record.getFullUnionCount(), AchievementTypeMacro.FULLUNION, playerAchievementRef4);
			}
		}
		
		/**战力改变*/
		else if (event.isId(AchievementTypeMacro.PlayerFightPowerChange_GE_ID)) {
			PlayerFightPowerChange_GE ge = (PlayerFightPowerChange_GE) event.getData();
			int fightPower = ge.getFightPower();

			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.UPFIGHTPOWER, AchievementTypeMacro.UPFIGHTPOWERBEGIN);
			targetNumGameEvent(fightPower, AchievementTypeMacro.UPFIGHTPOWER, playerAchievementRef);
		}
		
		/**获取法宝*/
		else if (event.isId(AchievementTypeMacro.TalismanAcquire_GE_ID)) {
			TalismanAcquire_GE talismanAcquireGE = (TalismanAcquire_GE) event.getData();
			int totalCount = talismanAcquireGE.getTotalCount();

			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.TALISMANCOUNT, AchievementTypeMacro.TALISMANCOUNTBEGING);
			targetNumGameEvent(totalCount, AchievementTypeMacro.TALISMANCOUNT, playerAchievementRef);
		}

		/**心法升级*/
		else if (event.isId(AchievementTypeMacro.HeartLevelUP_GE_ID)) {
			HeartLevelUP_GE ge = (HeartLevelUP_GE) event.getData();
			int heartLevel = ge.getHeartLevel();

			MGPlayerAchievementRef playerAchievementRef = (MGPlayerAchievementRef) targetGameEvent(AchievementTypeMacro.HEARTLEVEL, AchievementTypeMacro.HEARTLEVELBEGIN);
			targetNumGameEvent(heartLevel, AchievementTypeMacro.HEARTLEVEL, playerAchievementRef);
		}
	}

	private void achieveNotify(String refId) {// 获得的成就的refId
		Identity identity = getConcreteParent().getIdentity();
		G2C_Achievement_Get res = MessageFactory.getConcreteMessage(AchievementEventDefines.G2C_Achievement_Get);
		res.setRefId(refId);
		GameRoot.sendMessage(identity, res);

		MGPlayerAchievementRef ref = (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		MGStatFunctions.getAchievementStat(getConcreteParent(), ref);
	}

	/**
	 * 
	 * @param achieveType
	 *            成就类型
	 * @param achieveBegin
	 *            某一个成就类型的起始refId
	 * @return 下一成就的ref
	 */
	private MGPlayerAchievementRef targetGameEvent(byte achieveType, String achieveBegin) {
		MGPlayerAchievement playerachievement = achievementMgr.getMaxAchieveOfCategory(achieveType);
		MGPlayerAchievementRef nextAchieveRef = null;
		if (null == playerachievement) {
			nextAchieveRef = (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(achieveBegin);
		} else {
			nextAchieveRef = playerachievement.getAchievementRef().getNextAchieveRef();
		}
		return nextAchieveRef;
	}

	/**
	 * 按目标类型
	 * 
	 * @param crtRefId
	 *            当前类型成就的当前refId
	 * @param achieveType
	 *            成就类型
	 * @param ref
	 *            下一成就的ref
	 */
	private void targetTypeGameEvent(String crtRefId, byte achieveType, MGPlayerAchievementRef ref) {
		if (ref == null) {
			return;
		}

		if (achieveType == AchievementTypeMacro.WING || achieveType == AchievementTypeMacro.PEERAGE || achieveType == AchievementTypeMacro.MOUNT) {

			int firstIndex = crtRefId.indexOf("_");

			int secondIndex = crtRefId.indexOf("_", firstIndex + 1);

			int crtLevel = 0;
			int targetLevel = 0;
			if (secondIndex == -1) {
				crtLevel = Integer.parseInt(crtRefId.substring(firstIndex + 1));
				targetLevel = Integer.parseInt(ref.getTargetRefId().substring(firstIndex + 1));
			} else {
				crtLevel = Integer.parseInt(crtRefId.substring(firstIndex + 1, secondIndex));
				targetLevel = Integer.parseInt(ref.getTargetRefId().substring(firstIndex + 1, secondIndex));
			}

			if (targetLevel > crtLevel) {
				return;
			}
		}

		List<MGPlayerAchievementRef> refs = new ArrayList<MGPlayerAchievementRef>();
		while (true) {

			refs.add(ref);
			String targetRefId = ref.getTargetRefId();
			if (StringUtils.equals(targetRefId, crtRefId)) {
				break;
			}

			ref = ref.getNextAchieveRef();
			if (ref == null) {
				break;
			}
		}

		for (MGPlayerAchievementRef achievementRef : refs) {
			completeAchievement(achievementRef);
		}

	}

	private void completeAchievement(MGPlayerAchievementRef ref) {
		if (achievementMgr.getAchieveByRefId(ref.getId()) == null) {
			MGPlayerAchievement nextPlayerAchieve = new MGPlayerAchievement(ref);
			achievementMgr.addCrtAchievementList(nextPlayerAchieve);
			achievementMgr.addAllAchievementIdSet(ref.getId());
			achieveNotify(ref.getId());
		}
	}

	/**
	 * 
	 * @param count
	 *            目标次数
	 * @param achieveType
	 *            成就类型
	 * @param ref
	 *            下一成就ref
	 */
	private synchronized void targetNumGameEvent(int count, byte achieveType, MGPlayerAchievementRef ref) {
		if (ref == null) {
			return;
		}
		int targetNum = ref.getTargetNum(); // 目标数量
		MGPlayerAchievement nextPlayerAchieve = new MGPlayerAchievement(ref);
		if (count >= targetNum) {// 达到该成就
			boolean state = achievementMgr.addCrtAchievementList(nextPlayerAchieve);
			if(state){
				achievementMgr.addAllAchievementIdSet(ref.getId());
				achieveNotify(ref.getId());
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();

		switch (eventId) {
		case AchievementEventDefines.C2G_Achievement_List:
			handleAchievementList((C2G_Achievement_List) event);
			break;
		case AchievementEventDefines.C2G_Achievement_GetReward:
			handleAchievementGetReward((C2G_Achievement_GetReward) event);
			break;
		case AchievementEventDefines.C2G_Achievement_ExchangeMedal:
			handleAchievementExchangeMedal((C2G_Achievement_ExchangeMedal) event);
			break;
		case AchievementEventDefines.C2G_Achievement_LevlUpMedal:
			handleAchievementLevelUpMedal((C2G_Achievement_LevlUpMedal) event);
			break;
		case AchievementEventDefines.C2G_Achievement_GetAllReward:
			handleGetAllReward((C2G_Achievement_GetAllReward) event);
			break;
		default:
			break;
		}
	}

	private void handleAchievementList(C2G_Achievement_List event) {
		if (logger.isDebugEnabled()) {
			logger.debug("============成就列表============");
		}
		List<MGPlayerAchievement> crtAchievementList = achievementMgr.getCrtAchievementList();
		G2C_Achievement_List res = MessageFactory.getConcreteMessage(AchievementEventDefines.G2C_Achievement_List);
		res.setCrtAchievements(crtAchievementList);
		GameRoot.sendMessage(event.getIdentity(), res);
		if (logger.isDebugEnabled()) {
			logger.debug("============return successed!============");
		}
	}

	public void handleGetAllReward(C2G_Achievement_GetAllReward event) {
		if (logger.isDebugEnabled()) {
			logger.debug("============一键领取奖励============");
		}
		List<MGPlayerAchievement> achieves = achievementMgr.getAchieveNotGetReward();
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();

		for (int i = 1; i <= achieves.size(); i++) {
			MGPlayerAchievement achieve = achieves.get(i - 1);
			MGPlayerAchievementRef achievementRef = achieve.getAchievementRef();
			itemPairs.addAll(achievementRef.getItemPairs());
		}

		if (ItemFacade.isItemBagSlotEnough(player, itemPairs)) {
			ItemFacade.addItem(player, itemPairs, ItemOptSource.Achievement);
			for (int i = 1; i <= achieves.size(); i++) {
				achieves.get(i - 1).setSuccess(MGPlayerAchievement.ALREADYGET);
			}
			PlayerImmediateDaoFacade.update(player);
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MGSuccessCode.CODE_SUCCESS);
			
			// 日志
			for(ItemPair itemPair : itemPairs){
				if (itemPair.isAchievement()) {
					MGStatFunctions.achievePointStat(player, AchievePointStat.Add, AchievePointStat.AchieveReward, itemPair.getNumber());
				}
			}
			
		} else {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
			if (logger.isDebugEnabled()) {
				logger.debug("背包没有足够的格子");
			}
		}
	}

	private void handleAchievementGetReward(C2G_Achievement_GetReward event) {
		Identity identityId = event.getIdentity();
		short eventId = event.getActionEventId();
		String refId = event.getRefId();

		MGPlayerAchievement achieve = achievementMgr.getAchieveByRefId(refId);

		if (achieve == null) {
			ResultEvent.sendResult(identityId, eventId, MGErrorCode.CODE_ACHIEVE_NOTCOMPLETE);
			logger.error("error argument! refId = " + refId);
			return;
		}

		if (achieve.getSuccess() == MGPlayerAchievement.ALREADYGET) {
			ResultEvent.sendResult(identityId, eventId, MGErrorCode.CODE_ACHIEVE_ALREADYGETREWARD);
			logger.debug("该成就已经领取过奖励");
			return;
		}
		byte success = -1;
		MGPlayerAchievementRef achievementRef = achieve.getAchievementRef();

		if (ItemFacade.addItemCompareSlot(player, achievementRef.getItemPairs(), ItemOptSource.Achievement).isOK()) {
			success = 1;
			achieve.setSuccess(MGPlayerAchievement.ALREADYGET);
			PlayerImmediateDaoFacade.update(player);

			List<ItemPair> itemPairs = achievementRef.getItemPairs();
			int number = 0;
			for (ItemPair itemPair : itemPairs) {
				if (itemPair.isAchievement()) {
					number += itemPair.getNumber();
				}
			}
			MGStatFunctions.achievePointStat(player, AchievePointStat.Add, AchievePointStat.AchieveReward, number);
		} else {
			success = 0;
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_FULL);
			if (logger.isDebugEnabled()) {
				logger.debug("背包没有足够的格子");
			}
		}

		G2C_Achievement_GetReward res = (G2C_Achievement_GetReward) MessageFactory.getConcreteMessage(AchievementEventDefines.G2C_Achievement_GetReward);
		res.setRefId(refId);
		res.setSuccess(success);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	/** 检测身上是否有勋章 */
	// private boolean hasMedalInBag() {
	// ItemBag itemBag = player.getItemBagComponent().getItemBag();
	//
	//
	// }

	private void handleAchievementExchangeMedal(C2G_Achievement_ExchangeMedal event) {
		Identity identity = event.getIdentity();
		short eventId = event.getActionEventId();

		if (isExchange) {
			logger.debug("已经兑换过!");
			return;
		}

		/** 检索背包或者身上是否已经有勋章 */
		// 身上部位是否有
		String startRefId = firstMedalRefId;
		Item equip = player.getPlayerEquipBodyConponent().getPlayerBody().getBodyArea(PlayerEquipBodyArea.medalBodyId).getEquipment();
		int num = player.getItemBagComponent().getItemBag().getItemNumber(startRefId);

		if (num != 0 || equip != null) {
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_MEDAL_ALREADYOWNMEDAL);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家已经有勋章,兑换错误");
			}
			return;
		}

		MGMedalConfig medalConfig = (MGMedalConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGMedalConfig.MEDALID);
		MGMedalDataRef medalDataRef = medalConfig.getMedalDataRefMap().get(startRefId);

		int achievePoint = medalDataRef.getNeedAchievement();
		int playerAchievePoint = achievePointMgr.getAchievePoint();

		if (playerAchievePoint < achievePoint) {
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_MEDAL_ACHIEVEPOINTNOTENOUGH);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家的成就点数不够:当前玩家的成就点=" + playerAchievePoint + ",需要玩家的成就点数:" + achievePoint);
			}
			return;
		}

		ItemPair itemPair = new ItemPair(startRefId, 1, false);
		RuntimeResult runtimeResult = ItemFacade.addItem(player, itemPair, ItemOptSource.Achievement);

		if (!runtimeResult.isOK()) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_ITEM_FULL);
			logger.debug("背包没有足够的格子");
			return;
		}

		achievePointMgr.subAchievePoint(achievePoint);

		isExchange = true;
		sendGameEventMessage(startRefId);
		MGStatFunctions.achievePointStat(player, AchievePointStat.Delete, AchievePointStat.Buy_medal, achievePoint);
		PlayerImmediateDaoFacade.update(player);
		G2C_Achievement_ExchangeMedal res = MessageFactory.getConcreteMessage(AchievementEventDefines.G2C_Achievement_ExchangeMedal);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	public void handleAchievementLevelUpMedal(C2G_Achievement_LevlUpMedal event) {
		short eventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		byte position = event.getPosition();
		String crtRefId = event.getRefId();// 当前勋章的refId
		String itemId = event.getId();

		if (position != 0 && position != 1) {
			logger.error("error argument! position = " + position);
			return;
		}

		MGMedalConfig medalConfig = (MGMedalConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGMedalConfig.MEDALID);
		MGMedalDataRef crtMedalDataRef = medalConfig.getMedalDataRefMap().get(crtRefId);
		if (crtMedalDataRef == null) {
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_MEDAL_HASNOMEDAL);
			logger.error("error argument! crtRefId = " + crtRefId);
			return;
		}

		PlayerEquipBodyArea bodyArea = player.getPlayerEquipBodyConponent().getPlayerBody().getBodyArea(PlayerEquipBodyArea.medalBodyId);
		Item lowerMedalOnBody = bodyArea.getEquipment();
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		Item lowerMedalInBag = null;

		boolean medaoEquipmentInBody = true;
		if (lowerMedalOnBody == null) {
			medaoEquipmentInBody = false;
		}

		if (position == 0 && lowerMedalOnBody == null) {
			logger.error("error argument! position = " + position);
			return;
		}

		lowerMedalInBag = ItemFacade.getItemById(player, itemId);

		if (position == 1 && lowerMedalInBag == null) {
			logger.error("error argument! position = " + position);
			return;
		}

		String refId = null;
		if (position == 0) {
			refId = lowerMedalOnBody.getItemRefId();
		} else {
			refId = lowerMedalInBag.getItemRefId();
		}

		if (!StringUtils.equals(refId, crtRefId)) {
			logger.error("error argument! crtRefId = " + crtRefId);
			return;
		}

		int num = itemBag.getItemNumber(crtRefId);

		if (num > 1 || (lowerMedalInBag != null && lowerMedalOnBody != null)) {
			logger.debug("玩家拥有超过两个以上的勋章");
			return;
		}

		byte bindStatus = lowerMedalOnBody != null ? lowerMedalOnBody.getBindStatus() : lowerMedalInBag.getBindStatus();

		String nextRefId = crtMedalDataRef.getNextMedalRefId();
		MGMedalDataRef nextMedalDataRef = medalConfig.getMedalDataRefMap().get(nextRefId);
		if (StringUtils.isEmpty(nextRefId)) {
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_MEDAL_ALREADYHIGHEST);
			if (logger.isDebugEnabled()) {
				logger.debug("勋章已经是最高级,升级错误! crtRefId = " + crtRefId);
			}
			return;
		}

		int playerAchievePoint = achievePointMgr.getAchievePoint();
		int needAchievePoint = nextMedalDataRef.getNeedAchievement();
		Item upperMedal = GameObjectFactory.getItem(nextRefId);
		Item oldMedal = lowerMedalOnBody == null ? lowerMedalInBag : lowerMedalOnBody;
		if (needAchievePoint > playerAchievePoint) {
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_MEDAL_ACHIEVEPOINTNOTENOUGH);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家的成就点数不够,升级失败");
			}

			return;
		}

		PropertyDictionary property = oldMedal.getProperty();
		byte strengthenLevel = MGPropertyAccesser.getStrengtheningLevel(property);
		// 替换洗练属性
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(oldMedal);
		PropertyDictionary xiLianPd = itemSmithCompoent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary();
		itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(upperMedal);
		itemSmithCompoent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary().copyFrom(xiLianPd);
		// 强化新勋章到旧勋章的强化等级
		MGEquipmentSmithComponent smithComponent = (MGEquipmentSmithComponent) player.getTagged(MGEquipmentSmithComponent.Tag);
		if (medaoEquipmentInBody) {
			smithComponent.handle_Equip_Streng(upperMedal, strengthenLevel, PlayerEquipBodyArea.medalBodyId, PlayerEquipBodyArea.Left_Position);
		} else {
			int gridId = itemBag.getItemSlot(itemId).getIndex();
			smithComponent.handle_BAG_Streng(upperMedal, strengthenLevel, gridId);
		}

		upperMedal.setBindStatus(bindStatus);

		if (position == 0) {// 勋章装备在身上
			FightPropertyEffectFacade.detachAndSnapshot(player, lowerMedalOnBody.getItemRef().getEffectProperty());
			StatFunctions.EquipStat(player, StatEquip.Deequip, lowerMedalOnBody.getItemRef().getId(), ItemOptSource.equipLevelUp);
			bodyArea.setOrResetEquipment(upperMedal);
			StatFunctions.EquipStat(player, StatEquip.Equip, nextRefId, ItemOptSource.equipLevelUp);
			G2C_Equip_Update equipRes = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Update);
			equipRes.setEventType((byte) 1);
			equipRes.setCount((short) 1);
			equipRes.setBodyId(bodyArea.getId());
			equipRes.setPosition(PlayerEquipBodyArea.Left_Position);
			equipRes.setItem(upperMedal);
			equipRes.setPlayer(player);
			GameRoot.sendMessage(event.getIdentity(), equipRes);

			FightPropertyEffectFacade.attachAndNotify(player, upperMedal.getItemRef().getEffectProperty());
		} else if (position == 1) {// 勋章在背包中
			List<Item> items = new ArrayList<Item>();
			items.add(upperMedal);
			ItemFacade.removeItem(player, crtRefId, 1, true, ItemOptSource.Achievement);
			ItemFacade.addItems(player, items, ItemOptSource.Achievement);

		}

		if (!achievePointMgr.subAchievePoint(needAchievePoint)) {
			logger.error("sub achievePoint error!");
		}

		MGStatFunctions.achievePointStat(player, AchievePointStat.Delete, AchievePointStat.Buy_medal, needAchievePoint);
		sendGameEventMessage(nextRefId);
		PlayerImmediateDaoFacade.update(player);
		G2C_Achievement_LevlUpMedal res = MessageFactory.getConcreteMessage(AchievementEventDefines.G2C_Achievement_LevlUpMedal);
		res.setRefId(nextRefId);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	public void sendGameEventMessage(String nextRefId) {
		ExchangeOrLevelUpMedal_GE exchangeOrLevelUpMedal_GE = new ExchangeOrLevelUpMedal_GE();
		exchangeOrLevelUpMedal_GE.setMedalRefId(nextRefId);
		GameEvent<ExchangeOrLevelUpMedal_GE> ge = (GameEvent<ExchangeOrLevelUpMedal_GE>) GameEvent.getInstance(AchievementTypeMacro.MEDAL_GE_ID, exchangeOrLevelUpMedal_GE);
		sendGameEvent(ge, getConcreteParent().getId());

	}

	private int getAllEquipMinStrengthLevel() {
		List<Item> equipedItems = player.getPlayerEquipBodyConponent().getEquipedItems();

		byte minStrengtheningLevel = 0;
		if (equipedItems.size() < 11) {
			return minStrengtheningLevel;
		}
		Item minEquip = equipedItems.get(0);
		if(minEquip == null){
			return minStrengtheningLevel;
		}
		minStrengtheningLevel =  MGPropertyAccesser.getStrengtheningLevel(minEquip.getProperty());
		
		for (Item item : equipedItems) {
			PropertyDictionary property = item.getProperty();
			byte strengtheningLevel = MGPropertyAccesser.getStrengtheningLevel(property);
			
			if (strengtheningLevel < minStrengtheningLevel) {
				minStrengtheningLevel = strengtheningLevel;
			}
		}

		return minStrengtheningLevel;
	}

	public MGAchievePointMgr getAchievePointMgr() {
		return achievePointMgr;
	}

	public MGAchievementPersistenceObject getAchievementPersistenceObject() {
		return achievementPersistenceObject;
	}

	public void setAchievementPersistenceObject(MGAchievementPersistenceObject achievementPersistenceObject) {
		this.achievementPersistenceObject = achievementPersistenceObject;
	}

	public NumberRecordMgr getNumberMgr() {
		return numberMgr;
	}

	public void setNumberMgr(NumberRecordMgr numberMgr) {
		this.numberMgr = numberMgr;
	}

	public boolean isExchange() {
		return isExchange;
	}

	public void setExchange(boolean isExchange) {
		this.isExchange = isExchange;
	}

	public CastleWarRecord getRecord() {
		return record;
	}

	public void setRecord(CastleWarRecord record) {
		this.record = record;
	}

}
