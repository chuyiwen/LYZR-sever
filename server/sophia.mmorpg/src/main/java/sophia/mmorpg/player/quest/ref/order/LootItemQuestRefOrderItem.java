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

public final class LootItemQuestRefOrderItem extends AbstractQuestRefOrderItem {
	private String sceneRefId;
	
	private String monsterRefId;
	
	private String itemRefId;
	
	private int number;
	
	public LootItemQuestRefOrderItem() {
		
	}

	@Override
	public byte getOrderType() {
		return QuestRefOrderType.Loot_Item_Order_Type;
	}

	public final String getSceneRefId() {
		return sceneRefId;
	}

	public final void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}

	public final String getMonsterRefId() {
		return monsterRefId;
	}

	public final void setMonsterRefId(String monsterRefId) {
		this.monsterRefId = monsterRefId;
	}

	public final String getItemRefId() {
		return itemRefId;
	}

	public final void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public final int getNumber() {
		return number;
	}

	public final void setNumber(int number) {
		this.number = number;
	}
}
