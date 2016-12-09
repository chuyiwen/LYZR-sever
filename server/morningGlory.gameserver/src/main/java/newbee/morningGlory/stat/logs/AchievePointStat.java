package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class AchievePointStat extends AbstractStatLog{
	// 成就点增加或者减少
	public static final byte Add = 1;
	
	public static final byte Delete = 2;
	
	// 成就点增加减少的来源或去向
	public static final byte Quest = 1;
	
	public static final byte AchieveToken = 2;
	
	public static final byte AchieveReward = 3;
	
	public static final byte Buy_medal = 4;
	
	public static RecyclePool<AchievePointStat> Pool = new RecyclePool<AchievePointStat>() {

		@Override
		protected AchievePointStat instance() {
			return new AchievePointStat();
		}

		@Override
		protected void onRecycle(AchievePointStat obj) {
			obj.clear();
		}
	};
	
	@Override
	public byte getStatLogType() {
		return StatLogType.achievePoint;
	}

	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	public void setOptType(byte optType) {
		data.n1 = optType;
	}
	
	public void setSourceType(byte sourceType) {
		data.n2 = sourceType;
	}
	
	public void addOrDeleteNumber(int number) {
		data.n3 = number;
	}
	
}
