package newbee.morningGlory.mmorpg.player.activity.ladder;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.ladder.ArenaMgr;
import newbee.morningGlory.mmorpg.ladder.Battlefield;
import newbee.morningGlory.mmorpg.ladder.MGLadderMacro;
import newbee.morningGlory.mmorpg.ladder.MGLadderMember;
import newbee.morningGlory.mmorpg.ladder.MGLadderMemberMgr;
import newbee.morningGlory.mmorpg.ladder.MGLadderMgr;
import newbee.morningGlory.mmorpg.ladder.MGLadderSystemMessageFacade;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_Challenge;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_UpadateChallengeTarget;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_UpadateHeroInfo;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_UpdateChallengeCDTime;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.G2C_Arena_UpdateReceiveRewardTime;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.MGLadderDefines;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.IoBufferUtil;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Preconditions;

public class MessageSender {
	private static Logger logger = Logger.getLogger(MessageSender.class);

	private static MessageSender instance = new MessageSender();
	
	private MessageSender() {
	}
	
	public static MessageSender getInstance() {
		return instance;
	}
	
	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();

	public void writeAllListInfo(IoBuffer buffer, Player player, MGLadderMember member) {
		Preconditions.checkArgument(player != null );
		Preconditions.checkArgument(member != null);
		
		// 公告区域
		writeAnnouncementInfo(buffer);

		// 挑战对象区域
		writeEnemyInfo(buffer, player, member);

		// 战斗记录
		writeCombatRecord(buffer, member);

		// 个人信息区域
		writePersonalInfo(buffer, member);

		// 奖励区域
		writeRewardInfo(buffer, member);

		// CDTime
		writeRemainCDTime(buffer, member);

	}

	public void writeAnnouncementInfo(IoBuffer buffer) {
		MGLadderSystemMessageFacade.writeLadderSystemInfo(buffer);
	}

	public void writeRemainCDTime(IoBuffer buffer, MGLadderMember member) {
		int cdTime = member.getCDTime();
		buffer.putInt(cdTime);
	}

	public void writeRewardInfo(IoBuffer buffer, MGLadderMember member) {
		int rewardRank = member.getRewardRank();
		int getRewardRemainTime = 0;
		if (rewardRank == MGLadderMacro.Default_RewardRank) {
			rewardRank = member.getRank();
			long receiveRewardMillis = MGLadderMgr.getInstance().getReceiveRewardMillis();
			getRewardRemainTime = (int) ((receiveRewardMillis - System.currentTimeMillis()) / 1000);
			getRewardRemainTime = getRewardRemainTime < 0 ? 0 : getRewardRemainTime;
		}

		String refId = LadderRewardUtil.getArenaRewardRefIdByRank(rewardRank);
		ArenaRewardRef ref = LadderRewardUtil.getArenaRewardRef(refId);
		
		buffer.putInt(rewardRank);
		buffer.putInt(getRewardRemainTime);
		buffer.putInt(ref.getMeirtOrGoldReward(rewardRank, ArenaRewardRef.Type_Gold));
		buffer.putInt(ref.getMeirtOrGoldReward(rewardRank, ArenaRewardRef.Type_Merit));

	}

	public void writePersonalInfo(IoBuffer buffer, MGLadderMember member) {
		buffer.putInt(member.getRank());
		buffer.putShort((short) member.getStreak());
		buffer.putShort((short) member.getRemainChallengeCount());
				
	}

	public void writeEnemyInfo(IoBuffer buffer, Player player, MGLadderMember member) {
		int myRank = member.getRank();
		List<Integer> targetRanks = MGLadderMemberMgr.getMembersRankBefore(myRank);
		if (logger.isDebugEnabled()) {
			logger.debug("writeEnemyInfo " + targetRanks + ", myRank=" + myRank);
		}
		
		List<MGLadderMember> memberList = new ArrayList<>();
		for (int i = targetRanks.size() - 1; i >= 0; i--) {
			int rank = targetRanks.get(i);
			
			MGLadderMember targetMember = MGLadderMemberMgr.getLadderMemberByRank(rank);
			if (targetMember == null) {
				logger.error("writeEnemyInfo : member of rank = " + rank + " is null!");
				continue;
			}
			
			memberList.add(targetMember);
		}
		
		int position = buffer.position();   // buffer 位置
		buffer.put((byte) memberList.size());
		byte count = 0;
		for (MGLadderMember targetMember : memberList) {
			String targetMemberName = targetMember.getPlayerName();				
			Player target = playerManager.getPlayerByName(targetMemberName);     
			if (target != null) {
				buffer.put(target.getProfession());
				buffer.put(MGPropertyAccesser.getGender(target.getProperty()));
				buffer.putInt(targetMember.getRank());
				IoBufferUtil.putString(buffer, targetMemberName);

				// 等级和战力
				int targetLevel = 0;
				int targetFightValue = 0;
				if (target.isOnline()) {
					targetLevel = target.getExpComponent().getLevel();
					targetFightValue = target.getFightPower();
				} else {
					targetLevel = targetMember.getLevel();
					targetFightValue = targetMember.getFightValue();
				}

				buffer.putShort((short) targetLevel);
				buffer.putInt(targetFightValue);
				count++;
			} 
		}
		buffer.put(position,count);   // 更新玩家个数
		
	}

