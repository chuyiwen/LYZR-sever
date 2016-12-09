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
package newbee.morningGlory.mmorpg.player.dailyQuest.ref;

import java.util.List;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.quest.ref.condition.QuestRefCondition;
import sophia.mmorpg.player.quest.ref.npc.QuestRefNpc;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrder;
import sophia.mmorpg.player.quest.ref.reward.QuestRefReward;

public final class MGDailyQuestRef extends AbstractGameRefObjectBase {
//	public static final String type = "每日任务";
	
	private static final long serialVersionUID = -6428345836876366546L;

	private String questType;
	
	private QuestRefCondition conditionSet;
	
	private List<QuestRefOrder> orderList;
	
	private QuestRefReward reward;
	
	private QuestRefReward finalReward;
	
	private QuestRefReward overOrderReward;
	
	private QuestRefNpc npc;
	
	private String nextQuestId;
	
	public MGDailyQuestRef() {
	}
	
	public String getQuestType() {
		return questType;
	}
	
	public void setQuestType(final String questType) {
		this.questType = questType.intern();
	}

	public List<QuestRefOrder> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<QuestRefOrder> orderList) {
		this.orderList = orderList;
	}

	public QuestRefReward getReward() {
		return reward;
	}

	public void setReward(QuestRefReward reward) {
		this.reward = reward;
	}

	public QuestRefNpc getNpc() {
		return npc;
	}

	public void setNpc(QuestRefNpc npc) {
		this.npc = npc;
	}

	public String getNextQuestId() {
		return nextQuestId;
	}

	public void setNextQuestId(String nextQuestId) {
		this.nextQuestId = nextQuestId;
	}

	public QuestRefCondition getConditionSet() {
		return conditionSet;
	}

	public void setConditionSet(QuestRefCondition conditionSet) {
		this.conditionSet = conditionSet;
	}

	public QuestRefReward getFinalReward() {
		return finalReward;
	}

	public void setFinalReward(QuestRefReward finalReward) {
		this.finalReward = finalReward;
	}

	public QuestRefReward getOverOrderReward() {
		return overOrderReward;
	}

	public void setOverOrderReward(QuestRefReward overOrderReward) {
		this.overOrderReward = overOrderReward;
	}
}
