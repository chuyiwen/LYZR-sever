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
package sophia.mmorpg.player.fightSkill.event;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree;

public class G2C_GetLearnedSkillList extends ActionEventBase {
	private PlayerFightSkillTree skillTree;

	public G2C_GetLearnedSkillList() {
		super();
		this.actionEventId = FightSkillDefines.G2C_GetLearnedSkillList;
	}

	public G2C_GetLearnedSkillList(PlayerFightSkillTree skillTree) {
		super();
		this.skillTree = skillTree;
		this.actionEventId = FightSkillDefines.G2C_GetLearnedSkillList;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		ArrayList<FightSkill> skills = new ArrayList<>();
		skills.addAll(skillTree.getLearnedSkills());
		Collection<FightSkill> learnedExtendedSkills = skillTree.getLearnedExtendedSkills();
		skills.addAll(learnedExtendedSkills);
		buffer.put((byte) skills.size());
		for (FightSkill skill : skills) {
			putString(buffer, skill.getRefId());
			buffer.put((byte)skill.getLevel());
			buffer.putShort((short)skill.getExp());
			int slotIndex = skillTree.getShortcutSlotIndex(skill.getRefId());
			buffer.put((byte) slotIndex);
		}
		return buffer;
	}

	@Override
	public String toString() {
		return "G2C_GetLearnedSkillList [skillTree=" + skillTree + "]";
	}
}
