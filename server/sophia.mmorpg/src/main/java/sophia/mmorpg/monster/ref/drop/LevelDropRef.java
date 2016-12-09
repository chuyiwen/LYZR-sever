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
package sophia.mmorpg.monster.ref.drop;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应 level_drop
 */
public class LevelDropRef {
	
	private String levelRange;
	
	private int minLevel;
	
	private int maxLevel;
	
	private List<DropEntryRef> levelDropRefList = new ArrayList<>();

	public List<DropEntryRef> getLevelDropRefList() {
		return levelDropRefList;
	}

	public void setLevelDropRefList(List<DropEntryRef> levelDropRefList) {
		this.levelDropRefList = levelDropRefList;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	@Override
	public String toString() {
		return "[" + minLevel + "," + maxLevel + "]";
	}

	public String getLevelRange() {
		return levelRange;
	}

	public void setLevelRange(String levelRange) {
		this.levelRange = levelRange;
	}
}
