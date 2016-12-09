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
import sophia.mmorpg.gameInstance.GameInstanceSceneRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * 副本层-完成条件检查
 */
public final class MGGameInstanceSceneFinishCondtion implements LinYueShengModeCondition<Player> {
	private GameInstanceSceneRef gameInstanceSceneRef;

	public MGGameInstanceSceneFinishCondtion() {

	}

	public MGGameInstanceSceneFinishCondtion(GameInstanceSceneRef gameInstanceSceneRef) {
		this.gameInstanceSceneRef = gameInstanceSceneRef;
	}

	@Override
	public RuntimeResult eligible(Player player) {
		// 1.杀死指定怪物对象集合
		RuntimeResult result = MGGameInstanceConditionFacade.checkKillMonster(player, gameInstanceSceneRef);
		if (result.isError()) {
			return result;
		}

		return RuntimeResult.OK();
	}

	public GameInstanceSceneRef getGameInstanceSceneRef() {
		return gameInstanceSceneRef;
	}

	public void setGameInstanceRef(GameInstanceSceneRef gameInstanceSceneRef) {
		this.gameInstanceSceneRef = gameInstanceSceneRef;
	}
}
