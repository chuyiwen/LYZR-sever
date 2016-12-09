package newbee.morningGlory.mmorpg.ladder;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.ladder.ArenaRewardRef;
import newbee.morningGlory.mmorpg.player.activity.ladder.CombatRecord;
import newbee.morningGlory.mmorpg.player.activity.ladder.LadderRewardUtil;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_UpadateFightRecord;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_UpadateNotice;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.MGLadderDefines;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.IoBufferUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.PlayerChatFacade;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.chat.event.G2C_Chat_System;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.world.ActionEventFacade;

import com.google.common.base.Preconditions;

public class MGLadderSystemMessageFacade {
	private static Logger logger = Logger.getLogger(MGLadderSystemMessageFacade.class);

	public static final int Default_Sendmsg_StreakNumber = 15;

	public final static byte SYSTEM_TYPE_1 = 1;// [玩家姓名] 霸气外露，在竞技场达到了15连杀
	public final static byte SYSTEM_TYPE_2 = 2;// [玩家姓名] 击败 [玩家姓名] 成为竞技场第1名，称霸天梯
	public final static byte SYSTEM_TYPE_3 = 3;// [玩家姓名] 连升[X]名，成为竞技场的新星
	public final static byte SYSTEM_TYPE_4 = 4;// 恭喜 [玩家姓名]
												// 在竞技场脱颖而出，称霸天梯，获得5000功勋，金币2000000奖励，[玩家姓名]和[玩家姓名]分获2、3名

	private static byte curLadderSystemInfoType = 0;
	private static List<Object> ladderSystemInfo = new ArrayList<Object>();

