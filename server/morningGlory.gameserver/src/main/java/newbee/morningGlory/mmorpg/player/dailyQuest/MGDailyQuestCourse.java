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
package newbee.morningGlory.mmorpg.player.dailyQuest;

import java.util.ArrayList;
import java.util.List;

import sophia.mmorpg.player.quest.course.QuestCourseItem;

import com.google.common.base.Preconditions;

public final class MGDailyQuestCourse {
	private List<QuestCourseItem> questCourseItemList = new ArrayList<>();

	public MGDailyQuestCourse() {

	}

	public List<QuestCourseItem> getQuestCourseItemList() {
		return questCourseItemList;
	}

	public void setQuestCourseItemList(List<QuestCourseItem> questCourseItemList) {
		this.questCourseItemList = questCourseItemList;
	}

	public boolean wasCompleted() {
		if (questCourseItemList != null)
			for (QuestCourseItem questCourseItem : questCourseItemList) {
				if (!questCourseItem.wasCompleted()) {
					return false;
				}
			}
		return true;
	}

	public void addQuestCourseItem(QuestCourseItem questCourseItem) {
		Preconditions.checkNotNull(questCourseItem);
		this.questCourseItemList.add(questCourseItem);
	}
}
