package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatSign extends AbstractStatLog{
	public static final byte Normal_Sign = 1;
	
	public static final byte Makeup_Sign = 2;
	
	public static RecyclePool<StatSign> Pool = new RecyclePool<StatSign>() {

		@Override
		protected StatSign instance() {
			return new StatSign();
		}

		@Override
		protected void onRecycle(StatSign obj) {
			obj.clear();
		}
	};

	@Override
	public byte getStatLogType() {
		return StatLogType.sign;
	}

	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	public void setOptType(byte optType) {
		data.n1 = optType;
	}
	
	public void setSignDay(byte day) {
		data.n2 = day;
	}
	
	public void setSignCount(byte count) {
		data.n3 = count;
	}

}
