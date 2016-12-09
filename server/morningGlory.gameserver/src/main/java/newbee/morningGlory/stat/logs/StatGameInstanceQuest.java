package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatGameInstanceQuest extends AbstractStatLog {
	
	public static final byte Accept = 1;
	public static final byte Finish = 2;
	public static final byte Reward = 3;

	public static RecyclePool<StatGameInstanceQuest> Pool = new RecyclePool<StatGameInstanceQuest>() {

		@Override
		protected StatGameInstanceQuest instance() {
			return new StatGameInstanceQuest();
		}

		@Override
		protected void onRecycle(StatGameInstanceQuest obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.GameInstanceQuest;
	}
	
	public void setOptType(byte type) {
		data.n1 = type;
	}
	
	public void setQuestRefId(String questRefID) {
		data.s1 = questRefID;
	}
	
	public void setGameInstanceRefId(String gameInstanceRefId) {
		data.s2 = gameInstanceRefId;
	}

	public void setGameInstanceSceneId(String gameInstanceSceneId) {
		data.s3 = gameInstanceSceneId;
	}
}
