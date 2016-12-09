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
package newbee.morningGlory.mmorpg.player.fightSkill;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillComponent;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTreeGameEventDelegate;
import sophia.mmorpg.player.fightSkill.gameevent.SkillLevelUp_GE;
import sophia.mmorpg.player.fightSkill.gameevent.SkillUseSkill_GE;
import sophia.mmorpg.player.fightSkill.ref.PlayerFightSkillRefTree;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;

public final class MGPlayerFightSkillTreeGameEventDelegate implements PlayerFightSkillTreeGameEventDelegate {
	private static final Logger logger = Logger.getLogger(MGPlayerFightSkillTreeGameEventDelegate.class);

	public MGPlayerFightSkillTreeGameEventDelegate() {

	}

	@Override
	public void handleGameEvent(GameEvent<?> event, PlayerFightSkillComponent owner) {
		checkArgument(event != null && owner != null);
		if (event.isId(PlayerLevelUp_GE.class.getSimpleName())) {
			Player player = owner.getConcreteParent();
			byte professionId = player.getProfession();
			int playerLevel = player.getExpComponent().getLevel();
			if (logger.isDebugEnabled()) {
				logger.debug("Player Level Up GameEvent player profession: " + professionId + " player level: " + playerLevel);
			}

			PlayerFightSkillTree playerFightSkillTree = player.getPlayerFightSkillComponent().getPlayerFightSkillTree();
			List<SkillRef> availableSkills = PlayerFightSkillRefTree.getAvailableSkills(professionId, playerLevel);
			for (SkillRef skillRef : availableSkills) {
				if (!playerFightSkillTree.isLearned(skillRef.getId())) {
					playerFightSkillTree.learnSkill(skillRef.getId());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("available skills: " + availableSkills);
				logger.debug("all learned skills: " + playerFightSkillTree.getLearnedSkills());
			}

			// level up a skill if the skill's experience already surpassed
			// the max experience required and the player level is now fulfill
			// the required level
			Collection<FightSkill> allLearnedSkills = playerFightSkillTree.getAllLearnedSkills();
			for (FightSkill skill : allLearnedSkills) {
				playerFightSkillTree.levelUp(skill);
			}
		} else if (event.isId(SkillLevelUp_GE.class.getSimpleName())) {
			SkillLevelUp_GE message = (SkillLevelUp_GE) event.getData();
			String refId = message.getRefId();
			owner.getPlayerFightSkillTree().levelUp(refId);
		} else if (event.isId(SkillUseSkill_GE.class.getSimpleName())) {
			SkillUseSkill_GE message = (SkillUseSkill_GE) event.getData();
			String refId = message.getRefId();
			PlayerFightSkillTree playerFightSkillTree = owner.getPlayerFightSkillTree();
			FightSkill fightSkill = playerFightSkillTree.getFightSkill(refId);
			playerFightSkillTree.useSkill(fightSkill);
			if (logger.isDebugEnabled()) {
				logger.debug("handle SkillUseSkill_GE: skill: " + fightSkill);
			}

			// basic attack will add experience of an attribute skill
			if (StringUtils.equals(refId, SkillRef.basicAttackRefId)) {
				Collection<FightSkill> learnedSkills = playerFightSkillTree.getLearnedSkills();
				for (FightSkill skill : learnedSkills) {
					if (skill.getRef().isAttributeSkill()) {
						playerFightSkillTree.useSkill(skill.getRefId());
					}
				}
			}

			// use an extended skill will also add experience of the skill it's
			// based on.
			if (fightSkill.getRef().isExtendedSkill()) {
				FightSkill baseSkill = FightSkillRuntimeHelper.getBaseSkill(owner.getConcreteParent(), fightSkill);
				playerFightSkillTree.useSkill(baseSkill);
			}
		}

	}

}
