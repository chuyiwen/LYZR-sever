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
package sophia.mmorpg.monster.ref;

import java.util.ArrayList;
import java.util.List;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MonsterRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -7054266793145816430L;

	private byte canMove;

	private List<FightSkill> fightSkillList = new ArrayList<>();

	public List<FightSkill> getFightSkillList() {
		return fightSkillList;
	}

	public void setFightSkillList(List<FightSkill> fightSkillList) {
		this.fightSkillList = fightSkillList;
	}

	public FightSkill getFightSkill(final int index) {
		return fightSkillList.get(index);
	}

	public boolean isLearned(String skillRefId) {
		for (FightSkill skill : fightSkillList) {
			if (skill.getRefId().equals(skillRefId)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSummonMonster() {
		int monsterType = MGPropertyAccesser.getKind(this.getProperty());
		return monsterType == 5;
	}

	public boolean isSkillSummon() {
		int monsterType = MGPropertyAccesser.getKind(this.getProperty());
		return monsterType == 6;
	}

	public boolean isRegularMonster() {
		boolean isSummonMonster = isSummonMonster();
		boolean isSkillMonster = isSkillSummon();
		return !isSummonMonster && !isSkillMonster;
	}

	public byte getAttackDistance() {
		return MGPropertyAccesser.getAttackDistance(getProperty());
	}

	public byte getAttackType() {
		return MGPropertyAccesser.getAttackType(getProperty());
	}

	public boolean isActiveMonster() {
		return getAttackType() == 1;
	}

	public boolean isPassiveMonster() {
		return getAttackType() == 2;
	}

	public boolean canMove() {
		return canMove == 1;
	}

	public byte getCanMove() {
		return canMove;
	}

	public void setCanMove(byte canMove) {
		this.canMove = canMove;
	}

	public int getLevel() {
		return MGPropertyAccesser.getLevel(getProperty()); 
	}
	
	public boolean isBoss() {
		return MGPropertyAccesser.getQuality(getProperty()) == (byte) 3;
	}
}
