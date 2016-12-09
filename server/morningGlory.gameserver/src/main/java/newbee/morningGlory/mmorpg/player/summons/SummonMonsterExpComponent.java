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
package newbee.morningGlory.mmorpg.player.summons;

import java.util.ArrayList;
import java.util.Collection;

import newbee.morningGlory.mmorpg.monster.gameEvent.SkillMonsterLevelUp_GE;
import newbee.morningGlory.mmorpg.monster.ref.SkillMonsterLevelRef;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.monster.Monster;

/**
 * 召唤怪-等级组件
 */
public final class SummonMonsterExpComponent extends ConcreteComponent<Monster> {

	public static final String Tag = "SummonMonsterExpComponent";

	public static final String SkillMonsterLevelUp_GE_Id = SkillMonsterLevelUp_GE.class.getSimpleName();

	private static Collection<SkillMonsterLevelRef> skillMonsterRefs = new ArrayList<>();
	
	public static void addSkillMonsterLevelRef(SkillMonsterLevelRef skillMonsterLevelRef) {
		skillMonsterRefs.add(skillMonsterLevelRef);
	}

	public SummonMonsterExpComponent() {
	}

	private long exp;

	private Monster monster;

	public synchronized void setExp(long value) {
		if (value <= 0)
			return;
		setExpImpl(value);
	}

	public synchronized void addExp(int delta) {
		if (delta <= 0)
			return;
		setExp(exp + delta);
	}

	public synchronized void incrementLevel() {
		String monsterNextRefId = null;
		for (SkillMonsterLevelRef ref : skillMonsterRefs) {
			if (ref.getId().equals(monster.getMonsterRef().getId())) {
				monsterNextRefId = ref.getNextMonsterRefId();
			}
		}
		if (monsterNextRefId == null) {
			// 抛出错误
			return;
		}
		SkillMonsterLevelUp_GE ge = new SkillMonsterLevelUp_GE(monsterNextRefId);
		GameEvent<SkillMonsterLevelUp_GE> ge2 = (GameEvent<SkillMonsterLevelUp_GE>) GameEvent.getInstance(SkillMonsterLevelUp_GE_Id, ge);
		sendGameEvent(ge2, monster.getId());
	}

	public synchronized long getExp() {
		return exp;
	}

	public synchronized int getLevel() {
		for (SkillMonsterLevelRef ref : skillMonsterRefs) {
			if (ref.getId().equals(monster.getMonsterRef().getId())) {
				return ref.getLevel();
			}
		}
		return 0;
	}

	private synchronized void setExpImpl(long value) {
		monster = getConcreteParent();
		if (value <= 0)
			return;

		long deltaExp = value;

		while (deltaExp > 0) {
			long maxExp = maxExp();
			if (deltaExp >= maxExp) {
				if (getLevel() < maxLevel()) {
					deltaExp = maxExp - deltaExp;
					incrementLevel();
				} else {
					this.exp = maxExp;
					deltaExp = 0;
				}
			} else {
				this.exp = deltaExp;
				deltaExp = 0;
			}
		}
	}

	private long maxExp() {
		for (SkillMonsterLevelRef ref : skillMonsterRefs) {
			if (ref.getId().equals(monster.getMonsterRef().getId())) {
				return ref.getExpNeed();
			}
		}
		return 0;
	}

	private int maxLevel() {
		return 7;
	}

}
