package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatDigsItem extends AbstractStatLog {
	

	
	public static RecyclePool<StatDigsItem> Pool = new RecyclePool<StatDigsItem>() {

		@Override
		protected StatDigsItem instance() {
			return new StatDigsItem();
		}

		@Override
		protected void onRecycle(StatDigsItem obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.DigsItem;
	}
	public void setItemRefId(String itemRefId) {
		data.s1 = itemRefId;
	}
	public void setNumber(int number) {
		data.n1 = number;
	}
	public void setDigTime(long time) {
		data.n2 = time;
	}

	
}
