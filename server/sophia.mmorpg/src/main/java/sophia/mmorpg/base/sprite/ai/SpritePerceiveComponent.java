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
package sophia.mmorpg.base.sprite.ai;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.log4j.Logger;

import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.tick.SceneTick_GE;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteAttack_GE;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteOwnerInjured_GE;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteInjured_GE;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteDead_GE;
import sophia.mmorpg.base.sprite.state.FightSpriteStateMgr;
import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.adjunction.DizzinessState;
import sophia.mmorpg.base.sprite.state.adjunction.ParalysisState;
import sophia.mmorpg.monster.Monster;

public class SpritePerceiveComponent<T extends Sprite> extends ConcreteComponent<T> {
	private static final Logger logger = Logger.getLogger(SpritePerceiveComponent.class);

	public static final String SceneTick_GE_Id = SceneTick_GE.class.getSimpleName();
	public static final String FightSpriteDead_GE_Id = FightSpriteDead_GE.class.getSimpleName();
	public static final String FightSpriteInjured_GE_Id = FightSpriteInjured_GE.class.getSimpleName();
	public static final String FightSpriteAttack_GE_Id = FightSpriteAttack_GE.class.getSimpleName();
	public static final String FightSpriteOwnerInjured_GE_Id = FightSpriteOwnerInjured_GE.class.getSimpleName();

	public static final long changeLootOwnerInterval = 8000;

	private long lastReturnTime;
	private long lastSeekTime;
	
	private long lastRecoverTime;
	private long lastPatrolTime;
	private long lastChaseTime;
	private long lastAttackTime;
	private long lastAttackedTime;
	private FightSprite lastTarget;
	private FightSprite lastAttacker;
	private FightSprite lootOwner;

	public long getLastRecoverTime() {
		return lastRecoverTime;
	}

	public long getLastPatrolTime() {
		return lastPatrolTime;
	}

	public long getLastChaseTime() {
		return lastChaseTime;
	}

	public long getLastAttackTime() {
		return lastAttackTime;
	}

	public FightSprite getLastTarget() {
		return lastTarget;
	}

	public void setLastTarget(FightSprite who) {
		this.lastTarget = who;
	}

	public FightSprite getLastAttacker() {
		return lastAttacker;
	}

	public void setLastAttacker(FightSprite who) {
		this.lastAttacker = who;
	}

	public void reset() {
		lastChaseTime = 0;
		lastAttackTime = 0;
		lastRecoverTime = 0;
		lastPatrolTime = 0;
		lastTarget = null;
		lastAttacker = null;
		lootOwner = null;
	}
	
	public void clearReference() {
		lastTarget = null;
		lastAttacker = null;
		lootOwner = null;
	}

	public void updateLastChaseTime(long now) {
		lastChaseTime = now;
	}

	public void updateLastAttackTime(long now) {
		lastAttackTime = now;
	}

	public void updateLastRecoverTime(long now) {
		lastRecoverTime = now;
	}

	public void updateLastPatrolTime(long now) {
		lastPatrolTime = now;
	}
	
	public void updateLastMoveTime(long now) {
		this.lastPatrolTime = now;
		this.lastChaseTime = now;
	}

	public void forsakeTarget() {
		lastTarget = null;
		lastAttacker = null;
	}

	public void forsakeLootOwner() {
		lootOwner = null;
	}

	public long getLastSeekTime() {
		return lastSeekTime;
	}

	public void updateLastSeekTime(long now) {
		lastSeekTime = now;
	}

	public long getLastAttackedTime() {
		return lastAttackedTime;
	}

	public void updateLastAttackedTime(long now) {
		this.lastAttackedTime = now;
	}

	public FightSprite getLootOwner() {
		return lootOwner;
	}

	public void updateLootOwner(FightSprite lootOwner) {
		this.lootOwner = lootOwner;
	}

	public void updateLootOwner(FightSprite attacker, Monster monster, long now) {
		checkArgument(monster != null);

		if (attacker instanceof Monster) {
			Monster summonMonster = (Monster) attacker;
			if (!summonMonster.getMonsterRef().isRegularMonster() && summonMonster.getOwner() != null) {
				attacker = summonMonster.getOwner();
			}
		}

		FightSprite lootOwner = monster.getPerceiveComponent().getLootOwner();
		long lastAttackedTime = monster.getPerceiveComponent().getLastAttackedTime();
		if (lootOwner != attacker && now - lastAttackedTime >= changeLootOwnerInterval) {
			monster.getPerceiveComponent().updateLootOwner(attacker);
			updateLastAttackedTime(now);
			if (logger.isDebugEnabled()) {
				logger.debug("updateLootOwner update loot owner " + attacker);
			}
		}

		if (lootOwner == attacker) {
			updateLastAttackedTime(now);
			if (logger.isDebugEnabled()) {
				logger.debug("updateLootOwner update loot owner last attack time " + this.getLastAttackedTime());
			}
		}

	}

	public void forsakeLootOwner(Monster monster, long now) {
		checkArgument(monster != null);
		long lastAttackedTime = monster.getPerceiveComponent().getLastAttackedTime();
		FightSprite lootOwner = monster.getPerceiveComponent().getLootOwner();
		if (lootOwner != null && now - lastAttackedTime >= changeLootOwnerInterval) {
			monster.getPerceiveComponent().forsakeLootOwner();

			if (logger.isDebugEnabled()) {
				logger.debug("forsakeLootOwner lootOwner enough time elapsed " + lootOwner + " lastAttackedTime " + lastAttackedTime + " now " + now + " diff "
						+ (now - lastAttackedTime));
			}

			updateLastAttackedTime(0);
		}

		if (lootOwner != null && lootOwner.isDead()) {
			monster.getPerceiveComponent().forsakeLootOwner();
			updateLastAttackedTime(0);
			if (logger.isDebugEnabled()) {
				logger.debug("forsakeLootOwner lootOwner owner dead " + lootOwner);
			}
		}
	}

	public boolean selfCheckUnactBuffer(Monster monster) {
		FightSpriteStateMgr fightSpriteStateMgr = monster.getFightSpriteStateMgr();
		if (fightSpriteStateMgr.isState(DeadState.DeadState_Id)) {
			return true;
		}

		if (fightSpriteStateMgr.isState(DizzinessState.DizzinessState_Id)) {
			return true;
		}

		if (fightSpriteStateMgr.isState(ParalysisState.ParalysisState_Id)) {
			return true;
		}

		return false;
	}

	public long getLastReturnTime() {
		return lastReturnTime;
	}

	public void updateLastReturnTime(long lastReturnTime) {
		this.lastReturnTime = lastReturnTime;
	}
}
