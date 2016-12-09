package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class MeritPointStat extends AbstractStatLog {

	// 功勋点增加或者减少
	public static final byte Add = 1;

	public static final byte Delete = 2;

	// 功勋点增加减少的来源或去向
	public static final byte Quest = 1;

	public static final byte MeritToken = 2;

	public static final byte MeritReward = 3;

	public static final byte Knight_LevelUp = 4;

	public static RecyclePool<MeritPointStat> Pool = new RecyclePool<MeritPointStat>() {

		@Override
		protected MeritPointStat instance() {
			return new MeritPointStat();
		}

		@Override
		protected void onRecycle(MeritPointStat obj) {
			obj.clear();
		}
	};

	@Override
	public byte getStatLogType() {
		return StatLogType.Merit;
	}

	@Override
	public void recycle() {
		Pool.recycle(this);
	}

	public void setOptType(byte optType) {
		data.n1 = optType;
	}

	public void setSourceType(byte sourceType) {
		data.n2 = sourceType;
	}

	public void addOrDeleteNumber(int number) {
		data.n3 = number;
	}

}
