package sophia.mmorpg.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.utils.RecyclePool;

public class StatOnline extends AbstractStatLog{

	public static RecyclePool<StatOnline> Pool = new RecyclePool<StatOnline>() {

		@Override
		protected StatOnline instance() {
			return new StatOnline();
		}

		@Override
		protected void onRecycle(StatOnline obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.online;
	}
	
	public void setOnlineTime(int onlineTime){
		data.n1 = onlineTime;
	}

}	
