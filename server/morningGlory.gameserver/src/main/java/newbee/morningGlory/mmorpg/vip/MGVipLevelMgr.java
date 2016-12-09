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
package newbee.morningGlory.mmorpg.vip;

import java.util.Calendar;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGVipLevelMgr {
	private byte vipType = MGVipType.NO_VIP;
	private MGVipLevelDataRef vipLevelDataRef;
	private MGVipRewardRecord vipRewardRecord = new MGVipRewardRecord();
	private long vipStartTime = 0; // VIP开始生效时间
	private long stackRemainTime = 0; // 堆叠的剩余时间
	public long getStackRemainTime() {
		return stackRemainTime;
	}

	public void setStackRemainTime(long stackRemainTime) {
		this.stackRemainTime = stackRemainTime;
	}

	private long vipEndTime = 0; // VIP过期时间

	public MGVipLevelMgr() {

	}

	/**
	 * 获取当前VIP剩余天数
	 * 
	 * @return
	 */
	public int getRemainDays() {
		if (getVipType() == MGVipType.NO_VIP) {
			return 0;
		}
		int remainDays = (int) (getRemainTime() / 1000 / 3600 / 24);
		return remainDays;
	}

	/**
	 * 获取剩余的时间
	 * 
	 * @return
	 */
	public long getRemainTime() {
		long crtTime = System.currentTimeMillis();
		long totalRemainTime = this.vipEndTime - crtTime;
		return totalRemainTime;
	}

	/**
	 * 设置VIP类型
	 * 
	 * @param vipType
	 */
	public void setVipType(String vipRefId, byte vipType) {
		MGVipLevelDataRef vipRef = (MGVipLevelDataRef) GameRoot.getGameRefObjectManager().getManagedObject(vipRefId);
		if (vipRef == null) {
			return;
		}	
		setVipLevelDataRef(vipRef);
		PropertyDictionary pd = this.getVipLevelDataRef().getProperty();
		int duration = MGPropertyAccesser.getDuration(pd);
		long day =  1000 * 60 * 60 * 24l;
		if (this.getVipType() != MGVipType.NO_VIP) {
			this.stackRemainTime =  duration * day; 		
			this.vipEndTime = this.vipEndTime + this.stackRemainTime;
		}else{
			this.stackRemainTime =  duration * day;
			this.vipStartTime = System.currentTimeMillis();
			this.vipEndTime = this.vipStartTime + this.stackRemainTime;
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(this.vipEndTime + day - 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			this.vipEndTime = cal.getTime().getTime();
		}

		this.vipType = vipType;	
	}

	/**
	 * 过期重置
	 */
	public void resetVip() {
		this.stackRemainTime = 0;
		this.vipEndTime = 0;
		this.vipStartTime = 0;
		this.vipType = MGVipType.NO_VIP;
		this.vipLevelDataRef = null;
	}

	public boolean isVip(){
		return this.getVipType() != MGVipType.NO_VIP;
	}
	
	public byte getVipType() {
		return this.vipType;
	}

	public MGVipLevelDataRef getVipLevelDataRef() {
		return vipLevelDataRef;
	}

	public void setVipLevelDataRef(MGVipLevelDataRef vipLevelDataRef) {
		this.vipLevelDataRef = vipLevelDataRef;
	}

	public MGVipRewardRecord getVipRewardRecord() {
		return vipRewardRecord;
	}

	public void setVipRewardRecord(MGVipRewardRecord vipRewardRecord) {
		this.vipRewardRecord = vipRewardRecord;
	}

	public long getVipStartTime() {
		return vipStartTime;
	}

	public void setVipStartTime(long vipStartTime) {
		this.vipStartTime = vipStartTime;
	}

	public long getVipEndTime() {
		return vipEndTime;
	}

	public void setVipEndTime(long vipEndTime) {
		this.vipEndTime = vipEndTime;
	}
	public void setVip(byte vipType) {
		this.vipType = vipType;
	}
}
