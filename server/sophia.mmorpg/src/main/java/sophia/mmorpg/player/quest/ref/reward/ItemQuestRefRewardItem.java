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
package sophia.mmorpg.player.quest.ref.reward;


public final class ItemQuestRefRewardItem implements QuestRefRewardItem {
	private String itemRefId;
	
	private boolean binded;
	
	private int number;
	
	private int relateType;
	
	public ItemQuestRefRewardItem() {
		
	}
	
	@Override
	public byte getRewardType() {
		return QuestRefRewardType.Item_Reward_Type;
	}

	public final String getItemRefId() {
		return itemRefId;
	}

	public final void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public final boolean isBinded() {
		return binded;
	}

	public final void setBinded(boolean binded) {
		this.binded = binded;
	}

	public final int getNumber() {
		return number;
	}

	public final void setNumber(int number) {
		this.number = number;
	}

	public int getRelateType() {
		return relateType;
	}

	public void setRelateType(int relateType) {
		this.relateType = relateType;
	}
}
