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
import sophia.mmorpg.base.sprite.state.movement.ChaseState;
import sophia.mmorpg.base.sprite.state.movement.MoveState;

public class PluckingState extends ActionState {
	public static final short PluckingState_Id = 306;
	
	public PluckingState() {
		super(PluckingState_Id);
		addTransition(DeadState.DeadState_Id, IdleState.IdleState_Id);
		addCrossLayerReplacedState(MoveState.MoveState_Id, ChaseState.ChaseState_Id);
		addCrossLayerReplacedState(DeadState.DeadState_Id);
	}
	
	@Override
	public void enter(FightSprite owner) {
	}

	@Override
	public void exit(FightSprite owner) {
	}

}
