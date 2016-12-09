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
package newbee.morningGlory.checker.refObjectChecker.MGResDownLoadRefChecker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadDataRef;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.UnPropsItemRef;
import sophia.mmorpg.player.itemBag.ItemPair;

public class MGResDownRefChecker extends BaseRefChecker<MGResDownLoadDataRef> {
	Set<String> checkerSet = new HashSet<String>();
	@Override
	public String getDescription() {
		return "resdown";
	}
	public MGResDownRefChecker() {
		
	}
	@Override
	public void check(GameRefObject gameRefObject) {
		MGResDownLoadDataRef ref = (MGResDownLoadDataRef) gameRefObject;
		
		
		List<ItemPair> rewards = ref.getReward();
		for (ItemPair itemPair : rewards) {
			String itemRefId = itemPair.getItemRefId();
			GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if (itemRef == null || (!(itemRef instanceof ItemRef)&&!(itemRef instanceof UnPropsItemRef))) {
				error(gameRefObject, "下载奖励系统奖励物品refId 不存在:" + itemRefId);
			}
			int number = itemPair.getNumber();
			if (number <= 0) {
				error(gameRefObject, "下载奖励系统奖励物品数量 小于0:" + number);
			}
		}

	}

}
