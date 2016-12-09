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
package sophia.mmorpg.player.fightSkill;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.stat.StatFunctions;

public final class PlayerFightSkillTree {
	private static final Logger logger = Logger.getLogger(PlayerFightSkillTree.class);
	// SkillRefId -> FightSkill
	private Map<String, FightSkill> learnedSkills = new HashMap<String, FightSkill>();
	private Map<String, FightSkill> extendedSkills = new HashMap<String, FightSkill>();
	// slotId -> SkillRefId
	private Map<Integer, String> shortcutSkills = new HashMap<Integer, String>();
	private static int slotIndexNotUsed = -1;
	private static int maxSlotIndex = 8;

	private Player player;

	public PlayerFightSkillTree(Player player) {
		this.player = player;
	}

	public FightSkill getBaseSkill(String refId) {
		return learnedSkills.get(refId);
	}

	public FightSkill getExtenedSkill(String refId) {
		return extendedSkills.get(refId);
	}

	public FightSkill getFightSkill(String refId) {
		SkillRef ref = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (ref != null) {
			if (ref.isBaseSkill()) {
				return learnedSkills.get(refId);
			} else if (ref.isExtendedSkill()) {
				return extendedSkills.get(refId);
			}
		}
		return null;
	}

	public Map<Integer, String> getShortcutSkills() {
		return shortcutSkills;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isLearned(String refId) {
		boolean learnedAsBaseSkill = learnedSkills.containsKey(refId);
		boolean learnedAsExtendedSkill = extendedSkills.containsKey(refId);
		return learnedAsBaseSkill || learnedAsExtendedSkill;
	}

	public void learn(FightSkill skill) {
		learnedSkills.put(skill.getRefId(), skill);
	}

	public void learnExtendedSkill(FightSkill skill) {
		extendedSkills.put(skill.getRefId(), skill);
	}

	public void removeExtendedSkill(String skillRefId) {
		extendedSkills.remove(skillRefId);
	}

	public int getShortcutSlotIndex(String skillRefId) {
		for (Map.Entry<Integer, String> entry : this.shortcutSkills.entrySet()) {
			if (entry.getValue().equals(skillRefId)) {
				return entry.getKey();
			}
		}
		return slotIndexNotUsed;
	}

	public void setShortcutSkill(int slotIndex, String refId) {
		checkArgument(isLearned(refId));
		// slotIndex range [1, 8] or -1
		if (slotIndex < slotIndexNotUsed || slotIndex > maxSlotIndex || slotIndex == 0) {
			return;
		}
		int currentIndex = getShortcutSlotIndex(refId);
		if (currentIndex != slotIndex || slotIndex == slotIndexNotUsed) {
			this.shortcutSkills.remove(currentIndex);
		}
		this.shortcutSkills.put(slotIndex, refId);
	}

	public void removeShortcutSkill(int slotIndex) {
		this.shortcutSkills.remove(slotIndex);
	}

	public void remove(FightSkill skill) {
		this.learnedSkills.remove(skill.getRefId());
	}

	public void remove(String refId) {
		this.learnedSkills.remove(refId);
	}

	public Collection<FightSkill> getLearnedSkills() {
		return learnedSkills.values();
	}

	public Collection<FightSkill> getLearnedExtendedSkills() {
		return extendedSkills.values();
	}

	public Collection<FightSkill> getAllLearnedSkills() {
		ArrayList<FightSkill> allSkills = new ArrayList<>();
		Collection<FightSkill> learnedBaseSkills = getLearnedSkills();
		Collection<FightSkill> learnedExtendedSkills = getLearnedExtendedSkills();
		allSkills.addAll(learnedBaseSkills);
		allSkills.addAll(learnedExtendedSkills);
		return allSkills;
	}

	public void learnSkill(String refId) {
		SkillRef skillRef = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (skillRef == null) {
			return;
		}

		FightSkill fightSkill = new FightSkill(refId, skillRef);

		learn(fightSkill);
		attachAndNotify(fightSkill);
	}

	public void addExp(String refId, int inc) {
		SkillRef skillRef = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (skillRef == null) {
			return;
		}

		FightSkill skill = getFightSkill(refId);
		if (skill == null) {
			return;
		}
		
		int level = player.getExpComponent().getLevel();
		int oldLevel = skill.getLevel();
		detach(skill);
		skill.addExpAndCheckLevelUp(level, inc);
		attachAndNotify(skill);
		int newLevel = skill.getLevel();
		if(newLevel > oldLevel){
			StatFunctions.SkillLevelStat(player, skill.getName(), skill.getLevel(), skill.getExp());
			if (!skill.isBaseSkill()) {
				FightSkill baseSkill = FightSkillRuntimeHelper.getBaseSkill(player, skill);
				baseSkill.justCheckMaxLevelUp();
				StatFunctions.SkillLevelStat(player, baseSkill.getName(), baseSkill.getLevel(), baseSkill.getExp());
			}
		}
	}

	public void levelUp(String refId) {
		FightSkill skill = getFightSkill(refId);
		levelUp(skill);
	}

	public void levelUp(FightSkill skill) {
		if (skill == null) {
			return;
		}

		int level = player.getExpComponent().getLevel();
		boolean canLevelUp = skill.canLevelUp(level);
		if (!canLevelUp) {
			return;
		}

		detach(skill);
		if (skill.checkLevelUp(level) && !skill.isBaseSkill()) {
			FightSkill baseSkill = FightSkillRuntimeHelper.getBaseSkill(player, skill);
			baseSkill.justCheckMaxLevelUp();
			StatFunctions.SkillLevelStat(player, baseSkill.getName(), baseSkill.getLevel(), baseSkill.getExp());
		}
		
		attachAndNotify(skill);
		StatFunctions.SkillLevelStat(player, skill.getName(), skill.getLevel(), skill.getExp());
	}

	public void useSkill(String refId) {
		FightSkill skill = getFightSkill(refId);
		useSkill(skill);
	}

	public void useSkill(FightSkill skill) {
		if (skill == null) {
			return;
		}

		skill.use();
		levelUp(skill);
	}

	public void attachAndNotify(FightSkill skill) {
		checkArgument(skill != null);
		if (skill.getRef().isAttributeSkill()) {
			SkillLevelRef levelRef = skill.getLevelRef();
			if (levelRef != null && levelRef.getRuntimeParameter() != null) {
				FightPropertyEffectFacade.attachAndNotify(this.player, skill.getLevelRef().getRuntimeParameter());
			}
		}

	}

	public void detach(FightSkill skill) {
		checkArgument(skill != null);
		if (skill.getRef().isAttributeSkill()) {
			SkillLevelRef levelRef = skill.getLevelRef();
			if (levelRef != null && levelRef.getRuntimeParameter() != null) {
				FightPropertyEffectFacade.detachAndSnapshot(this.player, skill.getLevelRef().getRuntimeParameter());
			}
		}

	}

	@Override
	public String toString() {
		return "PlayerFightSkillTree [learnedSkills=" + learnedSkills + ", shortcutSkills=" + shortcutSkills + ", player=" + player + "]";
	}
}
