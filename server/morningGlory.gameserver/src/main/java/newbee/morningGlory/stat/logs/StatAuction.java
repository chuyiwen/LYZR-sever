package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatAuction extends AbstractStatLog {

	public static final byte Buy = 1;
	public static final byte Sell = 2;
	public static final byte cancelSell = 3;
	public static final byte Expried = 4;
	public static final byte BeSell = 5;
	public static RecyclePool<StatAuction> Pool = new RecyclePool<StatAuction>() {

		@Override
		protected StatAuction instance() {
			return new StatAuction();
		}

		@Override
		protected void onRecycle(StatAuction obj) {
			obj.clear();
		}
	};

	@Override
	public void recycle() {
		Pool.recycle(this);
	}

	@Override
	public byte getStatLogType() {
		return StatLogType.auction;
	}

	public void setOptType(byte type) {
		data.n1 = type;
	}

	public void setNumber(int number) {
		data.n2 = number;
	}

	public void setStrengThenLevel(byte strengThenLevel) {
		data.n3 = strengThenLevel;
	}

	public void setMoney(int money) {
		data.n4 = money;
	}

	public void setItemRefId(String itemRefId) {
		data.s1 = itemRefId;
	}

	public void setSellerName(String seller) {
		data.s2 = seller;
	}

	public void setOldItemId(String itemId) {
		data.s3 = itemId;
	}

	public void setWashPd(String washPd) {
		data.s4 = washPd;
	}

	public void setNewItemId(String itemId) {
		data.s5 = itemId;
	}

}
