/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package newbee.morningGlory.mmorpg.operatActivities;

import newbee.morningGlory.mmorpg.operatActivities.impl.EveryDayRechargeGift;
import newbee.morningGlory.mmorpg.operatActivities.impl.FirstRechargeGift;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * 用于记录活动需要存储的相关运行时临时数据，这里的数据走立即保存，这里的部分记录字段在目前版本已未用到，主要是用于兼容早起版本数据
 */
public final class OperatActivityRecord {
	/** 首冲礼包 */
	private byte isFirstRecharge = FirstRechargeGift.NotRecharge;		//是否已首冲
	private long firstRechargeTime = 0l;								//首次充值时间
	/**充值礼包 */
	private long crtRechargeValue = 0;							//当前充值数
	private int crtRechargeCount = 0;
	private String hadReceiveRechargeGiftStage = "";				//已领取充值阶段	
	private long LastTotalRechargeGiftsEndTime = 0;				//上次充值礼包结束时间
	private long lastRechargeTime = 0l;							//最近充值时间
	/** 日充值礼包 */
	private byte dayRecharge = EveryDayRechargeGift.NotRecharge;
	private String  EveryDayrewardId = "";									// 每日礼包奖励是随机选组
	private long lastDayRechargeTime = 0; 						// 上次充值的时间
	private long LastDayRechargeGiftsEndTime = 0;				//上次日充值礼包活动结束时间
	
	/** 每周消费 */
	private long crtWeekConsumeValue = 0;							//本周消费累计
	private String hadReceiveWeekConsumeGiftStage = "";				//已领取消费阶段	
	private long lastWeekConsumeStartTime = 0;						//上周累计消费开始时间
	private long LastWeekTotalConsumeGiftsEndTime = 0;				//上次消费礼包活动结束时间
	
	/** 开服七日登录 */
	public static final int SevenLoginDurationDay = 7;
	private String hadReceiveSevenLoginStage = "";
	private String hadSevenLoginStage = "";							//登录过的天数，比如第一天和第三天登录过  "1,3"
	private long openServerDate = 0;
	/** 总累计消费元宝和绑定元宝*/
	private long crtTotalConsumeValue = 0;							//累计消费元宝
	private long crtTotalConsumeBindedGold = 0;						//累计消费绑定元宝数
	public OperatActivityRecord(){
	}
	
	
	public void sevenLoginInit(Player player) {
		if(player != null){
			this.setOpenServerDate(MGPropertyAccesser.getBirthday(player.getProperty()));
		}
	}
	public void init(Player player){
		sevenLoginInit(player);
	}
	
	public long getOpenServerDate() {
		return openServerDate;
	}

	public void setOpenServerDate(long openServerDate) {
		this.openServerDate = openServerDate;
	}

	public long getCrtWeekConsumeValue() {
		return crtWeekConsumeValue;
	}

	public void setCrtWeekConsumeValue(long crtWeekConsumeValue) {
		this.crtWeekConsumeValue = crtWeekConsumeValue;
	}

	public String getHadReceiveWeekConsumeGiftStage() {
		return hadReceiveWeekConsumeGiftStage;
	}

	public void setHadReceiveWeekConsumeGiftStage(String hadReceiveWeekConsumeGiftStage) {
		this.hadReceiveWeekConsumeGiftStage = hadReceiveWeekConsumeGiftStage;
	}

	public long getLastWeekTotalConsumeGiftsEndTime() {
		return LastWeekTotalConsumeGiftsEndTime;
	}

	public void setLastWeekTotalConsumeGiftsEndTime(long lastWeekTotalConsumeGiftsEndTime) {
		LastWeekTotalConsumeGiftsEndTime = lastWeekTotalConsumeGiftsEndTime;
	}

	public byte getDayRecharge() {
		return dayRecharge;
	}

	public void setDayRecharge(byte dayRecharge) {
		this.dayRecharge = dayRecharge;
	}

	public long getLastDayRechargeGiftsEndTime() {
		return LastDayRechargeGiftsEndTime;
	}

