package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatFund extends AbstractStatLog{
	// 购买基金
	public static final byte Buy = 1;
	
	// 领取基金
	public static final byte Get = 2;
	
	
	public static RecyclePool<StatFund> Pool = new RecyclePool<StatFund>() {

		@Override
		protected StatFund instance() {
			return new StatFund();
		}

		@Override
		protected void onRecycle(StatFund obj) {
			obj.clear();
		}
	};

	@Override
	public byte getStatLogType() {
		return StatLogType.Fund;
	}

	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	public void setOptType(byte type){
		data.n1 = type;
	}
	
	public void setFundType(byte type) {
		data.n2 = type;
	}
	
	/**
	 * opt为购买 day = 0 ; 其他的为领取基金的指定日
	 * @param day
	 */
	public void setGetFundDay(byte day){
		data.n3 = day;
	}
	
	public void setMoneyType(byte moneyType) {
		data.n4 = moneyType;
	}
	
	public void setMoneyNum(long moneyNum) {
		data.n5 = moneyNum;
	}

}
