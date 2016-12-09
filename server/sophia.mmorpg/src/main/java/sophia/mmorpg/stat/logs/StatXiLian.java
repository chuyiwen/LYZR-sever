package sophia.mmorpg.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.utils.RecyclePool;

public class StatXiLian extends AbstractStatLog{

	public static RecyclePool<StatXiLian> Pool = new RecyclePool<StatXiLian>() {

		@Override
		protected StatXiLian instance() {
			return new StatXiLian();
		}

		@Override
		protected void onRecycle(StatXiLian obj) {
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
		return StatLogType.XiLian;
	}
	
	public void setItemRefId(String itemRefId){
		data.s1 = itemRefId;
	}

}	
