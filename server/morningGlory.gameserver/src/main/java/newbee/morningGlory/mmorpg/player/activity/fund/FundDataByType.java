package newbee.morningGlory.mmorpg.player.activity.fund;

import newbee.morningGlory.mmorpg.player.activity.Utils;

/**
 * 基金数据
 * 
 * @author lixing
 * 
 */
public class FundDataByType {

	private FundType type;// 基金类型
	private long buyFundTime = -1;// 购买的时间
	private byte[] getRewardRecord = new byte[0];// 领取的记录 默认0为未领取，1为领取过

	public FundDataByType(FundType type) {
		super();
		this.type = type;
	}

	public FundType getType() {
		return type;
	}

	public void setType(FundType type) {
		this.type = type;
	}

	public long getBuyFundTime() {
		return buyFundTime;
	}

	public void setBuyFundTime(long buyFundTime) {
		this.buyFundTime = buyFundTime;
	}

	public byte[] getGetRewardRecord() {
		return getRewardRecord;
	}

	public void setGetRewardRecord(byte[] getRewardRecord) {
		this.getRewardRecord = getRewardRecord;
	}

	/**
	 * 获取某天的领奖记录
	 * 
	 * @param day
	 * @return
	 */
	public byte getGetRewardRecordByDay(int day) {
		return getRewardRecord[day - 1];
	}

	/**
	 * 今天距离购买日相差几天
	 * 
	 * @return
	 */
	public int getBettwenDays() {
		if (getBuyFundTime() == -1) {
			return -1;
		}
		return Utils.daysBetween(System.currentTimeMillis(), getBuyFundTime());
	}

	/**
	 * 领取的时间超出限制
	 * 
	 * @param day
	 * @return
	 */
	public boolean isOverDay(int day) {
		return day > getBettwenDays() + 1;
	}

	/**
	 * 是否是领取过期的
	 * 
	 * @param day
	 * @return
	 */
	public boolean isGetRewardTimeout(int day) {
		return day <= getBettwenDays();
	}

	/**
	 * 获取领取的版本
	 * 
	 * @return 如果没有购买版本号为-1 如果购买了，版本号为（相隔购买的天数*10000+今天领取的记录）
	 */
	public int getVersion() {
		if (!this.isHaveRightGetReward()) {
			return -1;
		}
		return getBettwenDays() * 10000 + this.getGetRewardRecordByDay(getBettwenDays() + 1);

	}

	/**
	 * 保存领取成功记录
	 */
	public void recordGetReward() {
		this.getRewardRecord[getBettwenDays()] = 1;
	}

	/**
	 * 是否有权领奖
	 * 
	 * @return
	 */
	public boolean isHaveRightGetReward() {
		return getBuyFundTime() != -1 && this.getBettwenDays() < this.getRewardRecord.length;
	}

}
