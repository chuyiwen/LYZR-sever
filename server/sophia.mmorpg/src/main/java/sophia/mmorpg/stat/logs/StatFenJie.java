package sophia.mmorpg.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.utils.RecyclePool;

public class StatFenJie extends AbstractStatLog{

	public static RecyclePool<StatFenJie> Pool = new RecyclePool<StatFenJie>() {

		@Override
		protected StatFenJie instance() {
			return new StatFenJie();
		}

		@Override
		protected void onRecycle(StatFenJie obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		// TODO Auto-generated method stub
		return StatLogType.FenJie;
	}
	
	public void setItemRefId(String itemRefId){
		data.s1 = itemRefId;
	}
	
	
}	
