package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatDailyQuest extends AbstractStatLog {

	public static final byte Accept = 1;
	public static final byte Finish = 2;
	public static final byte Reward = 3;

	public static RecyclePool<StatDailyQuest> Pool = new RecyclePool<StatDailyQuest>() {

		@Override
		protected StatDailyQuest instance() {
			return new StatDailyQuest();
		}

		@Override
		protected void onRecycle(StatDailyQuest obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	@Override
	public byte getStatLogType() {
		return StatLogType.DailyQuest;
	}
	
	public void setOptType(byte type) {
		data.n1 = type;
	}
	
	public void setQuestRefId(String questRefID) {
		data.s1 = questRefID;
	}
	
	public void setMonsterRefId(String monsterRefId) {
		data.s2 = monsterRefId;
	}
	
	public void setDailyQuestNowTime(int time) {
		data.n2 = time;
	}
	
	public void setVipAddRingTime(int addTime) {
		data.n3 = addTime;
	}
	
	public void setDailyQuestStartLevel(int level) {
		data.n4 = level;
	}
}
