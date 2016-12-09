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
package sophia.mmorpg.player.quest.course;

import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;

public interface QuestCourseItem {
	/**
	 * 获取任务进度条目的索引值，即，该条目在任务的排序位置
	 * @return
	 */
	int getIndex();
	
	QuestRefOrderItem getQuestRefOrderItem();
	
	/**
	 * 该任务条目是否完成
	 * @return
	 */
	boolean wasCompleted();
}
