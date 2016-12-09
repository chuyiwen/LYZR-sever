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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public class SkillRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -4533625426082693498L;

	private Map<Integer, SkillLevelRef> skillLevelData = new HashMap<Integer, SkillLevelRef>();

	// Here's no runtimeParameter since its parameter lies in SkillLevelRef by
	// default. We will get its runtimeParameter by skill level.
	private Closure<RuntimeResult> runtime;

	public static final String basicAttackRefId = "skill_0";

	public SkillRef() {

	}

	public Closure<RuntimeResult> getRuntime() {
		return runtime;
	}

	public void setRuntime(Closure<RuntimeResult> skillRuntime) {
		this.runtime = skillRuntime;
	}

	public Map<Integer, SkillLevelRef> getSkillLevelData() {
		return skillLevelData;
	}

	public void setSkillLevelData(Map<Integer, SkillLevelRef> skillLevelData) {
		this.skillLevelData = skillLevelData;
	}

	public void add(SkillLevelRef skillLevelRef) {
		this.skillLevelData.put(skillLevelRef.getLevel(), skillLevelRef);
	}

	public SkillLevelRef get(int level) {
		return skillLevelData.get(level);
	}

	public boolean isAttributeSkill() {
		byte skillType = MGPropertyAccesser.getSkillType(property);
		return skillType == 0;
	}

	public boolean isTargetFriend() {
		byte skillTargetType = MGPropertyAccesser.getSkillTargetType(property);
		return skillTargetType != 2;
	}

	public boolean isTargetSkill() {
		byte skillAimType = MGPropertyAccesser.getSkillAimType(property);
		return skillAimType == 1;
	}

	public boolean isDirectionSkill() {
		byte skillAimType = MGPropertyAccesser.getSkillAimType(property);
		return skillAimType == 2;
	}

	public boolean isGridSkill() {
		byte skillAimType = MGPropertyAccesser.getSkillAimType(property);
		return skillAimType == 3;
	}

	public boolean isBasicSkill() {
		return StringUtils.equals(basicAttackRefId, id);
	}

	public boolean isBaseSkill() {
		int skillGroupId = MGPropertyAccesser.getSkillGroupId(property);
		return skillGroupId == 0;
	}

	public boolean isExtendedSkill() {
		int skillGroupId = MGPropertyAccesser.getSkillGroupId(property);
		return skillGroupId == 1;
	}

	@Override
	public String toString() {
		return "SkillRef [id=" + id + "]";
	}
}
