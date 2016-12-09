package newbee.morningGlory.mmorpg.ladder;

import java.util.LinkedList;
import java.util.UUID;

import newbee.morningGlory.mmorpg.player.activity.ladder.CombatRecord;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;

public class MGLadderMember {
	// 是否在战斗中
	private AtomicBoolean inBattle = new AtomicBoolean(false);
	// 战斗记录
	public LinkedList<CombatRecord> records = new LinkedList<CombatRecord>();

	private int remainChallengeCount;
	private int rank;
	private String playerName;
	private String playerId;
	private byte trend;
	private long enterTime;
	private int streak;
	private long CDBeginTime;
	private int level;
	private int fightValue;
	private int rankChange;
	private int rewardRank;
	private String id;
	private int lastRank;

	public MGLadderMember() {
		setId(UUID.randomUUID().toString());
	}

	public MGLadderMember(String playerId, String playerName, int level, int fightValue) {
		setId(UUID.randomUUID().toString());
		this.playerId = playerId;
		this.playerName = playerName;
		this.level = level;
		this.fightValue = fightValue;
		this.trend = MGLadderMacro.LadderTrend_Fair;
		this.remainChallengeCount = MGLadderMacro.Default_Challenge_Count;
		this.rewardRank = MGLadderMacro.Default_RewardRank;
		this.enterTime = System.currentTimeMillis();
		this.streak = 0;
		this.CDBeginTime = 0;
		this.rankChange = 0;
		this.lastRank = 0;
	}
	
	public int getLastRank() {
		return lastRank;
	}
	
	public void setLastRank(int lastRank) {
		this.lastRank = lastRank;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		setLastRank(this.rank);
		this.rank = rank;
	}

	public byte getTrend() {
		return trend;
	}

	public void setTrend(byte trend) {
		this.trend = trend;
	}

	public synchronized int getStreak() {
		return streak;
	}

	public synchronized void setStreak(int streak) {
		this.streak = streak;
	}

	public synchronized void resetStreak() {
		this.streak = 0;
	}

	public synchronized void addStreak() {
		this.streak++;
	}

	public synchronized int getRemainChallengeCount() {
		return remainChallengeCount;
	}

	public synchronized void setRemainChallengeCount(int remainChallengeCount) {
		this.remainChallengeCount = remainChallengeCount;
	}

	public synchronized void subRemainChallengeCount() {
		this.remainChallengeCount--;
	}

	public synchronized void resetChallengetCount() {
		this.remainChallengeCount = MGLadderMacro.Default_Challenge_Count;
	}

	public int getFightValue() {
		return fightValue;
	}

	public void setFightValue(int fightValue) {
		this.fightValue = fightValue;
	}

	public int getCDTime() {
		int remainCDTime = MGLadderMacro.Default_CDTIME - (int) ((System.currentTimeMillis() - CDBeginTime) / 1000);
		remainCDTime = remainCDTime < 0 ? 0 : remainCDTime;
		return remainCDTime;
	}

	public long getCDBeginTime() {
		return CDBeginTime;
	}

	public void setCDBeginTime(long cDBeginTime) {
		CDBeginTime = cDBeginTime;
	}

	public int getRankChange() {
		return rankChange;
	}

	public void setRankChange(int rankChange) {
		this.rankChange = rankChange;
	}

	public int getRewardRank() {
		return rewardRank;
	}

	public void setRewardRank(int rewardRank) {
		this.rewardRank = rewardRank;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}

	public AtomicBoolean getInBattle() {
		return inBattle;
	}

	public LinkedList<CombatRecord> getRecords() {
		return records;
	}

	public void setRecords(LinkedList<CombatRecord> records) {
		this.records = records;
	}

	public void addRecords(CombatRecord record) {
		this.records.addFirst(record);
	}

	@Override
	public String toString() {
		return "MGLadderMember [remainChallengeCount=" + remainChallengeCount + ", rank=" + rank + ", playerName=" + playerName + ", playerId=" + playerId + ", level=" + level
				+ ", rewardRank=" + rewardRank + ", id=" + id + "]";
	}

	
}
