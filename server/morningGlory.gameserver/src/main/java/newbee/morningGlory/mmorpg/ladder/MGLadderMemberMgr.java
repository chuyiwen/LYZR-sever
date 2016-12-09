package newbee.morningGlory.mmorpg.ladder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.activity.ladder.MessageSender;
import newbee.morningGlory.stat.MGStatFunctions;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class MGLadderMemberMgr {
	private static Logger logger = Logger.getLogger(MGLadderMemberMgr.class);

	// <rank, MGLadderMember>
	private static Map<Integer, MGLadderMember> members = new HashMap<Integer, MGLadderMember>();

	private static Map<String, Integer> nameToRankMapping = new HashMap<String, Integer>();

	private static Map<String, MGLadderMember> cacheMembers = new HashMap<>();

	private static PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();

	public static MGLadderMember createLadderMember(Player player) {
		Preconditions.checkArgument(player != null);

		String playerId = player.getId();
		String playerName = player.getName();
		int level = player.getExpComponent().getLevel();
		int fightValue = player.getFightPower();

		return new MGLadderMember(playerId, playerName, level, fightValue);
	}

	public static synchronized MGLadderMember addLadderMember(Player player) {
		Preconditions.checkArgument(player != null);

		final int totalMembersNum = getLadderMembersNum();
		MGLadderMember member = createLadderMember(player);

		int memberRank = 0;
		String playerName = player.getName();
		if (totalMembersNum < MGLadderMacro.Max_Member_Count) {
			memberRank = totalMembersNum + 1;
			member.setRank(memberRank);
			member.setLastRank(memberRank);
			members.put(memberRank, member);
			nameToRankMapping.put(playerName, memberRank);
		} else {
			memberRank = MGLadderMacro.Max_Member_Count + 1;
			member.setRank(memberRank);
			member.setLastRank(memberRank);
			cacheMembers.put(playerName, member);
		}

		return member;
	}

	public static synchronized int getLadderMembersNum() {
		return members.size();
	}

	public static synchronized MGLadderMember getLadderCacheMemberByName(String playerName) {
		return cacheMembers.get(playerName);
	}

	public static synchronized MGLadderMember getLadderMemberByName(String playerName) {
		Integer rank = nameToRankMapping.get(playerName);

		MGLadderMember member = null;
		if (rank != null) {
			member = getLadderMemberByRank(rank);
		}
		
		if(member == null){
			member = getLadderCacheMemberByName(playerName);
		}
		
		return member;
	}

	public static synchronized MGLadderMember getLadderMemberByRank(int rank) {
		Preconditions.checkArgument(rank > 0 && rank <= MGLadderMacro.Max_Member_Count, "invalid rank=" + rank);
		return members.get(rank);
	}

	public static synchronized void removeChacheMember() {
		Iterator<Entry<String, MGLadderMember>> it = cacheMembers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, MGLadderMember> entry = it.next();
			String playerName = entry.getKey();
			Player player = playerManager.getOnlinePlayerByName(playerName);
			if (player != null) {
				continue;
			}
			
			it.remove();
		}
	}

	/**
	 * 获取名次上次最快的成员
	 * 
	 * @return
	 */
	public static synchronized MGLadderMember getMaxRankChangeLadderMember() {
		int maxRankChange = 0;
		MGLadderMember maxRankChangeMember = null;
		
		for (Entry<Integer, MGLadderMember> entry : members.entrySet()) {
			MGLadderMember member = entry.getValue();
			updateMemberRankChange(member);
			int rankChange = member.getRankChange();
			if (null == maxRankChangeMember) {
				maxRankChangeMember = member;
				maxRankChange = rankChange;
				continue;
			}

			if (rankChange == maxRankChange) {
				maxRankChangeMember = maxRankChangeMember.getStreak() > member.getStreak() ? maxRankChangeMember : member;
			} else if (rankChange > maxRankChange) {
				maxRankChangeMember = member;
			}
			
			maxRankChange = maxRankChangeMember.getRankChange();
		}
		
		return maxRankChangeMember;
	}

	/**
	 * 天梯前50名成员的信息
	 * 
	 * @param buffer
	 */
	public static byte[] writeLadderMessge() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		List<MGLadderMember> selectMemberList = selectMemberList();
		int count = selectMemberList.size();
		
		buffer.writeByte((byte) count);

		for (MGLadderMember member : selectMemberList) {
			
			byte gender = 0;
			byte profession = 1;
			int level = 0;
			int fightValue = 0;
			String unionName = "-";
			byte trend = MGLadderMacro.LadderTrend_Fair;

			String playerName = member.getPlayerName();
			Player player = playerManager.getPlayerByName(playerName);
			if (player != null) {
				gender = MGPropertyAccesser.getGender(player.getProperty());
				profession = player.getProfession();
				unionName = MGPropertyAccesser.getUnionName(player.getProperty());
				if (Strings.isNullOrEmpty(unionName)) {
					unionName = "-";
				}

				if (player.isOnline()) {
					level = player.getExpComponent().getLevel();
					fightValue = player.getFightPower();
				} else {
					level = member.getLevel();
					fightValue = member.getFightValue();
				}
				
				trend = member.getTrend();

			} else {
				logger.error("member is not exist! playerName = " + playerName);
			}

			buffer.writeByte((byte) member.getRank());
			buffer.writeByte(gender);
			buffer.writeString(playerName);
			buffer.writeByte(profession);
			buffer.writeShort((short) level);
			buffer.writeInt(fightValue);
			buffer.writeString(unionName);
			buffer.writeByte(trend);
		}

		return buffer.getData();
	}

	public static synchronized List<MGLadderMember> selectMemberList() {
		List<MGLadderMember> memberList = new ArrayList<MGLadderMember>();

		int totalMemberNum = members.size();

		int count = totalMemberNum > MGLadderMacro.Default_ShowLadderMembers_Count ? MGLadderMacro.Default_ShowLadderMembers_Count : totalMemberNum;

		for (int rank = 1; rank <= count; rank++) {
			MGLadderMember member = members.get(rank);
			memberList.add(member);
		}
		
		return memberList;
	}

	public static int challengeValidCheck(Player player, int targetRank, MGLadderMember fightMember, MGLadderMember targetMember) {
		Preconditions.checkArgument(player != null);

		// String fighterName = player.getName();

		if (fightMember == null) {
			logger.error("player = " + player + " is not a ladderMember.can't challenge");
			return MGErrorCode.CODE_Ladder_NotLadderMember;
		}

		if (fightMember.getRemainChallengeCount() <= 0) {
			return MGErrorCode.CODE_Ladder_ChallengeCountIsZero;
		}

		if (fightMember.getCDTime() > 0) {
			return MGErrorCode.CODE_Ladder_CDTimeNotCode;
		}

		if (targetMember == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("targetRank = " + targetRank + " member is not exist!");
			}

			return MGErrorCode.CODE_Ladder_Invalid_Challenge_Rank;
		}
		
		if (StringUtils.equals(targetMember.getPlayerName(), fightMember.getPlayerName())) {
			return MGErrorCode.CODE_Ladder_CannotChallenge_Self;
		}

		// String targetName = targetMember.getPlayerName();
		// Player target = playerManager.getPlayerByName(targetName);
		//
		// if (target == null) {
		// logger.error("target is null! target player name = " + targetName);
		// return MGErrorCode.CODE_Ladder_Invalid_Challenge_Rank;
		// }

		if (fightMember.getInBattle().get() || targetMember.getInBattle().get()) {
			logger.debug("处于战斗中");
			return MGErrorCode.CODE_Ladder_InBattle;
		}

		return MGSuccessCode.CODE_SUCCESS;
	}

	public static synchronized boolean switchRank(MGLadderMember fighterMember, MGLadderMember targetMember) {
		
		int fighterRank = fighterMember.getRank();
		int targetRank = targetMember.getRank();
		// 排名靠前者挑战获胜，不调换位置
		if (fighterRank < targetRank) {
			return false;
		}

		String fighterName = fighterMember.getPlayerName();
		String targetName = targetMember.getPlayerName();
		
		fighterMember.setRank(targetRank);
		members.put(targetRank, fighterMember);
		nameToRankMapping.put(fighterName, targetRank);
		targetMember.setRank(fighterRank);

		if (fighterRank > MGLadderMacro.Max_Member_Count) {
			nameToRankMapping.remove(targetName);
			cacheMembers.put(targetName, targetMember);
			cacheMembers.remove(fighterName);
		} else {
			members.put(fighterRank, targetMember);
			nameToRankMapping.put(targetName, fighterRank);
		}
		
		return true;
	}

	public static void updateMemberTrend(MGLadderMember member, int oldRank, int newRank) {
		Preconditions.checkArgument(member != null);
		Preconditions.checkArgument(oldRank > 0 && newRank > 0);

		byte trend = -1;
		if (oldRank == newRank) {
			trend = MGLadderMacro.LadderTrend_Fair;
		} else if (oldRank < newRank) {
			trend = MGLadderMacro.LadderTrend_Down;
		} else {
			trend = MGLadderMacro.LadderTrend_Up;
		}

		member.setTrend(trend);
	}

	public static void updateMemberRankChange(MGLadderMember member) {
		int lastRank = member.getLastRank();
		int rank = member.getRank();
		
		updateMemberTrend(member, lastRank, rank);
		
		member.setRankChange(lastRank - rank);
		member.setLastRank(rank);
	}

	public static synchronized void resetAllMemberChallengeCount() {
		for (Entry<Integer, MGLadderMember> entry : members.entrySet()) {
			MGLadderMember ladderMember = entry.getValue();
			ladderMember.resetChallengetCount();
			ladderMember.resetStreak();
			ladderMember.setCDBeginTime(0);
		}
		
		for (Entry<String, MGLadderMember> entry : cacheMembers.entrySet()) {
			MGLadderMember chcheLadderMember = entry.getValue();
			chcheLadderMember.resetChallengetCount();
			chcheLadderMember.resetStreak();
			chcheLadderMember.setCDBeginTime(0);
		}
	}

	public static synchronized void modifyAllMemberRewardRank() {
		for (MGLadderMember member : members.values()) {
			
			// 有未领取的奖励
			if (member.getRewardRank() != MGLadderMacro.Default_RewardRank) {
				continue;
			}
			
			// 给到新的奖励
			member.setRewardRank(member.getRank());
		}
	}

	public static List<Integer> getMembersRankBefore(int myRank) {
		Preconditions.checkArgument(myRank > 0, "rank can't less than 0");

		List<Integer> ranks = new ArrayList<Integer>();
		int size = getLadderMembersNum();
		if (myRank <= 6) {
			size = size > 6 ? 6 : size;
			for (int i = 1; i <= size; i++) {
				if (i == myRank) {
					continue;
				}
				ranks.add(i);
			}
		} else if (myRank <= 200) {
			for (int i = 5; i > 0; i--) {
				ranks.add(myRank - i);
			}
		} else if (myRank <= 500) {
			for (int i = 15; i > 0; i -= 3) {
				ranks.add(myRank - i);
			}
		} else if (myRank <= 900) {
			for (int i = 25; i > 0; i -= 5) {
				ranks.add(myRank - i);
			}
		} else if (myRank <= 1000) {
			for (int i = 50; i > 0; i -= 10) {
				ranks.add(myRank - i);
			}
		} else {
			int[] result = SFRandomUtils.randomArray(901, 1000, 5);
			for (int value : result) {
				ranks.add(value);
			}
		}

		return ranks;
	}

	public static synchronized void setMembers(Map<Integer, MGLadderMember> members) {
		MGLadderMemberMgr.members = members;
	}

	public static synchronized void setNameToRankMapping(Map<String, Integer> nameToRankMapping) {
		MGLadderMemberMgr.nameToRankMapping = nameToRankMapping;
	}
	
	public static synchronized void updateMemberInfo(byte battlefieldType, Player fighter, Player target, MGLadderMember fighterMember, MGLadderMember targetMember) {

		// 主动发起战斗战胜，调换位置并通知
		if (battlefieldType == Battlefield.Initiative_FighterLower_TargetHigh_Win) {
			boolean switchResult = switchRank(fighterMember, targetMember);
			if (switchResult && targetMember.getRank() > MGLadderMacro.Max_Member_Count) {
				MGLadderMemberSaver.getInstance().insertMGLadderMember(fighterMember);
				MGStatFunctions.arenaStat(fighter, fighterMember);
				MGLadderMemberSaver.getInstance().deleteMGLadderMember(targetMember);
			}
			
			// 更新奖励
			MessageSender.getInstance().updateRewardInfo(fighter, fighterMember);
			MessageSender.getInstance().updateRewardInfo(target, targetMember);

			// 更新挑战对象
			MessageSender.getInstance().updateEnemyInfo(fighter, fighterMember);
			MessageSender.getInstance().updateEnemyInfo(target, targetMember);
		}

		// 主动发起战斗兵战败，更新CDTime和剩余挑战次数,连胜和趋势
		if (battlefieldType == Battlefield.Initiative_FighterHigh_TargetLower_Defeat || battlefieldType == Battlefield.Initiative_FighterLower_TargetHigh_Defeat) {
			MessageSender.getInstance().updateCDTime(fighter, fighterMember, System.currentTimeMillis());

			fighterMember.resetStreak();
			targetMember.addStreak();

			if (logger.isDebugEnabled()) {
				logger.debug("victor=" + targetMember.getPlayerName() + ", rank=" + targetMember.getRank() + ", victim=" + fighterMember.getPlayerName() + ", rank="
						+ fighterMember.getRank());
			}

		} else {
			targetMember.resetStreak();
			fighterMember.addStreak();

			if (logger.isDebugEnabled()) {
				logger.debug("victor=" + fighterMember.getPlayerName() + ", rank=" + fighterMember.getRank() + ", victim=" + targetMember.getPlayerName() + ", rank="
						+ targetMember.getRank());
			}

		}
		fighterMember.subRemainChallengeCount();
	}

}
