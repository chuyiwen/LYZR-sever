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
package newbee.morningGlory.mmorpg.gameInstance.quest;

import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrder;
import sophia.mmorpg.player.quest.ref.reward.QuestRefReward;

/**
 * 副本Ref-任务Ref
 * 		！副本开启的时候创建，离开后领取，如果背包状态：满，以邮件方式领取;
 */
public final class MGGameInstanceQuestRef {
	
	private String Id;
	
	private GameInstanceRef gameInstanceRef;
	
	private String questType;
	
	private QuestRefOrder order;
	
	private QuestRefReward reward;
	
	private int rewardType;
	
	public MGGameInstanceQuestRef() {
	}
	
	public MGGameInstanceQuestRef(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}

	public GameInstanceRef getGameInstanceRef() {
		return gameInstanceRef;
	}

	public void setGameInstanceRef(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}
	
	public String getQuestType() {
		return questType;
	}
	
	public void setQuestType(final String questType) {
		this.questType = questType.intern();
	}

	public QuestRefOrder getOrder() {
		return order;
	}

	public void setOrder(QuestRefOrder order) {
		this.order = order;
	}

	public QuestRefReward getReward() {
		return reward;
	}

	public void setReward(QuestRefReward reward) {
		this.reward = reward;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public int getRewardType() {
		return rewardType;
	}

	public void setRewardType(int rewardType) {
		this.rewardType = rewardType;
	}
}
