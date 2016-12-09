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
package sophia.mmorpg.base.sprite.state.movement;

import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.state.MovementState;
import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;

public class MoveState extends MovementState {
	public static final short MoveState_Id = 102;

	public MoveState() {
		super(MoveState_Id);
		addTransition(StopState.StopState_Id);
		addCrossLayerReplacedState(DeadState.DeadState_Id);
	}

	@Override
	public void enter(FightSprite owner) {
		if (owner.getFightSpriteStateMgr().isState(StealthState.StealthState_Id)) {
			owner.cancelState(StealthState.StealthState_Id);
		}
		if (owner.getFightSpriteStateMgr().isState(PluckingState.PluckingState_Id)) {
			owner.cancelState(PluckingState.PluckingState_Id);
		}
	}

	@Override
	public void exit(FightSprite owner) {

	}

}