	/**
	 * 在records里取最后5条记录
	 * 
	 * @param buffer
	 */
	public synchronized void writeCombatRecord(IoBuffer buffer, MGLadderMember member) {
		int index = 0;
		List<CombatRecord> records = member.getRecords();
		
		if (records.size() < 5) {
			index = records.size();
		} else {
			index = 5;
		}
		List<CombatRecord> newRecords = new ArrayList<>(records.subList(0, index));

		buffer.put((byte) newRecords.size());
		for (int i = 0; i < newRecords.size(); i++) {
			CombatRecord record = newRecords.get(i);
			
			buffer.put(record.getIsAction());
			IoBufferUtil.putString(buffer, record.getName());
			buffer.put(record.getResult());
			buffer.putInt(record.getRankChange());
		}
	}
	
	// ====================================
	public void sendChllengeMemberPropertyMsg(Player player, Player target, byte fightResult, int[] randomFightPower) {
		PropertyDictionary property = PlayerConfig.getPdToClientFromPlayerPd(target);
		
		List<ItemPair> itemPairs = LadderRewardUtil.getRewardByMemberType(fightResult);
		int goldNum = 0;
		int meritNum = 0;
		for (ItemPair itemPair : itemPairs) {
			if (itemPair.isGold()) {
				goldNum += itemPair.getNumber();
			}

			if (itemPair.isMerit()) {
				meritNum += itemPair.getNumber();
			}
		}

		G2C_Arena_Challenge res = (G2C_Arena_Challenge) MessageFactory.getMessage(MGLadderDefines.G2C_Arena_Challenge);
		res.setFightResult(fightResult);
		res.setGoldNum(goldNum);
		res.setMeritNum(meritNum);
		res.setFightRandomValue(randomFightPower[0]);
		res.setTargetRandomValue(randomFightPower[1]);
		res.setProperty(property);
		sendMessage(player, res);
	}
	
	/** 更新挑战CD时间*/
	public void updateCDTime(Player player, MGLadderMember member, long CDBeginTime) {
		member.setCDBeginTime(CDBeginTime);
		G2C_Arena_UpdateChallengeCDTime res = MessageFactory.getConcreteMessage(MGLadderDefines.G2C_Arena_UpdateChallengeCDTime);
		res.setCdtime(member.getCDTime());
		
		sendMessage(player, res);
	}
	
	/** 发送战斗记录*/
	public void sendAndSaveBattlefield(byte battlefieldType, Player fighter, Player target, MGLadderMember fighterMember, MGLadderMember targetMember) {
		CombatRecord fighterRecord = ArenaMgr.getRecord(battlefieldType, fighterMember, targetMember);
		CombatRecord targetRecord = ArenaMgr.getRecord(Battlefield.reverseBattlefield(battlefieldType), fighterMember, targetMember);

		fighterMember.addRecords(fighterRecord);
		targetMember.addRecords(targetRecord);
		
		MGLadderSystemMessageFacade.sendLadderBattleMsg(fighter, fighterRecord);
		if (!fighter.getIdentity().getId().equals(target.getIdentity().getId())) {
			MGLadderSystemMessageFacade.sendLadderBattleMsg(target, targetRecord);
		}
	}
	
	/** 更新个人信息区域*/
	public void updatePersonalInfo(Player player, MGLadderMember member) {
		G2C_Arena_UpadateHeroInfo res = (G2C_Arena_UpadateHeroInfo) MessageFactory.getMessage(MGLadderDefines.G2C_Arena_UpadateHeroInfo);
		res.setMember(member);
		sendMessage(player, res);
	}

	/** 更新挑战对象信息区域*/
	public void updateEnemyInfo(Player player, MGLadderMember member) {
		G2C_Arena_UpadateChallengeTarget res = (G2C_Arena_UpadateChallengeTarget) MessageFactory.getMessage(MGLadderDefines.G2C_Arena_UpadateChallengeTarget);
		res.setPlayer(player);
		res.setMember(member);
		sendMessage(player, res);
	}
	
	/** 更新奖励信息区域*/
	public void updateRewardInfo(Player owner, MGLadderMember ladderMember) {
		if ((ladderMember.getRewardRank() == MGLadderMacro.Default_RewardRank)) {
			G2C_Arena_UpdateReceiveRewardTime res = (G2C_Arena_UpdateReceiveRewardTime) MessageFactory.getMessage(MGLadderDefines.G2C_Arena_UpdateReceiveRewardTime);
			res.setMember(ladderMember);
			sendMessage(owner, res);
		}
	}
	
	private void sendMessage(Player player, ActionEventBase res) {
		if (!player.isOnline()) {
			return;
		}
		
		GameRoot.sendMessage(player.getIdentity(), res);
	}

}
