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

import newbee.morningGlory.mmorpg.monster.gameEvent.SkillMonsterAttack_GE;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.ai.SpritePerceiveComponent;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteOwnerInjured_GE;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;

/**
 * 道士职业-召唤怪组件
 */
public final class PlayerSummonMonsterComponent extends ConcreteComponent<FightSprite> {
	private Monster summonMonster;

	public static final String Tag = "PlayerSummonMonsterComponent";

	public static final String spriteId = SpritePerceiveComponent.FightSpriteOwnerInjured_GE_Id;

	public static final String SkillMonsterAttack_GE_ID = SkillMonsterAttack_GE.class.getSimpleName();

	public static final String AfterAttack_GE_Id = AfterAttack_GE.class.getSimpleName();

	public static final String PlayerDead_GE_ID = PlayerDead_GE.class.getSimpleName();

	public PlayerSummonMonsterComponent() {
	}

	@Override
	public void ready() {
		addInterGameEventListener(AfterAttack_GE_Id);
		addInterGameEventListener(spriteId);
		addInterGameEventListener(SkillMonsterAttack_GE_ID);
		addInterGameEventListener(PlayerDead_GE_ID);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(AfterAttack_GE_Id);
		removeInterGameEventListener(spriteId);
		removeInterGameEventListener(SkillMonsterAttack_GE_ID);
		removeInterGameEventListener(PlayerDead_GE_ID);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (summonMonster == null || summonMonster.isDead()) {
			return;
		}
		if (event.isId(SkillMonsterAttack_GE_ID)) {
			SkillMonsterAttack_GE ge = (SkillMonsterAttack_GE) event.getData();
			if (ge.getfighterId().equals(summonMonster.getId())) {
				SummonMonsterExpComponent monsterExpComponent = (SummonMonsterExpComponent) summonMonster.getTagged(SummonMonsterExpComponent.Tag);
				monsterExpComponent.addExp(1);
			}
		} else if (event.isId(spriteId)) {
			FightSpriteOwnerInjured_GE ge = (FightSpriteOwnerInjured_GE) event.getData();
			sendGameEvent(spriteId, ge, summonMonster.getId());
		} else if (event.isId(AfterAttack_GE_Id)) {
			AfterAttack_GE ge = (AfterAttack_GE) event.getData();
			sendGameEvent(AfterAttack_GE_Id, ge, summonMonster.getId());
		} else if (event.isId(PlayerDead_GE_ID)) {
			PlayerDead_GE ge = (PlayerDead_GE) event.getData();
			sendGameEvent(PlayerDead_GE_ID, ge, summonMonster.getId());
		}
		super.handleGameEvent(event);
	}

	public boolean hasSummonMonster() {
		return summonMonster != null;
	}

	public Monster getSummonMonster() {
		return summonMonster;
	}

	public void setSummonMonster(Monster summonMonster) {
		this.summonMonster = summonMonster;
	}
}
