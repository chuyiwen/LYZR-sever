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
package newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin;

import java.util.List;

import newbee.morningGlory.mmorpg.operatActivities.utils.ActivityGift;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class G2C_OA_SevenLogin_ReceiveState extends ActionEventBase {

	private String duration;
	private int whichDay;
	private List<ActivityGift> activityGifts;
	
	public G2C_OA_SevenLogin_ReceiveState(){
		ziped = (byte)1;
	}
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {

		putString(buffer, duration);
		buffer.put((byte)whichDay);
		buffer.put((byte) activityGifts.size());
		for (ActivityGift gift : activityGifts) {
			putString(buffer, gift.getId());
			//buffer.putInt(gift.getCondValue());
			buffer.put(gift.getStatus());
			//putString(buffer, gift.getPic());
			List<ItemPair> items = gift.getItems();
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

	public int getWhichDay() {
		return whichDay;
	}

	public void setWhichDay(int whichDay) {
		this.whichDay = whichDay;
	}

	public List<ActivityGift> getActivityGifts() {
		return activityGifts;
	}

	public void setActivityGifts(List<ActivityGift> activityGifts) {
		this.activityGifts = activityGifts;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}
