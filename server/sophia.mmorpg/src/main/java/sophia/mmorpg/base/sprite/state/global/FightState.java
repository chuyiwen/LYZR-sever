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
package sophia.mmorpg.base.sprite.state.global;

import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.state.GlobalState;
import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;
import sophia.mmorpg.base.sprite.state.posture.MountedState;

public final class FightState extends GlobalState {
	public static final short FightState_Id = 1;
	
	public FightState() {
		super(FightState_Id);
		addCrossLayerReplacedState(DeadState.DeadState_Id);
		addCrossLayerBlockedState(MountedState.MountedState_Id);
	}

	@Override
	public void enter(FightSprite owner) {
		if (owner.getFightSpriteStateMgr().isState(StealthState.StealthState_Id)) {
			owner.cancelState(StealthState.StealthState_Id);
		}
	}

	@Override
	public void exit(FightSprite owner) {
		// TODO Auto-generated method stub
		
	}

}
