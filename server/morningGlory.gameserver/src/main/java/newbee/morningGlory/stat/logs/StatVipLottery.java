package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatVipLottery extends AbstractStatLog {
	
	
	
	public static RecyclePool<StatVipLottery> Pool = new RecyclePool<StatVipLottery>() {

		@Override
		protected StatVipLottery instance() {
			return new StatVipLottery();
		}

		@Override
		protected void onRecycle(StatVipLottery obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.VipLottery;
	}
	
	public void setCount(int count) {
		data.n1 = count;
	}
	
	public void setItemRefId(String itemRefId) {
		data.s1 = itemRefId;
	}
}
