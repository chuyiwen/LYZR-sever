package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatTalisman extends AbstractStatLog {

	public static final byte Get = 1;
	public static final byte Active = 2;
	public static final byte LevelUp = 3;
	public static final byte UnActive = 4;
	
	public static RecyclePool<StatTalisman> Pool = new RecyclePool<StatTalisman>() {

		@Override
		protected StatTalisman instance() {
			return new StatTalisman();
		}

		@Override
		protected void onRecycle(StatTalisman obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.Talisman;
	}

	public void setOptType(byte type) {
		data.n1 = type;
	}

	public void setTalismanRefId(String talismanRefId){
		data.s1 = talismanRefId;
	}
	public void setCrtLevel(int level){
		data.n2 = level;
	}
	public void setCrtState(int state){
		data.n3 = state;
	}
}
