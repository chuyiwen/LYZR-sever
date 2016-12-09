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
package newbee.morningGlory.mmorpg.gameInstance.condition;

import sophia.mmorpg.core.linyuesheng.LinYueShengModeCondition;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * 副本-完成条件检查(副本暂时无通关条件)
 */
public final class MGGameInstanceFinishCondtion implements LinYueShengModeCondition<Player> {
	private GameInstanceRef gameInstanceRef;

	public MGGameInstanceFinishCondtion() {

	}

	public MGGameInstanceFinishCondtion(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}

	@Override
	public RuntimeResult eligible(Player player) {
		return RuntimeResult.OK();
	}

	public GameInstanceRef getGameInstanceRef() {
		return gameInstanceRef;
	}

	public void setGameInstanceRef(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}
}
