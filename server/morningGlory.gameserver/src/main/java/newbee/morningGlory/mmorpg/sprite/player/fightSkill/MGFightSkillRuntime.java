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
package newbee.morningGlory.mmorpg.sprite.player.fightSkill;

import groovy.lang.Closure;
import sophia.foundation.util.Position;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntime;
import sophia.mmorpg.utils.RuntimeResult;

public final class MGFightSkillRuntime implements FightSkillRuntime {

	@Override
	public RuntimeResult castingSkill(FightSkill fightSkill, FightSprite caster, FightSprite targetFightSprite) {
		String refId = fightSkill.getRefId();
		Closure<RuntimeResult> closure = fightSkill.getSkillRuntime();
		if (closure == null) {
			return RuntimeResult.ParameterError(resultDetailsFormat(refId));
		} else {
			return closure.call(fightSkill, caster, targetFightSprite);
		}
	}

	@Override
	public RuntimeResult castingSkill(FightSkill fightSkill, FightSprite caster, Position targetGrid) {
		String refId = fightSkill.getRefId();
		
		Closure<RuntimeResult> closure = fightSkill.getSkillRuntime();
		if (closure == null) {
			return RuntimeResult.ParameterError(resultDetailsFormat(refId));
		} else {
			return closure.call(fightSkill, caster, targetGrid);
		}
	}

	@Override
	public RuntimeResult castingSkill(FightSkill fightSkill, FightSprite caster, byte direction) {
		String refId = fightSkill.getRefId();
		Closure<RuntimeResult> closure = fightSkill.getSkillRuntime();
		if (closure == null) {
			return RuntimeResult.ParameterError(resultDetailsFormat(refId));
		} else {
			return closure.call(fightSkill, caster, direction);
		}
	}

	private static final String part1 = "skillRefId=";
	private static final String part2 = "的技能()。在使用技能的时候，没有找到对应的调用函数。";

	private static final String resultDetailsFormat(final String refId) {
		StringBuilder sb = new StringBuilder();
		sb.append(part1).append(refId).append(part2);
		return sb.toString();
	}

}
