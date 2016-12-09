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
package sophia.mmorpg.player.fightSkill.ref;

import groovy.lang.Closure;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.utils.RuntimeResult;

public class SkillLevelRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -2928214681416005655L;

	private int level;

	private Closure<RuntimeResult> runtime;
	private PropertyDictionary runtimeParameter;

	public SkillLevelRef() {
		super();
	}

	public SkillLevelRef(String id, PropertyDictionary property, int level, PropertyDictionary effect) {
		super(id, property);
		this.level = level;
		this.runtimeParameter = effect;
	}

	public PropertyDictionary getRuntimeParameter() {
		return runtimeParameter;
	}

	public void setRuntimeParameter(PropertyDictionary param) {
		this.runtimeParameter = param;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean hasLevelSkillRuntime() {
		return runtime != null;
	}

	public Closure<RuntimeResult> getLevelSkillRuntime() {
		return runtime;
	}

	public void setLevelSkillRuntime(Closure<RuntimeResult> levelSkillRuntime) {
		this.runtime = levelSkillRuntime;
	}
}
