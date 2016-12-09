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
package sophia.mmorpg.player.quest.ref.order;

public final class ChineseModeStringQuestRefOrderItem extends AbstractQuestRefOrderItem {
	private String chineseModeValue;
	private String chineseModeTarget;
	private long count;
	private int number;
	
	private short orderEventId = 0;
	
	public ChineseModeStringQuestRefOrderItem() {
	}
	
	@Override
	public byte getOrderType() {
		return QuestRefOrderType.ChineseMode_String_Value_Order_Type;
	}

	public final String getChineseModeValue() {
		return chineseModeValue;
	}

	public final void setChineseModeValue(String chineseModeValue) {
		this.chineseModeValue = chineseModeValue;
	}

	public final long getCount() {
		return count;
	}

	public final void setCount(long count) {
		this.count = count;
	}

	public final int getNumber() {
		return number;
	}

	public final void setNumber(int number) {
		this.number = number;
	}

	public short getOrderEventId() {
		return orderEventId;
	}

	public void setOrderEventId(short orderEventId) {
		this.orderEventId = orderEventId;
	}

	public String getChineseModeTarget() {
		return chineseModeTarget;
	}

	public void setChineseModeTarget(String chineseModeTarget) {
		this.chineseModeTarget = chineseModeTarget;
	}
}
