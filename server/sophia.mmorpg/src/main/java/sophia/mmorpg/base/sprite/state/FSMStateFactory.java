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
package sophia.mmorpg.base.sprite.state;

import java.util.HashMap;
import java.util.Map;

import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.base.sprite.state.adjunction.BleedState;
import sophia.mmorpg.base.sprite.state.adjunction.BurningState;
import sophia.mmorpg.base.sprite.state.adjunction.DizzinessState;
import sophia.mmorpg.base.sprite.state.adjunction.DumbingState;
import sophia.mmorpg.base.sprite.state.adjunction.InvincibleState;
import sophia.mmorpg.base.sprite.state.adjunction.MagicImmunityState;
import sophia.mmorpg.base.sprite.state.adjunction.MagicShieldState;
import sophia.mmorpg.base.sprite.state.adjunction.ParalysisState;
import sophia.mmorpg.base.sprite.state.adjunction.PhysicalImmunityState;
import sophia.mmorpg.base.sprite.state.adjunction.PoisoningState;
import sophia.mmorpg.base.sprite.state.adjunction.SlowDownState;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;
import sophia.mmorpg.base.sprite.state.global.FightState;
import sophia.mmorpg.base.sprite.state.global.PKState;
import sophia.mmorpg.base.sprite.state.global.TeamState;
import sophia.mmorpg.base.sprite.state.movement.ChaseState;
import sophia.mmorpg.base.sprite.state.movement.MoveState;
import sophia.mmorpg.base.sprite.state.movement.PatrolState;
import sophia.mmorpg.base.sprite.state.movement.ReturnToBirthState;
import sophia.mmorpg.base.sprite.state.movement.StopState;
import sophia.mmorpg.base.sprite.state.posture.MountedState;
import sophia.mmorpg.base.sprite.state.posture.StandedState;
import sophia.mmorpg.base.sprite.state.posture.WalkState;

public final class FSMStateFactory {
	private static final Map<Short, GlobalState> GlobalState_Map = new HashMap<>();

	private static final Map<Short, MovementState> MovementState_Map = new HashMap<>();

	private static final Map<Short, PostureState> PostureState_Map = new HashMap<>();

	private static final Map<Short, ActionState> ActionState_Map = new HashMap<>();

	private static final Map<Short, AdjunctionState> AdjunctionState_Map = new HashMap<>();

	static {

		FightState fightState = new FightState();
		GlobalState_Map.put(fightState.getId(), fightState);
		TeamState teamState = new TeamState();
		GlobalState_Map.put(teamState.getId(), teamState);
		PKState pkState = new PKState();
		GlobalState_Map.put(pkState.getId(), pkState);

		StopState stopState = new StopState();
		MovementState_Map.put(stopState.getId(), stopState);
		MoveState moveState = new MoveState();
		MovementState_Map.put(moveState.getId(), moveState);
		PatrolState patrolState = new PatrolState();
		MovementState_Map.put(patrolState.getId(), patrolState);
		ChaseState chaseState = new ChaseState();
		MovementState_Map.put(chaseState.getId(), chaseState);
		ReturnToBirthState returnToBirthState = new ReturnToBirthState();
		MovementState_Map.put(returnToBirthState.getId(), returnToBirthState);

		StandedState standedState = new StandedState();
		PostureState_Map.put(standedState.getId(), standedState);
		WalkState walkState = new WalkState();
		PostureState_Map.put(walkState.getId(), walkState);
		MountedState mountedStandedState = new MountedState();
		PostureState_Map.put(mountedStandedState.getId(), mountedStandedState);

		IdleState idleState = new IdleState();
		ActionState_Map.put(idleState.getId(), idleState);
		DeadState deadState = new DeadState();
		ActionState_Map.put(deadState.getId(), deadState);
		PluckingState pluckingState = new PluckingState();
		ActionState_Map.put(pluckingState.getId(), pluckingState);

		BleedState bleedState = new BleedState();
		AdjunctionState_Map.put(bleedState.getId(), bleedState);
		BurningState burningState = new BurningState();
		AdjunctionState_Map.put(burningState.getId(), burningState);
		DizzinessState dizzinessState = new DizzinessState();
		AdjunctionState_Map.put(dizzinessState.getId(), dizzinessState);
		DumbingState dumbingState = new DumbingState();
		AdjunctionState_Map.put(dumbingState.getId(), dumbingState);
		InvincibleState invincibleState = new InvincibleState();
		AdjunctionState_Map.put(invincibleState.getId(), invincibleState);
		MagicImmunityState magicImmunityState = new MagicImmunityState();
		AdjunctionState_Map.put(magicImmunityState.getId(), magicImmunityState);
		ParalysisState paralysisState = new ParalysisState();
		AdjunctionState_Map.put(paralysisState.getId(), paralysisState);
		PhysicalImmunityState physicalImmunityState = new PhysicalImmunityState();
		AdjunctionState_Map.put(physicalImmunityState.getId(), physicalImmunityState);
		PoisoningState poisoningState = new PoisoningState();
		AdjunctionState_Map.put(poisoningState.getId(), poisoningState);
		SlowDownState slowDownState = new SlowDownState();
		AdjunctionState_Map.put(slowDownState.getId(), slowDownState);
		StealthState stealthState = new StealthState();
		AdjunctionState_Map.put(stealthState.getId(), stealthState);
		MagicShieldState magicState = new MagicShieldState();
		AdjunctionState_Map.put(magicState.getId(), magicState);
	}

	private FSMStateFactory() {

	}

	public static GlobalState getGlobalState(final short id) {
		return GlobalState_Map.get(id);
	}

	public static MovementState getMovementState(final short id) {
		return MovementState_Map.get(id);
	}

	public static PostureState getPostureState(final short id) {
		return PostureState_Map.get(id);
	}

	public static ActionState getActionState(final short id) {
		return ActionState_Map.get(id);
	}

	public static AdjunctionState getAdjunctionState(final short id) {
		return AdjunctionState_Map.get(id);
	}
}
