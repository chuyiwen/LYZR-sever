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
package newbee.morningGlory.mmorpg.player.achievement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import sophia.mmorpg.player.Player;

public final class MGPlayerAchievementMgr {
	private Player player;
	private Set<String> AllAchievementIdSet = new HashSet<String>();

	private List<MGPlayerAchievement> crtAchievementList = new ArrayList<>();

	public MGPlayerAchievementMgr() {
	}

	/**
	 * 取得某一成就类别的最高成就
	 * 
	 * @param category
	 *            成就类别
	 */
	public synchronized MGPlayerAchievement getMaxAchieveOfCategory(byte category) {
		MGPlayerAchievement achieve = null;
		List<MGPlayerAchievement> achieves = getAchieveByCategory(category);
		for (MGPlayerAchievement temp : achieves) {
			MGPlayerAchievementRef achieveRef = temp.getAchievementRef().getNextAchieveRef();
			if (achieveRef == null || !AllAchievementIdSet.contains(achieveRef.getId())) {
				achieve = temp;
				break;
			}
		}
		return achieve;
	}

	public synchronized MGPlayerAchievement getAchieveByRefId(String refId) {
		MGPlayerAchievement achieve = null;
		for (MGPlayerAchievement temp : crtAchievementList) {
			if (StringUtils.equals(temp.getAchievementRef().getId(), refId)) {
				achieve = temp;
				break;
			}
		}
		return achieve;
	}

	/**
	 * 获取某一完成条件类别的成就
	 * 
	 * @return
	 */

	public synchronized List<MGPlayerAchievement> getAchieveByCategory(byte completeCondition) {
		List<MGPlayerAchievement> achieves = new ArrayList<MGPlayerAchievement>();
		for(MGPlayerAchievement achieve : crtAchievementList) {
			if(achieve.getCompleteCondition() == completeCondition) {
				achieves.add(achieve);
			}
		}
		return achieves;
	}

	/**
	 * 获取所有完成但未领取奖励的成就List
	 * 
	 * @return
	 */
	public synchronized List<MGPlayerAchievement> getAchieveNotGetReward() {
		List<MGPlayerAchievement> achieves = new ArrayList<MGPlayerAchievement>();
		for (int i = 0; i < crtAchievementList.size(); i++) {
			MGPlayerAchievement achieve = crtAchievementList.get(i);
			if (achieve.getSuccess() == MGPlayerAchievement.NOTGET) {
				achieves.add(achieve);
			}
		}
		return achieves;
	}

	public synchronized Set<String> getAllAchievementIdSet() {
		return AllAchievementIdSet;
	}

	public synchronized void setAllAchievementIdSet(Set<String> allAchievementIdSet) {
		AllAchievementIdSet = allAchievementIdSet;
	}
	
	public synchronized void addAllAchievementIdSet(String id) {
		if (this.AllAchievementIdSet.contains(id)) {
			return;
		}
		
		this.AllAchievementIdSet.add(id);
	}

	public synchronized List<MGPlayerAchievement> getCrtAchievementList() {
		return crtAchievementList;
	}

	public synchronized void setCrtAchievementList(List<MGPlayerAchievement> crtAchievementList) {
		this.crtAchievementList = crtAchievementList;
	}
	
	public synchronized boolean addCrtAchievementList(MGPlayerAchievement achievement){
		if (this.crtAchievementList.contains(achievement)) {
			return false;
		}
		
		return this.crtAchievementList.add(achievement);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
