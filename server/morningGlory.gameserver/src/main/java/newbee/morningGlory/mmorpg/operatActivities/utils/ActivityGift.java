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
package newbee.morningGlory.mmorpg.operatActivities.utils;

import java.util.List;

import sophia.mmorpg.player.itemBag.ItemPair;

public class ActivityGift {
	public static final byte Status_Cannot_Receive = 0;
	public static final byte Status_Has_Received = 1;
	public static final byte Status_Can_Receive = 2;
	public static final byte Status_Have_Expired = 3;
	private String id;
	private byte status;
	private int condValue;
	private int worth;
	

	private String name;
	private String pic;
	private List<ItemPair> items;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public int getCondValue() {
		return condValue;
	}

	public void setCondValue(int condValue) {
		this.condValue = condValue;
	}
	public int getWorth() {
		return worth;
	}

	public void setWorth(int worth) {
		this.worth = worth;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public List<ItemPair> getItems() {
		return items;
	}

	public void setItems(List<ItemPair> items) {
		this.items = items;
	}

}
