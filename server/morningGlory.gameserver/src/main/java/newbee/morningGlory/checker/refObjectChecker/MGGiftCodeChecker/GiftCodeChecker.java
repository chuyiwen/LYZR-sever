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
package newbee.morningGlory.checker.refObjectChecker.MGGiftCodeChecker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeDataTypeRef;
import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeType;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.UnPropsItemRef;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class GiftCodeChecker extends BaseRefChecker<MGGiftCodeDataTypeRef> {
	Set<String> checkerSet = new HashSet<String>();
	@Override
	public String getDescription() {
		return "giftCode";
	}
	public GiftCodeChecker() {
		
	}
	@Override
	public void check(GameRefObject gameRefObject) {
		MGGiftCodeDataTypeRef ref = (MGGiftCodeDataTypeRef) gameRefObject;
		PropertyDictionary pd = ref.getProperty();
		byte codeType = MGPropertyAccesser.getItemType(pd);
		if(!(codeType == 1 || codeType ==2)){
			error(gameRefObject, "礼包码类型错误，只能为1 和 2:" + codeType);
		}
		
		if (codeType == MGGiftCodeType.Double) { // 如果是多人公用
			String doubleKeyCode = ref.getDoubleGiftKeyCode();
			if (checkerSet.contains(doubleKeyCode)) {
				error(gameRefObject, "多人公用礼包码存在重复:" + doubleKeyCode);
			}
			checkerSet.add(doubleKeyCode);
		} else if (codeType == MGGiftCodeType.Single) { // 如果是单人专用
			Set<String> singleSet = ref.getGiftCodeSingleSet();
			if (singleSet != null) {
				for (String singleCode : singleSet) {
				
					if (checkerSet.contains(singleCode)) {
						error(gameRefObject, "单人专用礼包码存在重复:" + singleCode);
					}
					checkerSet.add(singleCode);
				}
			}
		}
		
		List<ItemPair> rewards = ref.getRewards();
		for (ItemPair itemPair : rewards) {
			String itemRefId = itemPair.getItemRefId();
			GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if (itemRef == null || (!(itemRef instanceof ItemRef)&&!(itemRef instanceof UnPropsItemRef))) {
				error(gameRefObject, "礼包码系统奖励物品refId 不存在:" + itemRefId);
			}
			int number = itemPair.getNumber();
			if (number <= 0) {
				error(gameRefObject, "礼包码系统奖励物品数量 小于0:" + number);
			}
		}

	}

}
