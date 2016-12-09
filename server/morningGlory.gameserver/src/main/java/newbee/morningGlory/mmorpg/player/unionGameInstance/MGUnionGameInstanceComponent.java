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
package newbee.morningGlory.mmorpg.player.unionGameInstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.gameInstance.PlayerGameInstanceComponent;
import newbee.morningGlory.mmorpg.player.union.ExitUnion_GE;
import newbee.morningGlory.mmorpg.player.union.UnionMacro;
import newbee.morningGlory.mmorpg.player.unionGameInstance.event.G2C_UnionGameInstance_Apply;
import newbee.morningGlory.mmorpg.player.unionGameInstance.event.G2C_UnionGameInstance_Enter;
import newbee.morningGlory.mmorpg.player.unionGameInstance.event.G2C_UnionGameInstance_Finish;
import newbee.morningGlory.mmorpg.player.unionGameInstance.event.MGUnionGameInstanceDefines;
import newbee.morningGlory.mmorpg.union.MGUnion;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;
import newbee.morningGlory.mmorpg.union.MGUnionMember;
import newbee.morningGlory.mmorpg.union.MGUnionMgr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.event.G2C_System_Prompt;
import sophia.mmorpg.player.chat.sysytem.SpecialEffectsType;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.chat.sysytem.SystemPromptPosition;
import sophia.mmorpg.utils.DateTimeUtil;
import sophia.mmorpg.world.ActionEventFacade;

