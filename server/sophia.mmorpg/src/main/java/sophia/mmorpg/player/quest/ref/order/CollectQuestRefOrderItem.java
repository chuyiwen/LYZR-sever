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

public final class CollectQuestRefOrderItem extends AbstractQuestRefOrderItem {
	private String sceneRefId;
	
	private String collectTargetObjectId;
	
	private String collectItemId;
	
	private short number;
	
	public CollectQuestRefOrderItem() {
		
	}
	
	@Override
	public byte getOrderType() {
		return QuestRefOrderType.Collect_Order_Type;
	}

	public final String getSceneRefId() {
		return sceneRefId;
	}

	public final void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}

	public final String getCollectTargetObjectId() {
		return collectTargetObjectId;
	}

	public final void setCollectTargetObjectId(String collectTargetObjectId) {
		this.collectTargetObjectId = collectTargetObjectId;
	}

	public final String getCollectItemId() {
		return collectItemId;
	}

	public final void setCollectItemId(String collectItemId) {
		this.collectItemId = collectItemId;
	}

	public final short getNumber() {
		return number;
	}

	public final void setNumber(short number) {
		this.number = number;
	}
}
