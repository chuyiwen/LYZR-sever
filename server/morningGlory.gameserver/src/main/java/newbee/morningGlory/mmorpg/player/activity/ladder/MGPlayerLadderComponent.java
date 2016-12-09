package newbee.morningGlory.mmorpg.player.activity.ladder;

import java.util.List;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.ladder.ArenaMgr;
import newbee.morningGlory.mmorpg.ladder.MGLadderMacro;
import newbee.morningGlory.mmorpg.ladder.MGLadderMember;
import newbee.morningGlory.mmorpg.ladder.MGLadderMemberMgr;
import newbee.morningGlory.mmorpg.ladder.MGLadderMemberSaver;
import newbee.morningGlory.mmorpg.ladder.MGLadderSystemMessageFacade;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.C2G_Arena_CanReceive;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.C2G_Arena_Challenge;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.C2G_Arena_Challenge_Award;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.C2G_Arena_ClearCDTime;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.C2G_Arena_ReceiveReward;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.C2G_Arena_ShowArenaView;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.C2G_Ladder_Select;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_CanReceive;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_ShowArenaView;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Ladder_Select;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.MGLadderDefines;
import newbee.morningGlory.mmorpg.player.activity.persistence.ActivityPersistenceObject;
import newbee.morningGlory.stat.MGStatFunctions;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;

