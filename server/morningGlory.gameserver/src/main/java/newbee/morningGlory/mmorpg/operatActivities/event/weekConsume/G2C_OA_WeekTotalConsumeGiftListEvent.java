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
package newbee.morningGlory.mmorpg.operatActivities.event.weekConsume;

import java.util.List;

import newbee.morningGlory.mmorpg.operatActivities.utils.ActivityGift;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class G2C_OA_WeekTotalConsumeGiftListEvent extends ActionEventBase {

	private long laveTime;
	private long beginTime;
	private long endTime;
	private String weekStartEndTime;
	private int crtTotalRechargeValue;
	private List<ActivityGift> activityGifts;
	
	public G2C_OA_WeekTotalConsumeGiftListEvent(){
		
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putLong(laveTime);
		buffer.putLong(beginTime);
		buffer.putLong(endTime);
		putString(buffer, weekStartEndTime);
		buffer.putInt(crtTotalRechargeValue);
		//buffer.putShort((short) activityGifts.size());
		buffer.put((byte) activityGifts.size());
		for (ActivityGift gift : activityGifts) {
			putString(buffer, gift.getId());
			buffer.putInt(gift.getCondValue());
			buffer.putInt(gift.getWorth());
			buffer.put(gift.getStatus());
			List<ItemPair> items = gift.getItems();
			//buffer.putShort((short) items.size());
			buffer.put((byte) items.size());
			for (ItemPair pair : items) {
				putString(buffer, pair.getItemRefId());
				buffer.putInt(pair.getNumber());
				buffer.put((byte) (pair.isBindStatus() ? 1 : 0));
			}
		}
		return buffer;
	}

	
	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public long getLaveTime() {
		return laveTime;
	}


	public void setLaveTime(long laveTime) {
		this.laveTime = laveTime;
	}


	public long getBeginTime() {
		return beginTime;
	}


	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}


	public long getEndTime() {
		return endTime;
	}


	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}


	public int getCrtTotalRechargeValue() {
		return crtTotalRechargeValue;
	}


	public void setCrtTotalRechargeValue(int crtTotalRechargeValue) {
		this.crtTotalRechargeValue = crtTotalRechargeValue;
	}


	public List<ActivityGift> getActivityGifts() {
		return activityGifts;
	}


	public void setActivityGifts(List<ActivityGift> activityGifts) {
		this.activityGifts = activityGifts;
	}


	public String getWeekStartEndTime() {
		return weekStartEndTime;
	}


	public void setWeekStartEndTime(String weekStartEndTime) {
		this.weekStartEndTime = weekStartEndTime;
	}


	
}
