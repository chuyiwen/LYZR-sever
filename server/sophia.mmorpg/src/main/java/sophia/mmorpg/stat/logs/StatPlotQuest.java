package sophia.mmorpg.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.utils.RecyclePool;

public class StatPlotQuest extends AbstractStatLog {
	
	public static final byte Accept = 1;
	public static final byte Finish = 2;
	public static final byte Reward = 3;

	public static RecyclePool<StatPlotQuest> Pool = new RecyclePool<StatPlotQuest>() {

		@Override
		protected StatPlotQuest instance() {
			return new StatPlotQuest();
		}

		@Override
		protected void onRecycle(StatPlotQuest obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.PlotQuest;
	}
	
	public void setOptType(byte type) {
		data.n1 = type;
	}
	
	public void setQuestRefId(String questRefID) {
		data.s1 = questRefID;
	}

}
