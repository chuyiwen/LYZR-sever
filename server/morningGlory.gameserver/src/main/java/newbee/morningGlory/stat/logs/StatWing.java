package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatWing extends AbstractStatLog {
	
	public static final byte Add = 1;
	public static final byte LevelUp = 2;
	
	public static RecyclePool<StatWing> Pool = new RecyclePool<StatWing>() {

		@Override
		protected StatWing instance() {
			return new StatWing();
		}

		@Override
		protected void onRecycle(StatWing obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.Wing;
	}
	
	public void setOptType(byte type) {
		data.n1 = type;
	}
	
	public void setStageLevel(byte stageLevel) {
		data.n2 = stageLevel;
	}
	
	public void setStarLevel(byte starLevel) {
		data.n3 = starLevel;
	}
	
	public void setExp(long exp) {
		data.n4 = exp;
	}

	public void setWingRefId(String wingRefId) {
		data.s1 = wingRefId;
	}
}
