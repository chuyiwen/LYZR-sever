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
package newbee.morningGlory.checker.refObjectChecker.skill;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import newbee.morningGlory.checker.BaseRefChecker;

public class SkillRefChecker extends BaseRefChecker<SkillRef> {

	@Override
	public String getDescription() {
		return "技能";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		SkillRef ref = (SkillRef) gameRefObject;
		String id = ref.getId();
		if(!id.startsWith("skill_")) {
			error(gameRefObject, "错误的技能id, 技能id以skill_开头! id = " + id);
		} else if (StringUtils.containsWhitespace(id)) {
			error(gameRefObject, "错误的技能id, 技能id不能包含空格。 id = \'" + id + "\'");
		}
		
		PropertyDictionary property = ref.getProperty();
		byte professionId = MGPropertyAccesser.getProfessionId(property);
		if(professionId < 0 || professionId > 4) {
			error(gameRefObject, "技能职业id错误，技能职业id应该是0到4之间的整数。当前职业id = " + professionId);
		}
	}

}
