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

public final class KillMonsterQuestRefOrderItem extends AbstractQuestRefOrderItem {
	private String sceneId;
	
	private String monsterRefId;
	
	private int MonsterRefeshX;
	
	private int MonsterRefeshY;
	
	private int number;
	
	public KillMonsterQuestRefOrderItem() {
		
	}
	
	@Override
	public byte getOrderType() {
		return QuestRefOrderType.Kill_Monster_Order_Type;
	}

	public final String getSceneId() {
		return sceneId;
	}

	public final void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}

	public final String getMonsterRefId() {
		return monsterRefId;
	}

	public final void setMonsterRefId(String monsterRefId) {
		this.monsterRefId = monsterRefId;
	}

	public final int getMonsterRefeshX() {
		return MonsterRefeshX;
	}

	public final void setMonsterRefeshX(int monsterRefeshX) {
		MonsterRefeshX = monsterRefeshX;
	}

	public final int getMonsterRefeshY() {
		return MonsterRefeshY;
	}

	public final void setMonsterRefeshY(int monsterRefeshY) {
		MonsterRefeshY = monsterRefeshY;
	}

	public final int getNumber() {
		return number;
	}

	public final void setNumber(int number) {
		this.number = number;
	}
}
