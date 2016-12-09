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
package sophia.mmorpg.player.quest;

import org.apache.log4j.Logger;

import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.player.quest.ref.QuestRef;

public final class PlayerQuestManager {
	private static final Logger logger = Logger.getLogger(PlayerQuestManager.class);
	
	private Quest crtQuest;
	
	public PlayerQuestManager() {
	}

	public final Quest getCrtQuest() {
		return crtQuest;
	}
	
	public final void setCrtQuest(Quest crtQuest) {
		this.crtQuest = crtQuest;
	}
	
	public final Quest preQuest() {
		return null;
	}

	public final Quest nextQuest() {
		QuestRef questRef = crtQuest.getQuestRef();
		String nextQuestId = questRef.getNextQuestId();
		Quest quest = GameObjectFactory.getQuest(nextQuestId);
		return quest;
	}
	
}
