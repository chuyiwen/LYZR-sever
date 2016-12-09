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
package sophia.mmorpg.player.quest.ref;

import java.util.HashSet;
import java.util.Set;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.quest.ref.condition.QuestRefCondition;
import sophia.mmorpg.player.quest.ref.npc.QuestRefNpc;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrder;
import sophia.mmorpg.player.quest.ref.reward.QuestRefReward;

public class QuestRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -6007844078024161145L;
	
	private String questType;
	
	private final Set<QuestRefCondition> conditionSet = new HashSet<>(2);
	
	private QuestRefOrder order;
	
	private QuestRefReward reward;
	
	private QuestRefNpc npc;
	
	private String nextQuestId;
	
	public QuestRef() {
		
	}
	
	public String getQuestType() {
		return questType;
	}
	
	public void setQuestType(final String questType) {
		this.questType = questType.intern();
	}

	public final QuestRefOrder getOrder() {
		return order;
	}

	public final void setOrder(QuestRefOrder order) {
		this.order = order;
	}

	public final QuestRefReward getReward() {
		return reward;
	}

	public final void setReward(QuestRefReward reward) {
		this.reward = reward;
	}

	public final QuestRefNpc getNpc() {
		return npc;
	}

	public final void setNpc(QuestRefNpc npc) {
		this.npc = npc;
	}
	
	public final void addCondition(QuestRefCondition condition) {
		this.conditionSet.add(condition);
	}
	
	public final Set<QuestRefCondition> getConditionSet(){
		return conditionSet;
	}

	public String getNextQuestId() {
		return nextQuestId;
	}

	public void setNextQuestId(String nextQuestId) {
		this.nextQuestId = nextQuestId;
	}

}
