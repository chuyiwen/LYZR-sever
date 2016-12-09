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
package sophia.mmorpg.player.ref;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

/**
 * 玩家-职业
 */
public final class PlayerProfessionRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 3121478854925465068L;

	private int maxLevel = 0;

	private List<PlayerProfessionLevelData> levelDataList;

	private Map<String, PlayerProfessionFightSkillRef> refIdToFightSkillRefMap = new HashMap<>();

	public PlayerProfessionRef() {

	}

	public final Map<String, PlayerProfessionFightSkillRef> getRefIdToFightSkillRefMap() {
		return refIdToFightSkillRefMap;
	}

	public final void setRefIdToFightSkillRefMap(Map<String, PlayerProfessionFightSkillRef> refIdToFightSkillRefMap) {
		this.refIdToFightSkillRefMap = refIdToFightSkillRefMap;
	}

	public final PlayerProfessionFightSkillRef getPlayerClassFightSkillRef(String fightSkillRefId) {
		return refIdToFightSkillRefMap.get(fightSkillRefId);
	}

	public final List<PlayerProfessionLevelData> getLevelDataList() {
		return levelDataList;
	}

	public final void setLevelDataList(List<PlayerProfessionLevelData> levelDataList) {
		this.levelDataList = levelDataList;
		maxLevel = levelDataList.size();
	}

	public final PlayerProfessionLevelData getPlayerClassLevelData(int level) {
		// return levelDataList.get(level);
		for (PlayerProfessionLevelData levelData : levelDataList) {
			if (levelData.getLevel() == level) {
				return levelData;
			}
		}
		return null;
	}

	public final void sortPlayerLevelDataList() {
		Collections.sort(levelDataList, new Comparator<PlayerProfessionLevelData>() {
			@Override
			public int compare(PlayerProfessionLevelData p1, PlayerProfessionLevelData p2) {
				int result = p1.getLevel() - p2.getLevel();
				return result;
			}
		});
	}

	public final int maxLevel() {
		return maxLevel;
	}
}
