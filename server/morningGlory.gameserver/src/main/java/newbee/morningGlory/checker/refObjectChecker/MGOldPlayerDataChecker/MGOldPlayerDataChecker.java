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
package newbee.morningGlory.checker.refObjectChecker.MGOldPlayerDataChecker;

import java.util.List;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.oldPlayer.MGOldPlayerDataRef;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.itemBag.ItemPair;

public class MGOldPlayerDataChecker extends BaseRefChecker<MGOldPlayerDataRef> {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "老用户";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGOldPlayerDataRef ref = (MGOldPlayerDataRef) gameRefObject;

		List<ItemPair> rewards = ref.getRewards();
		for (ItemPair itemPair : rewards) {
			String itemRefId = itemPair.getItemRefId();
			GameRefObject itemRef =  GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if (itemRef == null) {
				error(gameRefObject, "老用户活动 奖励物品不存在:" + itemRefId);
			}
		}

	}

}
