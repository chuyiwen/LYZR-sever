package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatDigs extends AbstractStatLog{
	//操作类型 挖宝
	public static final byte Dig = 1;
	//提取
	public static final byte Get = 2;
	
	
	public static RecyclePool<StatDigs> Pool = new RecyclePool<StatDigs>() {

		@Override
		protected StatDigs instance() {
			return new StatDigs();
		}

		@Override
		protected void onRecycle(StatDigs obj) {
			obj.clear();
		}
	};

	@Override
	public byte getStatLogType() {
		return StatLogType.Digs;
	}

	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	public void setOptType(byte type){
		data.n1 = type;
	}
	
	public void setCount(int count) {
		data.n2 = count;
	}
	
	public void setTime(long time) {
		data.n3 = time;
	}

}