	public void setLastDayRechargeGiftsEndTime(long lastDayRechargeGiftsEndTime) {
		LastDayRechargeGiftsEndTime = lastDayRechargeGiftsEndTime;
	}

	public byte getIsFirstRecharge() {
		return isFirstRecharge;
	}

	public void setIsFirstRecharge(byte isFirstRecharge) {
		this.isFirstRecharge = isFirstRecharge;
	}

	public long getCrtRechargeValue() {
		return crtRechargeValue;
	}

	public void setCrtRechargeValue(long crtRechargeValue) {
		this.crtRechargeValue = crtRechargeValue;
	}

	public String getHadReceiveRechargeGiftStage() {
		return hadReceiveRechargeGiftStage;
	}

	public void setHadReceiveRechargeGiftStage(String hadReceiveRechargeGiftStage) {
		this.hadReceiveRechargeGiftStage = hadReceiveRechargeGiftStage;
	}

	public long getLastTotalRechargeGiftsEndTime() {
		return LastTotalRechargeGiftsEndTime;
	}

	public void setLastTotalRechargeGiftsEndTime(long lastTotalRechargeGiftsEndTime) {
		LastTotalRechargeGiftsEndTime = lastTotalRechargeGiftsEndTime;
	}

	public long getLastDayRechargeTime() {
		return lastDayRechargeTime;
	}

	public void setLastDayRechargeTime(long lastDayRechargeTime) {
		this.lastDayRechargeTime = lastDayRechargeTime;
	}

	public long getLastWeekConsumeStartTime() {
		return lastWeekConsumeStartTime;
	}

	public void setLastWeekConsumeStartTime(long lastWeekConsumeStartTime) {
		this.lastWeekConsumeStartTime = lastWeekConsumeStartTime;
	}


	public String getHadReceiveSevenLoginStage() {
		return hadReceiveSevenLoginStage;
	}


	public void setHadReceiveSevenLoginStage(String hadReceiveSevenLoginStage) {
		this.hadReceiveSevenLoginStage = hadReceiveSevenLoginStage;
	}


	public String getHadSevenLoginStage() {
		return hadSevenLoginStage;
	}


	public void setHadSevenLoginStage(String hadSevenLoginStage) {
		this.hadSevenLoginStage = hadSevenLoginStage;
	}

	public String getEveryDayrewardId() {
		return EveryDayrewardId;
	}


	public void setEveryDayrewardId(String everyDayrewardId) {
		EveryDayrewardId = everyDayrewardId;
	}


	public long getFirstRechargeTime() {
		return firstRechargeTime;
	}


	public void setFirstRechargeTime(long firstRechargeTime) {
		this.firstRechargeTime = firstRechargeTime;
	}


	public int getCrtRechargeCount() {
		return crtRechargeCount;
	}


	public void setCrtRechargeCount(int crtRechargeCount) {
		this.crtRechargeCount = crtRechargeCount;
	}


	public long getLastRechargeTime() {
		return lastRechargeTime;
	}


	public void setLastRechargeTime(long lastRechargeTime) {
		this.lastRechargeTime = lastRechargeTime;
	}


	public long getCrtTotalConsumeValue() {
		return crtTotalConsumeValue;
	}


	public void setCrtTotalConsumeValue(long crtTotalConsumeValue) {
		this.crtTotalConsumeValue = crtTotalConsumeValue;
	}


	public long getCrtTotalConsumeBindedGold() {
		return crtTotalConsumeBindedGold;
	}


	public void setCrtTotalConsumeBindedGold(long crtTotalConsumeBindedGold) {
		this.crtTotalConsumeBindedGold = crtTotalConsumeBindedGold;
	}
	/**
	 * 修改累计消费的元宝数
	 * @param unbindedGold
	 */
	public void modifyCrtTotalConsumeValue(int unbindedGold){
		if(unbindedGold > 0){
			this.crtTotalConsumeValue += unbindedGold;
		}
	}
	/**
	 * 修改累计消费的绑定元宝数
	 * @param bindedGold
	 */
	public void modifyCrtTotalConsumeBindedGold(int bindedGold){
		if(bindedGold > 0){
			this.crtTotalConsumeBindedGold += bindedGold;
		}
	}
	
}
