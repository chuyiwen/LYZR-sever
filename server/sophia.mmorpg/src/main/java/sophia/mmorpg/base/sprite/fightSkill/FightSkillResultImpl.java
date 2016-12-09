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
package sophia.mmorpg.base.sprite.fightSkill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sophia.mmorpg.base.sprite.FightSprite;

public class FightSkillResultImpl implements FightSkillResult {
	private int damage;
	private FightSprite attacker;
	private FightSprite target;
	private List<SkillEffect> effects = new ArrayList<>();
	
	public FightSkillResultImpl(int damage, FightSprite attacker, FightSprite target) {
		super();
		this.damage = damage;
		this.attacker = attacker;
		this.target = target;
	}

	public FightSkillResultImpl(int damage, List<SkillEffect> effects) {
		super();
		this.damage = damage;
		this.effects = effects;
	}

	@Override
	public Collection<SkillEffect> getSkillEffects() {
		return this.effects;
	}

	@Override
	public int getDamage() {
		return this.damage;
	}

	public void addSkillEffect(SkillEffect effect) {
		this.effects.add(effect);
	}

	@Override
	public String toString() {
		return "FightSkillResultImpl [damage=" + damage + ", effects=" + effects + "]";
	}

	@Override
	public FightSprite getAttacker() {
		return attacker;
	}

	@Override
	public FightSprite getTarget() {
		return target;
	}

	public void setAttacker(FightSprite attacker) {
		this.attacker = attacker;
	}

	public void setTarget(FightSprite target) {
		this.target = target;
	}
	
}
