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
package sophia.mmorpg.base.sprite;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.ref.region.SceneSafeRegion;
import sophia.mmorpg.base.sprite.ai.SpritePerceiveComponent;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteAttack_GE;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteInjured_GE;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgrComponent;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyModifyTransaction;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent;
import sophia.mmorpg.base.sprite.state.AdjunctionState;
import sophia.mmorpg.base.sprite.state.FightSpriteStateMgr;
import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.core.state.FSMStateBase;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public abstract class FightSprite extends Sprite {
	private static final Logger logger = Logger.getLogger(FightSprite.class);
	
	/** 修改hp成功 */
	public static final byte MODIFY_HP_SUCCESS = 0;
	/** 修改hp失败 */
	public static final byte MODIFY_HP_FAILURE = -1;
	/** 修改hp导致死亡 */
	public static final byte MODIFY_HP_DEAD = 1;

	protected FightPropertyMgrComponent fightPropertyMgrComponent;

	protected FightSkillRuntimeComponent<?> fightSkillRuntimeComponent;

	protected final FightSpriteStateMgr fightSpriteStateMgr = new FightSpriteStateMgr(this);

	private Monster summonMonster;

	private final ReentrantLock performLock = new ReentrantLock();
	
	// 用于标记是否下线
	private volatile boolean online = false;
	
	// 复活是否Ready
	private volatile boolean isReviveReady = true;

	protected FightSprite() {
		fightPropertyMgrComponent = (FightPropertyMgrComponent) createComponent(FightPropertyMgrComponent.class);
	}

	public final FightPropertyMgrComponent getFightPropertyMgrComponent() {
		return fightPropertyMgrComponent;
	}

	public final FightSkillRuntimeComponent<?> getFightSkillRuntimeComponent() {
		return fightSkillRuntimeComponent;
	}

	public final FightSpriteStateMgr getFightSpriteStateMgr() {
		return fightSpriteStateMgr;
	}

	public void performActionEvent(final ActionEventBase actionEvent) {
		performLock.lock();
		try {
			long l = System.currentTimeMillis();
			handleActionEvent(actionEvent);
			l = System.currentTimeMillis() - l;
			if (l >= 100) {
				logger.error(this + " 处理请求：" + actionEvent + "时间过久：" + l + "毫秒");
			}
		} finally {
			performLock.unlock();
		}
	}
	
	public int getAttackSpeed() {
		return fightPropertyMgrComponent.getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeed_Id);
	}

	public int getMoveSpeed() {
		return fightPropertyMgrComponent.getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeed_Id);
	}

	public int getHP() {
		return fightPropertyMgrComponent.getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.HP_Id);
	}

	public int getHPMax() {
		return fightPropertyMgrComponent.getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MaxHP_Id);
	}

	public int getMP() {
		return fightPropertyMgrComponent.getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MP_Id);
	}

	public int getMPMax() {
		return fightPropertyMgrComponent.getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MaxMP_Id);
	}
	
	private void onDamage(final FightSprite attacker) {
		FightSpriteInjured_GE injuredGE = new FightSpriteInjured_GE();
		injuredGE.setAttacker(attacker);
		GameEvent<?> event = GameEvent.getInstance(SpritePerceiveComponent.FightSpriteInjured_GE_Id, injuredGE);
		this.handleGameEvent(event);
		GameEvent.pool(event);
		if (attacker != null) {
			FightSpriteAttack_GE attackGE = new FightSpriteAttack_GE();
			attackGE.setBeAttacker(this);
			event = GameEvent.getInstance(SpritePerceiveComponent.FightSpriteAttack_GE_Id, attackGE);
			attacker.handleGameEvent(event);
			GameEvent.pool(event);
		}
	}
	
	private void setDead() {
		fightSpriteStateMgr.switchState(DeadState.DeadState_Id);
		getPathComponent().silentStop();
	}
	
	public boolean isDead() {
		if (fightSpriteStateMgr.isState(DeadState.DeadState_Id)) {
			return true;
		}

		return false;
	}
	
	protected byte changeHP(final FightSprite attacker, final int hp) {
		if (!isReviveReady()) {
			return MODIFY_HP_FAILURE;
		}
		
		boolean isDamage = false;
		int curHP = 0;
		synchronized (this) {
			if (!isReviveReady()) {
				return MODIFY_HP_FAILURE;
			}
			
			int oldHP = getHP();
			// 为了预防目标未死亡，但是血量为0的情况，导致杀不死，这里加上isDead判定
			if (oldHP <= 0 && isDead()) {
				return MODIFY_HP_FAILURE;
			}

			FightPropertyModifyTransaction modifyTransaction = fightPropertyMgrComponent.getFightPropertyMgr().getModifyTransaction();
			curHP = modifyTransaction.modifyHP(hp);
			if (oldHP >= curHP && hp <= 0) {
				isDamage = true;
			}
			
			// 死亡，死亡后不产生伤害
			if (curHP <= 0) {
				setReviveReady(false);
				setDead();
				return MODIFY_HP_DEAD;
			}
		}

		// 产生伤害
		if (isDamage) {
			onDamage(attacker);
		}

		return MODIFY_HP_SUCCESS;
	}

	public abstract boolean modifyHP(final FightSprite attacker, final int hp);
	
	public boolean modifyMP(final int mp) {
		if (isDead()) {
			return false;
		}

		synchronized (this) {
			int oldMP = getMP();
			if (oldMP <= 0 && mp < 0) {
				return false;
			}

			FightPropertyModifyTransaction modifyTransaction = fightPropertyMgrComponent.getFightPropertyMgr().getModifyTransaction();
			modifyTransaction.modifyMP(mp);
		}

		return true;
	}

	public boolean applyHP(FightSprite attacker, final int hp) {
		return false;
	}

	public boolean applyMP(final int mp) {
		return false;
	}

	public List<FSMStateBase<FightSprite>> getStateList() {
		return fightSpriteStateMgr.getStateList();
	}

	public boolean checkBroadcast(FSMStateBase<FightSprite> state) {
		if (state instanceof AdjunctionState) {
			return true;
		}

		if (state.getId() == PluckingState.PluckingState_Id) {
			return true;
		}

		return false;
	}

	public void broadcastState() {
	}

	public boolean changeState(final short id) {
		FSMStateBase<FightSprite> state = fightSpriteStateMgr.getState(id);
		if (!fightSpriteStateMgr.switchState(state)) {
			return false;
		}

		if (checkBroadcast(state)) {
			broadcastState();
		}

		return true;
	}

	public boolean cancelState(final short id) {
		FSMStateBase<FightSprite> state = fightSpriteStateMgr.getState(id);
		if (!fightSpriteStateMgr.cancelState(state)) {
			return false;
		}

		if (checkBroadcast(state)) {
			broadcastState();
		}

		return true;
	}

	public abstract boolean isEnemyTo(FightSprite fightSprite);

	public boolean isInSafeRegion() {
		SceneGrid currentPos = this.getSceneGrid();
		SceneSafeRegion safeRegion = this.getCrtScene().getSafeRegion();
		if (safeRegion == null) {
			return false;
		}
		boolean inSafeRegion = safeRegion.getRegion().contains(currentPos);
		return inSafeRegion;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean onlineFlag) {
		online = onlineFlag;
	}

	public Monster getSummonMonster() {
		return summonMonster;
	}

	public void setSummonMonster(Monster summonMonster) {
		this.summonMonster = summonMonster;
	}
	
	public boolean isReviveReady() {
		return isReviveReady;
	}

	public void setReviveReady(boolean isReviveReady) {
		this.isReviveReady = isReviveReady;
	}
	
	public void revive() {
		setReviveReady(true);
	}
}