	public static void sendLadderSystemMsg(byte type, String msg) {
		if (type != SYSTEM_TYPE_4) {
			G2C_Arena_UpadateNotice ladderRes = MessageFactory.getConcreteMessage(MGLadderDefines.G2C_Arena_UpadateNotice);
			ActionEventFacade.sendMessageToWorld(ladderRes);
		}

		G2C_Chat_System chatRes = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_System);
		chatRes.setType((byte) 1);
		chatRes.setMsg(msg);
		ActionEventFacade.sendMessageToWorld(chatRes);
	}

	public static void sendLadderBattleMsg(Player receiver, CombatRecord record) {
		G2C_Arena_UpadateFightRecord ladderRes = MessageFactory.getConcreteMessage(MGLadderDefines.G2C_Arena_UpadateFightRecord);
		ladderRes.setRecord(record);
		PlayerChatFacade.sendMessageToPlayer(receiver, ladderRes);
	}

	public static synchronized void addLadderSystemInfo(final byte type, Object... args) {
		curLadderSystemInfoType = type;
		ladderSystemInfo.clear();
		for (Object obj : args) {
			ladderSystemInfo.add(obj);
		}
	}

	public static synchronized void writeLadderSystemInfo(IoBuffer buffer) {
		buffer.put(curLadderSystemInfoType);
		for (Object obj : ladderSystemInfo) {
			if (obj instanceof String) {
				IoBufferUtil.putString(buffer, (String) obj);
			}

			if (obj instanceof Integer) {
				buffer.putInt((Integer) obj);
			}
		}
	}

	public static String formatInfo(byte type, Object... args) {
		String msg = null;
		if (type == SYSTEM_TYPE_1)
			msg = String.format("{p=%1$s<%2$s><%3$d>}霸气外露，在竞技场达到了15连杀", args[0], args[1], args[2]);
		if (type == SYSTEM_TYPE_2)
			msg = String.format("{p=%1$s<%2$s><%3$d>}击败{p=%4$s<%5$s><%6$d>} 成为竞技场第1名，称霸天梯", args[0], args[1], args[2], args[3], args[4], args[5]);
		if (type == SYSTEM_TYPE_3)
			msg = String.format("{p=%1$s<%2$s><%3$d>}连升 %4$d名，成为竞技场的新星", args[0], args[1], args[2], args[3]);
		if (type == SYSTEM_TYPE_4)
			msg = String.format("恭喜{p=%1$s<%2$s><%3$d>}在竞技场脱颖而出，称霸天梯，获得%4$d功勋，金币%5$d奖励，{p=%6$s<%7$s><%8$d>}和{p=%9$s<%10$s><%11$d>}分获2、3名", args[0], args[1], args[2], args[3],
					args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
		return msg;
	}

	public static Player getPlayerMemberName(String memberName) {
		return MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(memberName);
	}

	/** [玩家姓名] 霸气外露，在竞技场达到了15连杀 */
	public static void sendStreakNotice(Player player, int streakNumber) {
		Preconditions.checkArgument(player != null);
		if (streakNumber <= 0) {
			return;
		}
		
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		String playerName = player.getName();
		String playerId = player.getId();

		if (streakNumber == Default_Sendmsg_StreakNumber) {
			addLadderSystemInfo(MGLadderSystemMessageFacade.SYSTEM_TYPE_1, playerName);
			String msg = formatInfo(MGLadderSystemMessageFacade.SYSTEM_TYPE_1, playerName, playerId, gender);
			MGLadderSystemMessageFacade.sendLadderSystemMsg(MGLadderSystemMessageFacade.SYSTEM_TYPE_1, msg);
		}
	}

	/** [玩家姓名] 击败 [玩家姓名] 成为竞技场第1名，称霸天梯 */
	public static void sendDominateNotice(Player fighter, Player target, int fighterRank) {
		byte fighterGender = MGPropertyAccesser.getGender(fighter.getProperty());
		byte targetGender = MGPropertyAccesser.getGender(target.getProperty());

		String fighterName = fighter.getName();
		String targetName = target.getName();

		String fighterId = fighter.getId();
		String targetId = target.getId();

		if (fighterRank == 1) {
			addLadderSystemInfo(SYSTEM_TYPE_2, fighterName, targetName);
			String msg = formatInfo(SYSTEM_TYPE_2, fighterName, fighterId, fighterGender, targetName, targetId, targetGender);
			sendLadderSystemMsg(MGLadderSystemMessageFacade.SYSTEM_TYPE_2, msg);
		}
	}

	/** 每小时发送系统消息 [玩家姓名] 连升[X]名，成为竞技场的新星 */
	public static void sendNewStarNotice() {
		MGLadderMember member = MGLadderMemberMgr.getMaxRankChangeLadderMember();

		if (null == member || member.getRankChange() <= 0) {
			return;
		}

		int rankChange = member.getRankChange();
		String playerName = member.getPlayerName();
		Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(playerName);

		if (player == null) {
			logger.error("member is not exist! player = " + player);
			return;
		}

		String playerId = player.getId();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());

		addLadderSystemInfo(SYSTEM_TYPE_3, playerName, rankChange);
		String msg = formatInfo(SYSTEM_TYPE_3, playerName, playerId, gender, rankChange);
		MGLadderSystemMessageFacade.sendLadderSystemMsg(SYSTEM_TYPE_3, msg);
	}

	public static void sendWolrdNotice() {

		if (MGLadderMemberMgr.getLadderMembersNum() < 3) {
			return;
		}
		MGLadderMember firstMember = MGLadderMemberMgr.getLadderMemberByRank(1);
		MGLadderMember secondMember = MGLadderMemberMgr.getLadderMemberByRank(2);
		MGLadderMember thirdMember = MGLadderMemberMgr.getLadderMemberByRank(3);
		String firstName = firstMember.getPlayerName();
		String secondName = secondMember.getPlayerName();
		String thirdName = thirdMember.getPlayerName();

		Player firstPlayer = getPlayerMemberName(firstName);
		Player secondPlayer = getPlayerMemberName(secondName);
		Player thirdPlayer = getPlayerMemberName(thirdName);

		String firstPlayerId = firstPlayer.getId();
		String secondPlayerId = secondPlayer.getId();
		String thirdPlayerId = thirdPlayer.getId();

		byte firstPlayerGender = MGPropertyAccesser.getGender(firstPlayer.getProperty());
		byte secondPlayerGender = MGPropertyAccesser.getGender(secondPlayer.getProperty());
		byte thirdPlayerGender = MGPropertyAccesser.getGender(thirdPlayer.getProperty());
		
		int rewardRank = firstMember.getRewardRank();
		String refId = LadderRewardUtil.getArenaRewardRefIdByRank(rewardRank);
		ArenaRewardRef ref = LadderRewardUtil.getArenaRewardRef(refId);
		int gold = 0;
		int merit = 0;
		if (ref != null) {
			gold = ref.getMeirtOrGoldReward(rewardRank, ArenaRewardRef.Type_Gold);
			merit = ref.getMeirtOrGoldReward(rewardRank, ArenaRewardRef.Type_Merit);
		}

		String msg = formatInfo(SYSTEM_TYPE_4, firstName, firstPlayerId, firstPlayerGender, merit, gold, secondName, secondPlayerId, secondPlayerGender, thirdName, thirdPlayerId,
				thirdPlayerGender);
		sendLadderSystemMsg(SYSTEM_TYPE_4, msg);
	}

}
