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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.ref.condition.QuestRefCondition;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionItem;

public final class MGDailyQuestRefMgr {
	// 策划表的，日常数据引用的管理
	public static List<MGDailyQuestRef> dailyQuestRefList = new ArrayList<>();

	public MGDailyQuestRefMgr() {

	}

	public List<MGDailyQuestRef> getDailyQuestRefList() {
		return dailyQuestRefList;
	}

	/**
	 * 获取指定玩家新增的日常任务列表(当前等级能接的所有)
	 * 
	 * @param player
	 * @return
	 */
	public List<MGDailyQuest> getAddedDailyQuest(Player player, List<MGDailyQuest> nowQuestList) {
		List<MGDailyQuest> dailyQuestList = new ArrayList<>();
		for (MGDailyQuestRef questRef : dailyQuestRefList) {
			if (checkListContainQuest(nowQuestList, questRef)) {
				continue;
			}
			QuestRefCondition conditionSet = questRef.getConditionSet();
			Set<QuestRefConditionItem> items = conditionSet.getConditionItems(null);
			short succeed = 0;
			for (QuestRefConditionItem item : items) {
				if (player.getExpComponent().getLevel() >= item.getNumber()) {
					succeed += 1;
				}
			}
			if (succeed == items.size()) {
				String dailyQuestId = questRef.getId();
				MGDailyQuest dailyQuest = GameObjectFactory.get(MGDailyQuest.class, dailyQuestId);
				dailyQuest.setQuestState(QuestState.AcceptableQuestState);
				dailyQuestList.add(dailyQuest);
				dailyQuest.setLastRefreshTime(System.currentTimeMillis());
			}
		}
		return dailyQuestList;
	}

	private boolean checkListContainQuest(List<MGDailyQuest> nowQuestList, MGDailyQuestRef questRef) {
		for (MGDailyQuest quest : nowQuestList) {
			String id = quest.getDailyQuestRef().getId();
			String id2 = questRef.getId();
			if (StringUtils.equals(id, id2)) {
				return true;
			}
		}
		return false;
	}
}
