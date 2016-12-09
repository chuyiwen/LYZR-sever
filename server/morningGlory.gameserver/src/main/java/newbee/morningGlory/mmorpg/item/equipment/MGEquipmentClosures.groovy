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
package newbee.morningGlory.mmorpg.item.equipment

import newbee.morningGlory.mmorpg.player.wing.MGWingEffectMgr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger

import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper;
import sophia.mmorpg.item.Item
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.utils.RuntimeResult
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser

class MGEquipmentClosures {
	private static final Logger logger = Logger
	.getLogger(MGEquipmentClosures.class.getName());

	Set<String> commEquipmentIdSet = new HashSet<>();


	Map<String, Closure<RuntimeResult>> specalEquipmentMap = new HashMap<String, Closure<RuntimeResult>>();

	public MGEquipmentClosures() {
		specalEquipmentMap.put("commEquipmentEquipTo", commEquipmentEquipTo);
		specalEquipmentMap.put("commEquipmentDeequipFrom", commEquipmentDeequipFrom);
	}



	static Closure<RuntimeResult> commEquipmentEquipTo = {Player player, Item equipment ->

		learnExtendedSkill.call(player, equipment)
	}

	static Closure<RuntimeResult> commEquipmentDeequipFrom = {Player player, Item equipment ->
		unlearnExtendedSkill.call(player, equipment)
	}

	static Closure<RuntimeResult> learnExtendedSkill = { Player player, Item equipment ->
		String skillRefId = MGPropertyAccesser.getSkillRefId(equipment.getItemRef().getProperty());
		if(!StringUtils.isBlank(skillRefId)) {
			SkillRef ref = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(skillRefId);
			if(ref!=null){
				boolean isLearned = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().isLearned(skillRefId)
				if(!isLearned) {
					FightSkill extendedSkill = new FightSkill(skillRefId, ref);
					FightSkill skill = FightSkillRuntimeHelper.getBaseSkill(player, extendedSkill);
					extendedSkill.setLevel(skill.getLevel());
					extendedSkill.setExp(skill.getExp());
					player.getPlayerFightSkillComponent().getPlayerFightSkillTree().learnExtendedSkill(extendedSkill);
				}
			}
		}
	}

	static Closure<RuntimeResult> unlearnExtendedSkill = { Player player, Item equipment ->
		String skillRefId = MGPropertyAccesser.getSkillRefId(equipment.getItemRef().getProperty());
		if(!StringUtils.isBlank(skillRefId)) {
			player.getPlayerFightSkillComponent().getPlayerFightSkillTree().removeExtendedSkill(skillRefId);
		}
	}
}
