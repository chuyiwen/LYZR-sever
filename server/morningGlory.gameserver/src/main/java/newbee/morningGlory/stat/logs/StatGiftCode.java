package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatGiftCode extends AbstractStatLog {
	

	
	public static RecyclePool<StatGiftCode> Pool = new RecyclePool<StatGiftCode>() {

		@Override
		protected StatGiftCode instance() {
			return new StatGiftCode();
		}

		@Override
		protected void onRecycle(StatGiftCode obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.GiftCode;
	}
	public void setKeyCode(String keyCode) {
		data.s1 = keyCode;
	}
	public void setResult(int result) {
		data.n1 = result;
	}
	

	
}