public class MGUnionGameInstanceComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGUnionGameInstanceComponent.class);
	public static final String Tag = "MGUnionGameInstanceComponent";
	public static final String GameInstanceRefId = "Ins_8";
	public static final String unionGameInstanceRefId = "unionGameInstance";
	private UnionGameInstanceRef unionGameInstanceRef = null;

	public MGUnionGameInstanceComponent() {
		unionGameInstanceRef = (UnionGameInstanceRef) GameRoot.getGameRefObjectManager().getManagedObject(unionGameInstanceRefId);
	}

	@Override
	public void ready() {
		addActionEventListener(MGUnionGameInstanceDefines.C2G_UnionGameInstance_Apply);
		addActionEventListener(MGUnionGameInstanceDefines.C2G_UnionGameInstance_Enter);
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
		addInterGameEventListener(ExitUnion_GE.class.getSimpleName());
	}

	@Override
	public void suspend() {
		removeActionEventListener(MGUnionGameInstanceDefines.C2G_UnionGameInstance_Apply);
		removeActionEventListener(MGUnionGameInstanceDefines.C2G_UnionGameInstance_Enter);
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
		removeInterGameEventListener(ExitUnion_GE.class.getSimpleName());
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(ExitUnion_GE.class.getSimpleName())) {
			MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
			Player player = getConcreteParent();
			if (MGUnionHelper.isUnionCreater(player)) {
				UnionGameInstanceMgr applyUnionGameInstance = MorningGloryContext.getUnionSystemComponent().getUnionGameInstanceMgr();
				PlayerGameInstanceComponent component = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
				String gameInstanceId = component.getGameInstanceId(MGUnionGameInstanceComponent.GameInstanceRefId);
				GameInstance gameInstance = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr().getGameInstace(gameInstanceId);
				component.closeMultiGameInstance(gameInstance);
				applyUnionGameInstance.close(gameInstance);
			} else {
				PlayerGameInstanceComponent component = (PlayerGameInstanceComponent) getConcreteParent().getTagged(PlayerGameInstanceComponent.Tag);
				component.removeGameInstanceId(GameInstanceRefId);
			}
		} else if (event.isId(Monster.MonsterDead_GE_Id)) {
			MonsterDead_GE ge = (MonsterDead_GE) event.getData();
			Monster monster = ge.getMonster();
			FightSprite attacker = ge.getAttacker();
			if (attacker instanceof Monster) {
				Monster baobao = (Monster) attacker;
				if (!baobao.getMonsterRef().isRegularMonster() && baobao.getOwner() != null) {
					attacker = baobao.getOwner();
				}
			}
			String sceneRefId = monster.getCrtScene().getRef().getId();
			String uninoSceneRefId = unionGameInstanceRef.getSceneRefId();
			if (StringUtils.equals(sceneRefId, uninoSceneRefId) && StringUtils.equals(monster.getMonsterRef().getId(), unionGameInstanceRef.getBossId())) {
				Player killer = (Player) attacker;
				MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
				MGUnion union = MGUnionHelper.getUnion(killer);
				union.setKilledUnionBoss(true);
				Collection<Player> players = new ArrayList<Player>();
				PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
				for (MGUnionMember member : union.getMemberMgr().getMemberList()) {
					String playerId = member.getPlayerId();
					Player p = playerManager.getOnlinePlayer(playerId);
					if (p != null) {
						players.add(p);
					}
				}
				String contents = "挑战禁地魔王成功，活动结束后可获得邮件奖励";
				G2C_UnionGameInstance_Finish res1 = MessageFactory.getConcreteMessage(MGUnionGameInstanceDefines.G2C_UnionGameInstance_Finish);
				res1.setRet((byte) 1);
				G2C_System_Prompt res2 = SystemPromptFacade.getRes(contents, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
				ActionEventFacade.sendMessageToPart(res1, players);
				ActionEventFacade.sendMessageToPart(res2, players);
				MGUnionHelper.sendUnionSystemChatMessage(union, contents);
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		switch (actionEventId) {
		case MGUnionGameInstanceDefines.C2G_UnionGameInstance_Apply:
			handle_UnionGameInstance_Apply(event);
			break;
		case MGUnionGameInstanceDefines.C2G_UnionGameInstance_Enter:
			handle_UnionGameInstance_Enter(event);
			break;
		default:
			break;
		}
	}

	private void handle_UnionGameInstance_Apply(ActionEventBase event) {
		Player player = getConcreteParent();
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		UnionGameInstanceMgr applyUnionGameInstance = MorningGloryContext.getUnionSystemComponent().getUnionGameInstanceMgr();
		// String unionName = unionMgr.getUnionName(player);
		int needGold = unionGameInstanceRef.getGold();
		int minNumber = unionGameInstanceRef.minNumber();
		// if (StringUtils.isEmpty(unionName)) {
		// ResultEvent.sendResult(player.getIdentity(),
		// event.getActionEventId(),
		// MGErrorCode.CODE_UNIONFUBEN_NOT_JOIN_UNION);
		// return;
		// }

		MGUnion union = MGUnionHelper.getUnion(player);

		if (union == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家没加入公会");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_JOIN_UNION);
			return;
		}

		String unionName = union.getName();

		if (!MGUnionHelper.isUnionCreater(player)) {
			if (logger.isDebugEnabled()) {
				logger.debug(player.getName() + "不是公会会长");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_UNIONLEARDER);
			return;
		}

		if (applyUnionGameInstance.isApplyGameInstance(union)) {
			if (logger.isDebugEnabled()) {
				logger.debug("公会:" + unionName + "今天已报名公会副本");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_HAD_BAOMING);
			return;
		}

		int memberNumber = union.getMemberMgr().getMemberCount();
		if (memberNumber < minNumber) {
			if (logger.isDebugEnabled()) {
				logger.debug("公会:" + unionName + "人数少于10");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_ENOUGH);
			return;
		}

		int gold = player.getPlayerMoneyComponent().getGold();
		if (gold < needGold) {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家:" + player.getName() + "金币数少于1千万");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_GOLD);
			return;
		}
		if (!applyUnionGameInstance.canJoin()) {
			if (logger.isDebugEnabled()) {
				logger.debug("当前时间不可开启公会副本");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_TIME_BAO);
			return;
		}

		boolean addGameInstanceUnions = applyUnionGameInstance.join(union);
		if (!addGameInstanceUnions) {
			if (logger.isDebugEnabled()) {
				logger.debug("公会:" + unionName + "失败");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_HAD_BAOMING);
			return;
		}

		player.getPlayerMoneyComponent().subGold(needGold, (byte) 0);
		if (logger.isDebugEnabled()) {
			logger.debug("公会:" + unionName + "报名成功");
		}

		long startTime = applyUnionGameInstance.getStartTime();
		String dateDetailString = DateTimeUtil.getDateDetailString(startTime);
		String content = "公会报名公会副本成功，请于" + dateDetailString + "准时参加公会副本，夺取极品装备及大量公会贡献";
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		for (MGUnionMember m : union.getMemberMgr().getMemberList()) {
			String playerId = m.getPlayerId();
			Player p = playerManager.getPlayer(playerId);
			MailMgr.sendMailById(p.getId(), content, Mail.huodong);
		}

		byte ret = 1;
		G2C_UnionGameInstance_Apply res = MessageFactory.getConcreteMessage(MGUnionGameInstanceDefines.G2C_UnionGameInstance_Apply);
		res.setRet(ret);
		GameRoot.sendMessage(player.getIdentity(), res);
	}

	private void handle_UnionGameInstance_Enter(ActionEventBase event) {
		Player player = getConcreteParent();
		UnionGameInstanceMgr applyUnionGameInstance = MorningGloryContext.getUnionSystemComponent().getUnionGameInstanceMgr();
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家没加入公会");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_JOIN_UNION);
			return;
		}

		String unionName = union.getName();

		if (!applyUnionGameInstance.isOpenEnter()) {
			if (logger.isDebugEnabled()) {
				logger.debug("公会副本开放时间未到");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_TIME_OPEN);
			return;
		}
		if (!applyUnionGameInstance.isApplyGameInstance(union)) {
			if (logger.isDebugEnabled()) {
				logger.debug("公会:" + unionName + "今天未报名公会副本");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_UNIONFUBEN_NOT_BAOMING);
			return;
		}

		MGUnionMember member = union.getCreater();
		Player unionLeader = MGUnionHelper.getPlayerByMember(member);
		PlayerGameInstanceComponent leaderComponent = (PlayerGameInstanceComponent) unionLeader.getTagged(PlayerGameInstanceComponent.Tag);
		String gameInstanceId = leaderComponent.getGameInstanceId(GameInstanceRefId);
		if (StringUtils.isEmpty(gameInstanceId)) {
			union.setApplyGameInstance(false);
			applyUnionGameInstance.openUnionGameInstance(unionLeader);
			gameInstanceId = leaderComponent.getGameInstanceId(GameInstanceRefId);
		}

		PlayerGameInstanceComponent selfComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
		if (StringUtils.isEmpty(selfComponent.getGameInstanceId(GameInstanceRefId))) {
			selfComponent.addGameInstanceIdIfAbsent(GameInstanceRefId, gameInstanceId);
		}

		PlayerGameInstanceComponent component = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
		boolean gameMultiInstanceEnter = component.gameMultiInstanceEnter(player, gameInstanceId, event.getIdentity(), event.getActionEventId());
		byte ret = 0;
		byte isComplete = 0;
		if (gameMultiInstanceEnter) {
			ret = 1;
		}
		if (union.isKilledUnionBoss()) {
			isComplete = 1;
		}
		G2C_UnionGameInstance_Enter res = MessageFactory.getConcreteMessage(MGUnionGameInstanceDefines.G2C_UnionGameInstance_Enter);
		res.setRet(ret);
		res.setIsComplete(isComplete);
		GameRoot.sendMessage(player.getIdentity(), res);
	}

}
