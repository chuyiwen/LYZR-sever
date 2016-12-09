package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatVip extends AbstractStatLog {

	public static final byte Cancel = 1;
	public static final byte LevelUp = 2;

	
	public static RecyclePool<StatVip> Pool = new RecyclePool<StatVip>() {

		@Override
		protected StatVip instance() {
			return new StatVip();
		}

		@Override
		protected void onRecycle(StatVip obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.vip;
	}

	public void setOptType(byte type) {
		data.n1 = type;
	}

	public void setVipType(byte vipType) {
		data.n2 = vipType;
	}
	

}
