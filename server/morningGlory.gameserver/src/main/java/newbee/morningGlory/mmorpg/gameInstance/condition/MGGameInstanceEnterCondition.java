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
 * 副本-进入条件检查 ! 进入副本次数，检查 ! 副本是否在开放时间内 ! 是否达到等级 ! 某类型任务的任务是否领取
 */
public final class MGGameInstanceEnterCondition implements LinYueShengModeCondition<Player> {
	private GameInstanceRef gameInstanceRef;

	public MGGameInstanceEnterCondition() {

	}

	public MGGameInstanceEnterCondition(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}

	@Override
	public RuntimeResult eligible(Player player) {
		// 1.检查玩家进入副本次数
		RuntimeResult result = MGGameInstanceConditionFacade.checkCount(player, gameInstanceRef);
		if (result.isError()) {
			return result;
		}

		// 2.检查副本是否开放
		result = MGGameInstanceConditionFacade.checkOpenTime(player, gameInstanceRef);
		if (result.isError()) {
			return result;
		}

		// 3.是否达到等级
		result = MGGameInstanceConditionFacade.checkPlayerLevel(player, gameInstanceRef);
		if (result.isError()) {
			return result;
		}

		// 4.玩家当前的主线任务
		result = MGGameInstanceConditionFacade.checkQuest(player, gameInstanceRef);
		if (result.isError()) {
			return result;
		}

		return RuntimeResult.OK();
	}

	public GameInstanceRef getGameInstanceRef() {
		return gameInstanceRef;
	}

	public void setGameInstanceRef(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}
}
