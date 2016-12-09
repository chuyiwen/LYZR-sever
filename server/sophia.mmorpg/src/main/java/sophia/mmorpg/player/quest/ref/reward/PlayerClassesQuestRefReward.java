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
package sophia.mmorpg.player.quest.ref.reward;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class PlayerClassesQuestRefReward implements QuestRefReward {

	private Map<String, Map<Byte, Set<QuestRefRewardItem>>> playerClassIdToRewardItemsMap = new HashMap<>();

	public PlayerClassesQuestRefReward() {
	}

	@Override
	public Set<QuestRefRewardItem> getRewardItems(Player player) {
		if (player == null) {
			return null;
		}
		byte professionId = MGPropertyAccesser.getProfessionId(player.getProperty());
		String profession = Byte.toString(professionId);
		Map<Byte, Set<QuestRefRewardItem>> professionMap = playerClassIdToRewardItemsMap.get(profession);
		return professionMap.get(MGPropertyAccesser.getGender(player.getProperty()));
	}

	public void addProfessionQuestRefRewardItemSet(String playerClassId, byte gender, Set<QuestRefRewardItem> rewardItemSet) {
		if (playerClassIdToRewardItemsMap.containsKey(playerClassId)) {
			Map<Byte, Set<QuestRefRewardItem>> genderMap = playerClassIdToRewardItemsMap.get(playerClassId);
			genderMap.put(gender, rewardItemSet);
			playerClassIdToRewardItemsMap.put(playerClassId, genderMap);
		} else {
			Map<Byte, Set<QuestRefRewardItem>> genderMap = new HashMap<>();
			genderMap.put(gender, rewardItemSet);
			playerClassIdToRewardItemsMap.put(playerClassId, genderMap);
		}
	}

	public Map<String, Map<Byte, Set<QuestRefRewardItem>>> getProfessionQuestRefRewardItemSet() {
		return playerClassIdToRewardItemsMap;
	}

	public Set<QuestRefRewardItem> checkRewardItems(String a, byte b) {
		return playerClassIdToRewardItemsMap.get(a).get(b);
	}

	@Override
	public int getRelatedType() {
		return 1;
	}
}
