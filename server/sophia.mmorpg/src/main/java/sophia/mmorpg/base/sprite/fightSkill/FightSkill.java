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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.log4j.Logger;

import groovy.lang.Closure;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.utils.SFRandomUtils;

public final class FightSkill {
	
	private final static Logger logger = Logger.getLogger(FightSkill.class);
	
	private String id;
	private int level;
	private int exp;
	private SkillRef ref;

	private Closure<RuntimeResult> runtime;
	private PropertyDictionary runtimeParameter;

	public FightSkill(String id, SkillRef ref) {
		this.id = id;
		this.level = 1;
		this.exp = 0;
		checkArgument(ref != null);
		this.ref = ref;
		this.runtime = getDefaultRuntime();
		this.runtimeParameter = (this.getLevelRef() != null) ? this.getLevelRef().getRuntimeParameter() : null;
	}

	public FightSkill(String id, int level, int exp, SkillRef ref) {
		this.id = id;
		this.level = level;
		this.exp = exp;
		checkArgument(ref != null);
		this.ref = ref;
		this.runtime = getDefaultRuntime();
		this.runtimeParameter = (this.getLevelRef() != null) ? this.getLevelRef().getRuntimeParameter() : null;
	}

	public Closure<RuntimeResult> getSkillRuntime() {
		return this.runtime;
	}

	public void changeSkillRuntime(Closure<RuntimeResult> skillRuntime) {
		this.runtime = skillRuntime;
	}

	public PropertyDictionary getRuntimeParameter() {
		return this.runtimeParameter;
	}

	public void changeRuntimeParameter(PropertyDictionary runtimeParameter) {
		this.runtimeParameter = runtimeParameter;
	}

	public Closure<RuntimeResult> getDefaultRuntime() {
		SkillLevelRef skillLevelRef = getLevelRef();
		if (skillLevelRef != null && skillLevelRef.hasLevelSkillRuntime()) {
			return skillLevelRef.getLevelSkillRuntime();
		} else {
			return this.ref.getRuntime();
		}
	}

	public String getId() {
		return this.id;
	}

	public String getRefId() {
		return ref.getId();
	}

	public SkillRef getRef() {
		return ref;
	}

	public SkillLevelRef getLevelRef() {
		checkNotNull(this.ref);
		checkNotNull(this.level);
		return this.ref.get(this.level);
	}

	public synchronized void addExpAndCheckLevelUp(int playerLevel, int exp) {
		addExp(exp);

		checkLevelUp(playerLevel);
		
		if(ref==null){
			logger.error("FightSkill error id ="+this.id);
			return;
		}
		
		SkillLevelRef levelRef = ref.get(getLevel());
		if (levelRef == null) {
			return;
		}

		int expRequired = MGPropertyAccesser.getSkillUpperExp(levelRef.getProperty());
		if (getExp() > expRequired) {
			this.exp = expRequired;
		}
	}

	public synchronized boolean checkLevelUp(int playerLevel) {
		boolean canLevelUp = canLevelUp(playerLevel);
		if (!canLevelUp) {
			return false;
		}

		levelUp();

		return true;
	}

	public boolean canLevelUp(int playerLevel) {
		int maxLevel = ref.getSkillLevelData().size();

		SkillLevelRef levelRef = ref.get(level);
		if (levelRef == null)
			return false;
		int expRequired = MGPropertyAccesser.getSkillUpperExp(levelRef.getProperty());

		int nextLevel = Math.min(level + 1, maxLevel);
		SkillLevelRef nextLevelRef = ref.get(nextLevel);
		int levelRequired = MGPropertyAccesser.getSkillLearnLevel(nextLevelRef.getProperty());

		boolean can = exp >= expRequired && playerLevel >= levelRequired && level < maxLevel;
		if (!can && this.exp > expRequired) {
			this.exp = expRequired;
		}

		return can;
	}

	public synchronized void justCheckMaxLevelUp() {
		int maxLevel = ref.getSkillLevelData().size();
		if (this.level >= maxLevel) {
			return;
		}
		levelUp();
	}

	private synchronized void levelUp() {
		this.level++;
		this.exp = 0;
	}

	public synchronized void addExp(int exp) {
		this.exp += exp;
	}

	public synchronized void use() {
		// use a skill can add exp 1 to 3 randomly
		// TODO: consider to make 3 configurable
		int maxExpExcluded = 3;
		this.exp += SFRandomUtils.random(maxExpExcluded);
	}

	public synchronized int getLevel() {
		return level;
	}

	public synchronized void setLevel(int level) {
		this.level = level;
	}

	public synchronized int getExp() {
		return exp;
	}

	public synchronized void setExp(int exp) {
		this.exp = exp;
	}

	public boolean isBaseSkill() {
		return getRef().isBaseSkill();
	}

	public int getMaxExpRequired() {
		SkillLevelRef levelRef = ref.get(level);
		if (levelRef == null)
			return 0;
		int expRequired = MGPropertyAccesser.getSkillUpperExp(levelRef.getProperty());
		return expRequired;
	}

	public String getName() {
		return MGPropertyAccesser.getName(ref.getProperty());
	}

	@Override
	public String toString() {
		return "FightSkill [id=" + getId() + ", level=" + level + ", exp=" + exp + ", ref=" + ref + "]";
	}
}
