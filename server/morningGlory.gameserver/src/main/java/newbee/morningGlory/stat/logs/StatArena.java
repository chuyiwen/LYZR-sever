package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatArena extends AbstractStatLog{

	public static RecyclePool<StatArena> Pool = new RecyclePool<StatArena>() {

		@Override
		protected StatArena instance() {
			return new StatArena();
		}

		@Override
		protected void onRecycle(StatArena obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.Arena;
	}

	public void setRank(int rank){
		data.n1 = rank;
	}
	
	public void setStreak(int streak){
		data.n2 = streak;
	}
	
	public void setRemainChallengeCount(int remainChallengeCount){
		data.n3 = remainChallengeCount;
	}
	
	public void setRewardRank(int rewardRank){
		data.n4 = rewardRank;
	}
}
