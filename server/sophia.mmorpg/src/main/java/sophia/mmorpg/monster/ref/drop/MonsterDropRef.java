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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

public class MonsterDropRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = 8150628314559924527L;

	private Map<String, DirectDropRef> monsterDrop = new HashMap<>();
	
	// 由低到高的等级排序
	private List<LevelDropRef> levelDrop = new ArrayList<>();

	public Map<String, DirectDropRef> getMonsterDrop() {
		return monsterDrop;
	}

	public void setMonsterDrop(Map<String, DirectDropRef> monsterDrop) {
		this.monsterDrop = monsterDrop;
	}

	public List<LevelDropRef> getLevelDrop() {
		return levelDrop;
	}

	public void setLevelDrop(List<LevelDropRef> levelDrop) {
		this.levelDrop = levelDrop;
	}
}
