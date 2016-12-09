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
package newbee.morningGlory.mmorpg.sortboard;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import sophia.mmorpg.player.Player;

public final class SortboardHelper {
	
	public static int getRanking(SortboardType sortboardType, Player player) {
		SortboardData sortboardData = SortboardMgr.getInstance().getSortboardData(sortboardType);
		List<SortboardScoreData> list = sortboardData.getScoreData();
		String pre = player.getId();
		for (int i = 0; i < list.size(); i++) {
			String playerId = list.get(i).getPlayerId();
			if (playerId.startsWith(pre)) {
				return i + 1;
			}
		}
		
		return 0;
	}
	
	public static int getScore(SortboardType sortboardType, Player player) {
		SortboardData sortboardData = SortboardMgr.getInstance().getSortboardData(sortboardType);
		List<SortboardScoreData> list = sortboardData.getScoreData();
		String pre = player.getId();
		for (int i = 0; i < list.size(); i++) {
			String playerId = list.get(i).getPlayerId();
			if (playerId.startsWith(pre)) {
				return list.get(i).getScore();
			}
		}

		return 0;
	}
	
	public static List<String> getTopPlayerIdList() {
		List<String> topPlayerList = new ArrayList<>();
		EnumSet<SortboardType> currEnumSet = EnumSet.allOf(SortboardType.class);
		for (SortboardType type : currEnumSet) {
			SortboardData data = SortboardMgr.getInstance().getSortboardData(type);
			if (data.getScoreData().size() > 0) {
				String name = data.getScoreData().get(0).getName();
				topPlayerList.add(name);
			}
		}
		
		return topPlayerList;
	}
}
