package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatGameInstance extends AbstractStatLog {
	
	public static final byte Enter = 1;
	public static final byte Finish = 2;
	public static final byte NextLayer = 3;
	public static final byte Leave = 4;

	public static RecyclePool<StatGameInstance> Pool = new RecyclePool<StatGameInstance>() {

		@Override
		protected StatGameInstance instance() {
			return new StatGameInstance();
		}

		@Override
		protected void onRecycle(StatGameInstance obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.GameInstance;
	}
	
	public void setOptType(byte type) {
		data.n1 = type;
	}
	
	public void setGameInstanceRefId(String gameInstanceRefId) {
		data.s1 = gameInstanceRefId;
	}
	
	public void setGameInstanceSceneId(String gameInstanceSceneId) {
		data.s2 = gameInstanceSceneId;
	}
	
}
