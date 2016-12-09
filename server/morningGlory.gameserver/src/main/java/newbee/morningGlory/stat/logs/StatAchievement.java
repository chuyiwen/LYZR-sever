package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatAchievement extends AbstractStatLog{

	public static RecyclePool<StatAchievement> Pool = new RecyclePool<StatAchievement>() {

		@Override
		protected StatAchievement instance() {
			return new StatAchievement();
		}

		@Override
		protected void onRecycle(StatAchievement obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.Achievement;
	}

	public void setAchievementRefId(String achievementRefId){
		data.s1 = achievementRefId;
	}
	
	public void setAchievementType(int type){
		data.n1 = type;
	}
	
	public void setCompleteCondition(int completeCondition){
		data.n2 = completeCondition;
	}
}
