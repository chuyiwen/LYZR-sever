package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatUnion extends AbstractStatLog {

	public static final byte Add = 1;
	public static final byte Exit = 2;
	public static final byte create = 3;
	public static final byte kick = 4;
	public static final byte upgrade = 5;

	public static RecyclePool<StatUnion> Pool = new RecyclePool<StatUnion>() {

		@Override
		protected StatUnion instance() {
			return new StatUnion();
		}

		@Override
		protected void onRecycle(StatUnion obj) {
			obj.clear();
		}
	};

	@Override
	public void recycle() {
		Pool.recycle(this);
	}

	@Override
	public byte getStatLogType() {
		return StatLogType.Union;
	}

	public void setOptType(byte type) {
		data.n1 = type;
	}

	public void setUnionOfficialId(byte unionOfficialId) {
		data.n2 = unionOfficialId;
	}

	public void setUnionName(String unionName) {
		data.s1 = unionName;
	}

	public void setUnionId(String unionId) {
		data.s2 = unionId;
	}

}
