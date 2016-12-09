package newbee.morningGlory.mmorpg.ladder.persistence;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.ladder.MGLadderMember;
import newbee.morningGlory.mmorpg.player.activity.ladder.CombatRecord;
import sophia.foundation.util.ByteArrayReadWriteBuffer;

public class LadderPersistenceObject {
	private static LadderPersistenceObject instance = new LadderPersistenceObject();

	private LadderPersistenceObject() {

	}

	public static LadderPersistenceObject getInstance() {
		return instance;
	}

	public byte[] ladderMemberInfoToBytes(MGLadderMember member) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		String playerName = member.getPlayerName();
		int remainChallengeCount = member.getRemainChallengeCount();
		int rank = member.getRank();
		byte trend = member.getTrend();
		long enterTime = member.getEnterTime();
		int streak = member.getStreak();
		long CDBeginTime = member.getCDBeginTime();
		int level = member.getLevel();
		int fightValue = member.getFightValue();
		int rewardRank = member.getRewardRank();

		buffer.writeInt(10000);
		buffer.writeString(playerName);
		buffer.writeInt(remainChallengeCount);
		buffer.writeInt(rank);
		buffer.writeByte(trend);
		buffer.writeLong(enterTime);
		buffer.writeInt(streak);
		buffer.writeLong(CDBeginTime);
		buffer.writeInt(level);
		buffer.writeInt(fightValue);
		buffer.writeInt(rewardRank);

		List<CombatRecord> records = member.getRecords();

		int index = 0;
		if (records.size() < 5) {
			index = records.size();
		} else {
			index = 5;
		}

		List<CombatRecord> newRecords = new ArrayList<>(records.subList(0, index));

		buffer.writeInt(newRecords.size());
		for (CombatRecord record : newRecords) {
			buffer.writeByte(record.getIsAction());
			buffer.writeString(record.getName());
			buffer.writeByte(record.getResult());
			buffer.writeInt(record.getRankChange());
		}

		return buffer.getData();
	}

	public MGLadderMember ladderMemberInfoFromBytes(String id, byte[] memberData) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(memberData);
		MGLadderMember member = new MGLadderMember();
		member.setId(id);

		buffer.readInt();
		String playerName = buffer.readString();
		int remainChallengeCount = buffer.readInt();
		int rank = buffer.readInt();
		byte trend = buffer.readByte();
		long enterTime = buffer.readLong();
		int streak = buffer.readInt();
		long CDBeginTime = buffer.readLong();
		int level = buffer.readInt();
		int fightValue = buffer.readInt();
		int rewardRank = buffer.readInt();

		member.setPlayerName(playerName);
		member.setRemainChallengeCount(remainChallengeCount);
		member.setRank(rank);
		member.setTrend(trend);
		member.setEnterTime(enterTime);
		member.setStreak(streak);
		member.setCDBeginTime(CDBeginTime);
		member.setLevel(level);
		member.setFightValue(fightValue);
		member.setRewardRank(rewardRank);
		int size = buffer.readInt();
		
		for (int i = 0; i < size; i++) {
			byte action = buffer.readByte();
			String name = buffer.readString();
			byte result = buffer.readByte();
			int rankChange = buffer.readInt();
			CombatRecord record = new CombatRecord(action, name, result, rankChange);
			member.addRecords(record);
		}

		return member;
	}

}
