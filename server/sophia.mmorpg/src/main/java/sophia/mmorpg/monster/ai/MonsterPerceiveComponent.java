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
package sophia.mmorpg.monster.ai;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.util.Position;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.ai.SpritePerceiveComponent;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteInjured_GE;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper;
import sophia.mmorpg.base.sprite.state.global.FightState;
import sophia.mmorpg.base.sprite.state.movement.ChaseState;
import sophia.mmorpg.base.sprite.state.movement.PatrolState;
import sophia.mmorpg.base.sprite.state.movement.ReturnToBirthState;
import sophia.mmorpg.monster.Monster;

public class MonsterPerceiveComponent extends SpritePerceiveComponent<Monster> {
	private static final Logger logger = Logger.getLogger(MonsterPerceiveComponent.class);

	public static final long seekInterval = 3000;
	public static final long patrolInterval = 6000;
	public static final long recoverInterval = 10000;
	public static final long chaseInterval = 4000;
	public static final long returnInterval = 1000;

	@Override
	public void ready() {
		addInterGameEventListener(FightSpriteDead_GE_Id);
		addInterGameEventListener(FightSpriteInjured_GE_Id);
		addInterGameEventListener(SceneTick_GE_Id);
		addInterGameEventListener(PatrolState.PatrolStateExit_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(FightSpriteDead_GE_Id);
		removeInterGameEventListener(FightSpriteInjured_GE_Id);
		removeInterGameEventListener(SceneTick_GE_Id);
		removeInterGameEventListener(PatrolState.PatrolStateExit_Id);
		super.suspend();
	}

	public synchronized void chaseAndAttack(Monster monster, FightSprite target, long now) {
		FightSkill skill = monster.getRandomSkill();
		// attack
		boolean canCastSkill = MonsterAIHelper.canCastSkill(monster, target, skill, now);
		if (canCastSkill) {
			if (skill.getRef().isTargetSkill()) {
				monster.getFightSkillRuntimeComponent().castingSkill(skill, target);
			} else if (skill.getRef().isDirectionSkill()) {
				byte direction = FightSkillRuntimeHelper.getDirection(monster.getCrtPosition(), target.getCrtPosition());
				monster.getFightSkillRuntimeComponent().castingSkill(skill, direction);
			} else if (skill.getRef().isGridSkill()) {
				monster.getFightSkillRuntimeComponent().castingSkill(skill, target.getCrtPosition());
			}
			updateLastAttackTime(now);
			monster.changeState(FightState.FightState_Id);
			if (target.isDead()) {
				// forsake lastTarget;
				Collection<Monster> monsters = GameSceneHelper.getAOIInterestedMonsters(target);
				for (Monster m : monsters) {
					if (m.getMonsterRef().isRegularMonster()) {
						if (target.equals(getLastTarget())) {
							forsakeTarget();
						}
					}
				}
			}
		}

		// chase
		boolean neededToChase = MonsterAIHelper.isNeededToChase(monster, target, skill, now);
		if (neededToChase) {
			Position nextPosition = MonsterAIHelper.getNextPosition(monster, target);
			Position targetPosition = target.getPathComponent().getEndPosition();
			boolean isWalkable = GameSceneHelper.isWalkable(monster.getCrtScene(), nextPosition) && !nextPosition.equals(targetPosition);
			if (isWalkable) {
				if (logger.isDebugEnabled()) {
					logger.debug("chaseAndAttack chase nextPosition: " + nextPosition + " isWalkable " + isWalkable + " monster: " + monster + " target: " + target
							+ " target's target position " + targetPosition);
					checkArgument(!nextPosition.equals(target.getCrtPosition()), " monster's nextPosition is duplicate with player's current position");
					checkArgument(!nextPosition.equals(targetPosition), "monster's nextPosition is duplicate with player's target position");
				}
				monster.getPathComponent().stopMove(nextPosition);
				updateLastChaseTime(now);
				monster.changeState(ChaseState.ChaseState_Id);
			}
		}

	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(SceneTick_GE_Id)) {
			// SceneTick_GE ge = (SceneTick_GE) event.getData();
			// long now = ge.getTimestamp();
			long now = System.currentTimeMillis();
			Monster monster = getConcreteParent();

			monster.recoverHP(now);

			forsakeLootOwner(monster, now);

			if (selfCheckUnactBuffer(monster)) {
				return;
			}

			if (!monster.isWithinBirthRange()) {
				monster.changeState(ReturnToBirthState.ReturnToBirthState_Id);
			}

			if (monster.getMonsterRef().isPassiveMonster()) {

				chaseAndAttack(monster, getLastTarget(), now);

				// patrol
				boolean needToPatrol = MonsterAIHelper.isNeededToPatrol(monster, now);
				if (needToPatrol) {
					Position patrolTo = MonsterAIHelper.getNextPatrolPosition(monster);
					boolean isWalkable = GameSceneHelper.isWalkable(monster.getCrtScene(), patrolTo);
					if (isWalkable && !patrolTo.equals(monster.getCrtPosition())) {
						monster.getPathComponent().stopMove(patrolTo);
						updateLastPatrolTime(now);
						monster.changeState(PatrolState.PatrolState_Id);
					}
				}

			} else if (monster.getMonsterRef().isActiveMonster()) {

				boolean neededToSeek = MonsterAIHelper.isNeededToSeek(monster, now);
				if (neededToSeek) {
					byte attackDistance = monster.getMonsterRef().getAttackDistance();
					FightSprite nearestEnemy = GameSceneHelper.getNearestPlayer(monster, attackDistance);
					setLastTarget(nearestEnemy);
					updateLastSeekTime(now);
				}

				chaseAndAttack(monster, getLastTarget(), now);

			}

			// return
			boolean neededToReturn = MonsterAIHelper.isNeededToReturn(monster, now);
			if (neededToReturn) {
				Position nextPositionTowardBirth = MonsterAIHelper.getNextPositionTowardBirth(monster);
				if (nextPositionTowardBirth != null) {
					monster.getPathComponent().stopMove(nextPositionTowardBirth);
					updateLastReturnTime(now);
					forsakeTarget();
				}
			}

			boolean returnedToBirth = GameSceneHelper.distance(monster.getCrtScene(), monster.getCrtPosition(), monster.getBirthPosition()) <= 1;
			if (returnedToBirth) {
				monster.cancelState(ReturnToBirthState.ReturnToBirthState_Id);
			}

		} else if (event.isId(FightSpriteInjured_GE_Id)) {
			FightSpriteInjured_GE ge = (FightSpriteInjured_GE) event.getData();
			FightSprite attacker = ge.getAttacker();

			long now = System.currentTimeMillis();
			updateLootOwner(attacker, getConcreteParent(), now);

			if (attacker instanceof Monster) {
				Monster monster = (Monster) attacker;
				boolean isSummonMonster = monster.getMonsterRef().isSkillSummon();
				if (isSummonMonster && monster.getOwner() != null) {
					attacker = monster.getOwner();
				}
			}

			if (getConcreteParent().isEnemyTo(attacker)) {
				setLastAttacker(attacker);
				setLastTarget(attacker);
				getConcreteParent().changeState(FightState.FightState_Id);
			}
		} else if (event.isId(PatrolState.PatrolStateExit_Id)) {
			updateLastPatrolTime(System.currentTimeMillis());
		}

		super.handleGameEvent(event);
	}
}
