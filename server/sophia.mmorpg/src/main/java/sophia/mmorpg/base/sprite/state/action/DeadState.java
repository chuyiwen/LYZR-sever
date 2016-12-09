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
package sophia.mmorpg.base.sprite.state.action;

import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.state.ActionState;
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
import sophia.mmorpg.base.sprite.state.movement.ChaseState;
import sophia.mmorpg.base.sprite.state.movement.MoveState;
import sophia.mmorpg.base.sprite.state.movement.PatrolState;
import sophia.mmorpg.base.sprite.state.movement.ReturnToBirthState;

public final class DeadState extends ActionState {
	public static final short DeadState_Id = 307;
	
	public DeadState() {
		super(DeadState_Id);
		addTransition(IdleState.IdleState_Id);
		addCrossLayerBlockedState(PluckingState.PluckingState_Id);
		addCrossLayerBlockedState(BleedState.BleedState_Id);
		addCrossLayerBlockedState(BurningState.BurningState_Id);
		addCrossLayerBlockedState(DizzinessState.DizzinessState_Id);
		addCrossLayerBlockedState(DumbingState.DumbingState_Id);
		addCrossLayerBlockedState(InvincibleState.InvincibleState_Id);
		addCrossLayerBlockedState(MagicImmunityState.MagicImmunityState_Id);
		addCrossLayerBlockedState(MagicShieldState.MagicShieldState_Id);
		addCrossLayerBlockedState(ParalysisState.ParalysisState_Id);
		addCrossLayerBlockedState(PhysicalImmunityState.PhysicalImmunityState_Id);
		addCrossLayerBlockedState(PoisoningState.PoisoningState_Id);
		addCrossLayerBlockedState(SlowDownState.SlowDownState_Id);
		addCrossLayerBlockedState(StealthState.StealthState_Id);
		addCrossLayerBlockedState(FightState.FightState_Id);
		addCrossLayerBlockedState(MoveState.MoveState_Id, ChaseState.ChaseState_Id, PatrolState.PatrolState_Id, ReturnToBirthState.ReturnToBirthState_Id);
	}

	@Override
	public void enter(FightSprite owner) {
		// 把死亡事件告诉对自己死亡感兴趣的角色
//		FightSpriteDead_GE ge = new FightSpriteDead_GE();
//		ge.setFightSprite(owner);
//		GameEvent<?> event = GameEvent.getInstance(SpritePerceiveComponent.FightSpriteDead_GE_Id, ge);
//		owner.getPerceiveComponent().handleGameEvent(event);
//		GameEvent.pool(event);
	}

	@Override
	public void exit(FightSprite owner) {
		// TODO Auto-generated method stub
		
	}
	

}
