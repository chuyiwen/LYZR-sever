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

import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.adjunction.DizzinessState;
import sophia.mmorpg.base.sprite.state.adjunction.ParalysisState;
import sophia.mmorpg.player.Player;

public final class FSMStateHelper {

	public static boolean canOperation(Player player) {
		FightSpriteStateMgr fightSpriteStateMgr = player.getFightSpriteStateMgr();
		if (fightSpriteStateMgr.isState(DeadState.DeadState_Id)) {
			return false;
		}

		if (fightSpriteStateMgr.isState(DizzinessState.DizzinessState_Id)) {
			return false;
		}

		if (fightSpriteStateMgr.isState(ParalysisState.ParalysisState_Id)) {
			return false;
		}

		return true;
	}
}
