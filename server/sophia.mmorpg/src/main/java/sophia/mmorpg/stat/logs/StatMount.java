package sophia.mmorpg.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.utils.RecyclePool;

public class StatMount extends AbstractStatLog{

	public static RecyclePool<StatMount> Pool = new RecyclePool<StatMount>() {

		@Override
		protected StatMount instance() {
			return new StatMount();
		}

		@Override
		protected void onRecycle(StatMount obj) {
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
		return StatLogType.Mount;
	}
	
	public void setCrtRefId(String crtRefId){
		data.s1 = crtRefId;
	}
	
	public void setName(String name){
		data.s2 = name ;
	}
	
	public void setStartLevel(int startLevel){
		data.n1 = startLevel ;
	}
	
	public void setCrtExp(long exp){
		data.n2 = exp ;
	}
	
}	
