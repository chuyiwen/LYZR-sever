package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatPlayerChangeProperty extends AbstractStatLog{
	
	public static RecyclePool<StatPlayerChangeProperty> Pool = new RecyclePool<StatPlayerChangeProperty>() {

		@Override
		protected StatPlayerChangeProperty instance() {
			return new StatPlayerChangeProperty();
		}

		@Override
		protected void onRecycle(StatPlayerChangeProperty obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.PlayerChangeProperty;
	}
	
	public void setCommandstr(String cmdstr){
		data.s1 = cmdstr;
	}
	
}
