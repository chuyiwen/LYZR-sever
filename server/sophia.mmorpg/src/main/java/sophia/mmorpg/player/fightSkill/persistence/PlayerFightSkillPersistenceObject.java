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
package sophia.mmorpg.player.fightSkill.persistence;

import java.util.ArrayList;
import java.util.Collection;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree;

public class PlayerFightSkillPersistenceObject extends AbstractPersistenceObject {
	private static final String skillFieldName = "skillData";
	private PlayerFightSkillTree skillTree;
	private PersistenceParameter skillPersistenceParameter;
	private PlayerFightSkillReadWrite skillReadWrite;

	public PlayerFightSkillPersistenceObject(PlayerFightSkillTree skillTree, Player player) {
		super();
		this.skillTree = skillTree;
		this.skillPersistenceParameter = new PersistenceParameter();
		this.skillPersistenceParameter.setName(skillFieldName);
		this.skillReadWrite = new PlayerFightSkillReadWrite(skillTree, player);
		this.persistenceParameters = new ArrayList<>(1);
		this.persistenceParameters.add(this.skillPersistenceParameter);
	}

	@Override
	public void snapshot() {
		skillPersistenceParameter.setValue(skillReadWrite.toBytes(skillTree));
	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for (PersistenceParameter persistenceParameter : persistenceParameters) {
			String name = persistenceParameter.getName();
			if (skillFieldName.equals(name)) {
				skillReadWrite.fromBytes((byte[]) persistenceParameter.getValue());
			}
		}

		// attach skill effect when data is loaded from database
		attachAllSkillEffects();
	}

	public void attachAllSkillEffects() {
		for (FightSkill skill : this.skillTree.getLearnedSkills()) {
			if (skill.getRef().isAttributeSkill()) {
				FightPropertyEffectFacade.attachWithoutSnapshot(this.skillTree.getPlayer(), skill.getLevelRef().getRuntimeParameter());
			}
		}
	}

}
