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
package newbee.morningGlory.checker.refObjectChecker.item;

import newbee.morningGlory.checker.BaseRefChecker;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class ItemRefChecker extends BaseRefChecker<ItemRef> {
	@Override
	public void check(GameRefObject gameRefObject) {
		ItemRef info = (ItemRef) gameRefObject;
		if (!(info.getId().startsWith("item_") || info.getId().startsWith("equip_"))) {
			error(gameRefObject, "物品道具<refId>错误 , 请以item_ 或 equip_ 开头!!! 错误的refId为: " + info.getId());
		}

		PropertyDictionary pd = info.getProperty();
//		int limiteType = MGPropertyAccesser.getItemType(pd);
//		if (limiteType < 0 || limiteType > 12) {
//			error(gameRefObject, "物品道具<itemType>错误 !!! 错误的refId为: " + info.getId());
//		}

		String itemRefId = info.getId();
		ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
		if (itemRef == null) {
			error(gameRefObject, "物品道具RefID不存在：" + itemRefId);
		}
		byte quality = MGPropertyAccesser.getQuality(pd);
		byte bindType = MGPropertyAccesser.getBindType(pd);
		int itemSrotId = MGPropertyAccesser.getItemSortId(pd);
		if (quality < 1 || quality > 4) {
			error(gameRefObject, "物品道具品质<quality>错误 !!!,值应该在[1,4]之间, 错误的refId为: " + info.getId());
		} else if (bindType < 0 || bindType > 2) {
			error(gameRefObject, "物品道具绑定类型<bindType>错误 !!!,值应该在[0,2]之间, 错误的refId为: " + info.getId());
		} else if (itemSrotId < 10000 || itemSrotId > 29999) {
			error(gameRefObject, "物品道具排序ID<itemSrotId>错误 !!!,值应该在[10000,19999]之间, 错误的refId为: " + info.getId());
		}

	}

	@Override
	public String getDescription() {
		return "物品道具";
	}
}
