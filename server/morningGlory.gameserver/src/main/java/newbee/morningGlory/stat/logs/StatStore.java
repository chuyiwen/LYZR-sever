package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatStore extends AbstractStatLog {
	
	public static final byte PersonLimit = 1;
	public static final byte ServerLimit = 2;
	
	public static RecyclePool<StatStore> Pool = new RecyclePool<StatStore>() {

		@Override
		protected StatStore instance() {
			return new StatStore();
		}

		@Override
		protected void onRecycle(StatStore obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.Store;
	}
	
	public void setOptType(int type) {
		data.n1 = type;
	}
	
	public void setItemRefId(String itemRefId) {
		data.s1 = itemRefId;
	}
	
	public void setItemNum(int itemNum) {
		data.n2 = itemNum;
	}
	
	public void setCostMoney(int costMoney) {
		data.n3 = costMoney;
	}
	
	public void setMoneyType(int moneyType) {
		data.n4 = moneyType;
	}
	
	public void setStoreItemId(String storeItemId) {
		data.s2 = storeItemId;
	}
}