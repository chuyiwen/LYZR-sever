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
package sophia.mmorpg.monster.gameEvent;

import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.monster.Monster;

public final class MonsterDead_GE {
	
	private FightSprite attacker;
	
	private Monster monster;

	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public FightSprite getAttacker() {
		return attacker;
	}

	public void setAttacker(FightSprite attacker) {
		this.attacker = attacker;
	}
}
