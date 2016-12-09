package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatPeerage extends AbstractStatLog {

	public static RecyclePool<StatPeerage> Pool = new RecyclePool<StatPeerage>() {

		@Override
		protected StatPeerage instance() {
			return new StatPeerage();
		}

		@Override
		protected void onRecycle(StatPeerage obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.Peerage;
	}

	public void setPeerageRefId(String peerageRefId) {
		data.s1 = peerageRefId;
	}

	public void setPeerageLevel(int peerageLevel) {
		data.n1 = peerageLevel ;
	}
}
