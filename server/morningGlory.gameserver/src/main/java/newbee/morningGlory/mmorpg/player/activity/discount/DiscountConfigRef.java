package newbee.morningGlory.mmorpg.player.activity.discount;

import sophia.game.ref.AbstractGameRefObjectBase;

public class DiscountConfigRef extends AbstractGameRefObjectBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2199325637279499150L;

	private short batch;// 商品批次
	private String beginTime;
	private String endTime;

	public short getBatch() {
		return batch;
	}

	public void setBatch(short batch) {
		this.batch = batch;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;

	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
