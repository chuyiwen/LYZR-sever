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

import java.util.UUID;

import newbee.morningGlory.mmorpg.player.dailyQuest.ref.MGDailyQuestRef;
import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.player.quest.QuestState;

public final class MGDailyQuestProvider implements GameObjectProvider<MGDailyQuest> {

	private static final MGDailyQuestProvider instance = new MGDailyQuestProvider();

	private MGDailyQuestProvider() {

	}

	public static final MGDailyQuestProvider getInstance() {
		return instance;
	}

	@Override
	public MGDailyQuest get(Class<MGDailyQuest> type) {
		MGDailyQuest ret = new MGDailyQuest();
		return ret;
	}

	@Override
	public MGDailyQuest get(Class<MGDailyQuest> type, Object... args) {
		MGDailyQuest ret = new MGDailyQuest();

		String dailyQuestRefId = (String) args[0];
		MGDailyQuestRef dailyQuestRef = (MGDailyQuestRef) GameRoot.getGameRefObjectManager().getManagedObject(dailyQuestRefId);

		ret.setId(UUID.randomUUID().toString());
		ret.setDailyQuestRef(dailyQuestRef);
		ret.setQuestState(QuestState.VisiableQuestState);
		return ret;
	}
}
