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
package newbee.morningGlory.checker.refObjectChecker.GiftBag;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.itemBag.gift.MGGiftBagConfig;
import newbee.morningGlory.mmorpg.player.itemBag.gift.MGGiftRef;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;

public class GiftBagChecker extends BaseRefChecker<MGGiftBagConfig> {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGGiftBagConfig config = (MGGiftBagConfig) gameRefObject;
		Map<String, List<MGGiftRef>> map = config.getGiftConfigMap();
		for(Entry<String,List<MGGiftRef>> entry : map.entrySet()){
			for(MGGiftRef ref : entry.getValue()){
				String giftRefId = ref.getKey();
				GameRefObject gift = GameRoot.getGameRefObjectManager().getManagedObject(giftRefId);
				if(gift == null){
					error(gameRefObject, "礼包checker 道具表中不存在 此礼包 refId :" + giftRefId);
				}
				String itemRefId =  ref.getItemRefId();
				GameRefObject item = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
				if(item == null){
					error(gameRefObject, "礼包checker 道具表中不存在 此道具 refId :" + itemRefId);
				}
				int number = ref.getNumber();
				if(number < 0){
					error(gameRefObject, "礼包checker 礼包道具的数量 < 0");
				}
			}
			
		}
		
	}

}