public class MGPlayerLadderComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerLadderComponent.class);

	public static final String Tag = "MGPlayerLadderComponentTag";

	public static final String LeaveWorld_GE_ID = LeaveWorld_GE.class.getSimpleName();
	
	public static final String EnterWorld_GE_ID = EnterWorld_GE.class.getSimpleName();

	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();

	private ActivityPersistenceObject activityPersistenceObject;

	private byte fightResult;

	private boolean challengeRewardState = false;

	private Player player;
	
	private MessageSender msgSender;
	
	private MGLadderMemberSaver ladderSaver;

	@Override
	public void ready() {
		player = getConcreteParent();
		msgSender = MessageSender.getInstance();
		ladderSaver = MGLadderMemberSaver.getInstance();
		addActionEventListener(MGLadderDefines.C2G_Arena_ShowArenaView);
		addActionEventListener(MGLadderDefines.C2G_Arena_ReceiveReward);
		addActionEventListener(MGLadderDefines.C2G_Arena_Challenge);
		addActionEventListener(MGLadderDefines.C2G_Ladder_Select);
		addActionEventListener(MGLadderDefines.C2G_Arena_CanReceive);
		addActionEventListener(MGLadderDefines.C2G_Arena_ClearCDTime);
		addActionEventListener(MGLadderDefines.C2G_Arena_Challenge_Award);
		addInterGameEventListener(LeaveWorld_GE_ID);
		addInterGameEventListener(EnterWorld_GE_ID);
		addInterGameEventListener(ChineseModeQuest_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(MGLadderDefines.C2G_Arena_ShowArenaView);
		removeActionEventListener(MGLadderDefines.C2G_Arena_ReceiveReward);
		removeActionEventListener(MGLadderDefines.C2G_Arena_Challenge);
		removeActionEventListener(MGLadderDefines.C2G_Ladder_Select);
		removeActionEventListener(MGLadderDefines.C2G_Arena_CanReceive);
		removeActionEventListener(MGLadderDefines.C2G_Arena_ClearCDTime);
		removeActionEventListener(MGLadderDefines.C2G_Arena_Challenge_Award);
		removeInterGameEventListener(LeaveWorld_GE_ID);
		removeInterGameEventListener(EnterWorld_GE_ID);
		removeInterGameEventListener(ChineseModeQuest_GE_Id);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(LeaveWorld_GE_ID)) {
			int level = player.getExpComponent().getLevel();
			int fightValue = player.getFightPower();
			String playerName = player.getName();
			MGLadderMember member = MGLadderMemberMgr.getLadderMemberByName(playerName);
			if (member != null) {
				member.setLevel(level);
				member.setFightValue(fightValue);
				ladderSaver.saveImmediateData(member);
			}
		} else if (event.isId(ChineseModeQuest_GE_Id)) {
			ChineseModeQuest_GE chineseModeQuest_GE = (ChineseModeQuest_GE) event.getData();
			if (chineseModeQuest_GE.getType() != ChineseModeQuest_GE.AcceptType) {
				return;
			}
			if (chineseModeQuest_GE.getOrderEventId() == QuestChineseOrderDefines.Ladder) {
				sendChineseModeGameEventMessage(0);
			}
		} else if (event.isId(EnterWorld_GE_ID)) {
			long now = System.currentTimeMillis();
			long lastLogoutTime = MGPropertyAccesser.getLastLogoutTime(player.getProperty());
			boolean sameDay = DateTimeUtil.isTheSameDay(now, lastLogoutTime);
			if (!sameDay) {
				String playerName = player.getName();
				MGLadderMember member = MGLadderMemberMgr.getLadderMemberByName(playerName);
				if (member != null) {
					member.resetChallengetCount();
					member.resetStreak();
				}
			}
		}
		super.handleGameEvent(event);
	}

	public void sendChineseModeGameEventMessage(int joinTime) {
		MGLadderMember playerMember = MGLadderMemberMgr.getLadderMemberByName(player.getName());
		if (playerMember != null && joinTime == 0) {
			int remainChallengeCount = playerMember.getRemainChallengeCount();
			joinTime = MGLadderMacro.Default_Challenge_Count - remainChallengeCount;
		}
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setNumber(joinTime);
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.Ladder);
		GameEvent<ChineseModeQuest_GE> chineseModeGE = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		getConcreteParent().handleGameEvent(chineseModeGE);
		GameEvent.pool(chineseModeGE);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("竞技场事件：" + eventId);
		}

		switch (eventId) {
		case MGLadderDefines.C2G_Arena_ShowArenaView:
			handle_Arena_ShowArenaView((C2G_Arena_ShowArenaView) event);
			break;

		case MGLadderDefines.C2G_Ladder_Select:
			handle_Ladder_Select((C2G_Ladder_Select) event);
			break;

		case MGLadderDefines.C2G_Arena_ReceiveReward:
			handle_Arena_ReceiveReward((C2G_Arena_ReceiveReward) event);
			break;

		case MGLadderDefines.C2G_Arena_Challenge:
			handle_Arena_Challenge((C2G_Arena_Challenge) event);
			break;

		case MGLadderDefines.C2G_Arena_ClearCDTime:
			handle_Arena_ClearCDTime((C2G_Arena_ClearCDTime) event);
			break;

		case MGLadderDefines.C2G_Arena_CanReceive:
			handle_Arena_CanReceive((C2G_Arena_CanReceive) event);
			break;

		case MGLadderDefines.C2G_Arena_Challenge_Award:
			handle_Arena_GetChallengeAward((C2G_Arena_Challenge_Award) event);
		default:
			break;
		}

		super.handleActionEvent(event);
	}

	private void handle_Ladder_Select(C2G_Ladder_Select event) {
		G2C_Ladder_Select res = MessageFactory.getConcreteMessage(MGLadderDefines.G2C_Ladder_Select);
		GameRoot.sendMessage(player.getIdentity(), res);
	}

	private void handle_Arena_ClearCDTime(C2G_Arena_ClearCDTime event) {
		MGLadderMember member = MGLadderMemberMgr.getLadderMemberByName(player.getName());

		if (member == null) {
			logger.error("clearCDTime error: playerName = " + player.getName() + " not a ladder member");
			return;
		}

		int remainCDTime = member.getCDTime();
		int divideValue = remainCDTime / 60;
		int min = remainCDTime % 60 == 0 ? divideValue : divideValue + 1;
		if (min <= 0) {
			return;
		}
		
		int unbindGoldCost = min * MGLadderMacro.Default_UnbinedGold_ClearCDEveryMin;
		boolean result = player.getPlayerMoneyComponent().subUnbindGold(unbindGoldCost, ItemOptSource.Ladder);
		if (!result) {
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_Ladder_UnbinedGoldNotEnough);
			return;
		}

		msgSender.updateCDTime(player, member, 0);

		ladderSaver.saveImmediateData(member);
	}

	private void handle_Arena_CanReceive(C2G_Arena_CanReceive event) {
		byte canReceive = 0;
		MGLadderMember member = MGLadderMemberMgr.getLadderMemberByName(player.getName());

		if (member != null) {
			canReceive = member.getRewardRank() == 0 ? (byte) 0 : (byte) 1;
		}

		G2C_Arena_CanReceive res = MessageFactory.getConcreteMessage(MGLadderDefines.G2C_Arena_CanReceive);
		res.setCanReceive(canReceive);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_Arena_ReceiveReward(C2G_Arena_ReceiveReward event) {
		short actionEventId = event.getActionEventId();

		MGLadderMember member = MGLadderMemberMgr.getLadderMemberByName(player.getName());
		if (member == null) {
			logger.error("receiveReward error, playerName = " + player.getName() + " not a ladder member");
			return;
		}

		int rewardRank = member.getRewardRank();
		if (rewardRank == MGLadderMacro.Default_RewardRank) {
			logger.debug("领奖时间未到");
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_TimeNotOver);
			return;
		}

		List<ItemPair> itemPairs = LadderRewardUtil.getRewardItemPairs(player, rewardRank);
		if (!ItemFacade.isItemBagSlotEnough(player, itemPairs)) {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MMORPGErrorCode.CODE_ITEM_FULL);
			return;
		}
		
		ItemFacade.addItem(player, itemPairs, ItemOptSource.Ladder);
	
		member.setRewardRank(MGLadderMacro.Default_RewardRank);
		msgSender.updateRewardInfo(player, member);

		ladderSaver.saveImmediateData(member);
		MGStatFunctions.arenaStat(player, member);
	}

	/**
	 * 领取挑战奖励
	 * 
	 * @param event
	 */
	private void handle_Arena_GetChallengeAward(C2G_Arena_Challenge_Award event) {

		MGLadderMember member = MGLadderMemberMgr.getLadderMemberByName(player.getName());
		if (member == null) {
			logger.error("receiveReward error, playerName = " + player.getName() + " not a ladder member");
			return;
		}

		if (!hasChleengeReward()) {
			logger.debug("have not challenge reward!");
			return;
		}

		List<ItemPair> itemPairs = LadderRewardUtil.getRewardByMemberType(fightResult);
		if (!ItemFacade.isItemBagSlotEnough(player, itemPairs)) {
			ResultEvent.sendResult(player.getIdentity(), MGLadderDefines.C2G_Arena_Challenge_Award, MMORPGErrorCode.CODE_ITEM_FULL);
			return;
		}
		
		ItemFacade.addItem(player, itemPairs, ItemOptSource.Ladder);
		updateChallengeReward(false);
		ladderSaver.saveImmediateData(member);
	}

	private void handle_Arena_ShowArenaView(C2G_Arena_ShowArenaView event) {
		int level = player.getExpComponent().getLevel();
		if (level < MGLadderMacro.Default_Arena_Openlevel) {
			if (logger.isDebugEnabled()) {
				logger.debug("player level is low! level = " + level);
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_Activity_LevelTooLower);
			return;
		}

		String playerName = player.getName();
		MGLadderMember member = MGLadderMemberMgr.getLadderMemberByName(playerName);
		if (member == null) {
			member = MGLadderMemberMgr.addLadderMember(player);
			if (member.getRank() <= MGLadderMacro.Max_Member_Count) {
				ladderSaver.insertMGLadderMember(member);
				MGStatFunctions.arenaStat(player, member);
			}
		}

		if (member != null) {
			G2C_Arena_ShowArenaView res = MessageFactory.getConcreteMessage(MGLadderDefines.G2C_Arena_ShowArenaView);
			res.setOwner(player);
			res.setMember(member);
			GameRoot.sendMessage(player.getIdentity(), res);
		}

	}
	
	private boolean checkValidRank(int rank) {
		int totalNum = MGLadderMemberMgr.getLadderMembersNum();
		if (rank < 1 || rank > MGLadderMacro.Max_Member_Count || rank > totalNum) {
			return false;
		}
		
		return true;
	}

	private void handle_Arena_Challenge(C2G_Arena_Challenge event) {
		short actionEventId = event.getActionEventId();

		int targetRank = event.getTargetRank();
		Player fighter = player;
		String fighterName = fighter.getName();

		int level = fighter.getExpComponent().getLevel();
		if (level < MGLadderMacro.Default_Arena_Openlevel) {
			if (logger.isDebugEnabled()) {
				logger.debug("level = " + level);
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_Activity_LevelTooLower);
			return;
		}

		if (!checkValidRank(targetRank)) {
			logger.error("challenge error, invalid targetRank=" + targetRank);
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_Ladder_Invalid_Challenge_Rank);
			return;
		}

		MGLadderMember fightMember = MGLadderMemberMgr.getLadderMemberByName(fighterName);

		if (fightMember == null) {
			fightMember = MGLadderMemberMgr.getLadderCacheMemberByName(fighterName);
		}

		MGLadderMember targetMember = MGLadderMemberMgr.getLadderMemberByRank(targetRank);

		int code = MGLadderMemberMgr.challengeValidCheck(fighter, targetRank, fightMember, targetMember);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			if (logger.isDebugEnabled()) {
				logger.debug("challenge failure, errorCode=" + CodeContext.description(code));
			}

			ResultEvent.sendResult(player.getIdentity(), actionEventId, code);
			return;
		}

		String targetName = targetMember.getPlayerName();
		Player target = playerManager.getPlayerByName(targetName);

		boolean fighterInBattle = false;
		boolean targetInBattle = false;
		try {
			fighterInBattle = fightMember.getInBattle().compareAndSet(false, true);
			targetInBattle = targetMember.getInBattle().compareAndSet(false, true);

			if (fighterInBattle == false || targetInBattle == false) {
				if (logger.isDebugEnabled()) {
					logger.debug("inBattle, fighterInBattle=" + fighterInBattle + ", targetInBattle=" + targetInBattle);
				}
				return;
			}
			
			targetRank = targetMember.getRank();
			if (!checkValidRank(targetRank) || MGLadderMemberMgr.getLadderMemberByRank(targetRank) == null) {
				logger.error("challenge error, invalid targetRank=" + targetRank);
				ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_Ladder_Invalid_Challenge_Rank);
				return;
			}
			
			challenge(fightMember, targetMember, fighter, target);
		} finally {
			if (fighterInBattle) {
				fightMember.getInBattle().set(false);
			}
			if (targetInBattle) {
				targetMember.getInBattle().set(false);
			}
		}

		ladderSaver.saveImmediateData(fightMember);
		ladderSaver.saveImmediateData(targetMember);

		MGStatFunctions.arenaStat(fighter, fightMember);
		MGStatFunctions.arenaStat(target, targetMember);

	}

	private void challenge(MGLadderMember fightMember, MGLadderMember targetMember, Player fighter, Player target) {

		int fightPower = fighter.getFightPower();
		int targetPower = target.getFightPower();

		int fighterRank = fightMember.getRank();
		int targetRank = targetMember.getRank();

		int[] randomFightPower = ArenaMgr.getRandomFightValue(fightPower, targetPower);

		fightResult = ArenaMgr.getFightResult(randomFightPower);

		byte battlefieldType = ArenaMgr.getBattlefieldType(fighterRank, targetRank, fightResult);

		// 发送播放动画需要的挑战对象的信息
		msgSender.sendChllengeMemberPropertyMsg(player, target, fightResult, randomFightPower);

		// 发送战斗结果记录
		msgSender.sendAndSaveBattlefield(battlefieldType, fighter, target, fightMember, targetMember);
		sendChineseModeGameEventMessage(1);
		// 战斗结束========================================================

		// 交换名次，更新相关信息
		MGLadderMemberMgr.updateMemberInfo(battlefieldType, fighter, target, fightMember, targetMember);

		// 可以领取天梯战斗奖励
		updateChallengeReward(true);

		// 发送战斗胜利系统战斗消息
		if (fightResult == ArenaMgr.Athletics_Win) {
			sendWinSystemMessage(fighter, target, fightMember, targetMember);
		}

		// 更新个人信息区域
		msgSender.updatePersonalInfo(fighter, fightMember);
		msgSender.updatePersonalInfo(target, targetMember);
	}

	private void sendWinSystemMessage(Player fighter, Player target, MGLadderMember fightMember, MGLadderMember targetMember) {
		MGLadderSystemMessageFacade.sendStreakNotice(fighter, fightMember.getStreak());
		MGLadderSystemMessageFacade.sendDominateNotice(fighter, target, fightMember.getRank());
	}

	private void updateChallengeReward(boolean state) {
		this.challengeRewardState = state;
	}

	private boolean hasChleengeReward() {
		return this.challengeRewardState;
	}

	public ActivityPersistenceObject getActivityPersistenceObject() {
		return activityPersistenceObject;
	}

	public void setActivityPersistenceObject(ActivityPersistenceObject activityPersistenceObject) {
		this.activityPersistenceObject = activityPersistenceObject;
	}

}
