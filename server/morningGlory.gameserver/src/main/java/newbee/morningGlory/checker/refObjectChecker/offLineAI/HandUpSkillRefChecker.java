package newbee.morningGlory.checker.refObjectChecker.offLineAI;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.HandUpSkillRef;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class HandUpSkillRefChecker extends BaseRefChecker<HandUpSkillRef> {

	@Override
	public String getDescription() {
		return "HandUpSkillRefChecker";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		HandUpSkillRef ref = (HandUpSkillRef) gameRefObject;
		String id = ref.getId();
		if(!id.startsWith("skill_")) {
			error(gameRefObject, "错误的技能id, 技能id以skill_开头! id = " + id);
		} else if (StringUtils.containsWhitespace(id)) {
			error(gameRefObject, "错误的技能id, 技能id不能包含空格。 id = \'" + id + "\'");
		}
		
		PropertyDictionary property = ref.getProperty();
		byte professionId = MGPropertyAccesser.getProfessionId(property);
		if(professionId < 0 || professionId > 4) {
			error(gameRefObject, "职业id错误，职业id应该是0到4之间的整数。当前职业id = " + professionId);
		}
		int targetNum = MGPropertyAccesser.getTargetNum(property);
		if(targetNum < 0 || targetNum > 2) {
			error(gameRefObject, "攻击目标错误，攻击目标应该是0到2之间的整数。当前攻击目标 = " + targetNum);
		}
		
	}

}
