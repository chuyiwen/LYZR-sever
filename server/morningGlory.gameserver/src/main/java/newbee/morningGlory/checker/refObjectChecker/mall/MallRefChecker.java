package newbee.morningGlory.checker.refObjectChecker.mall;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.store.ItemPrice;
import newbee.morningGlory.mmorpg.store.ref.MallItemRef;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MallRefChecker extends BaseRefChecker<MallItemRef> {

	@Override
	public String getDescription() {
		return "商城道具";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MallItemRef info = (MallItemRef) gameRefObject;
		if (!info.getId().startsWith("mall_item_")) {
			error(gameRefObject, "商城商品<refId>错误 , 请以mall_item_开头!!! 错误的refId为: " + info.getId());
		}

		ItemPrice nowItemPrice = info.getNowItemPrice();
		checkItemPrice(gameRefObject, nowItemPrice);
		if (info.getOldItemPrice() != null) {
			ItemPrice oldItemPrice = info.getOldItemPrice();
			checkItemPrice(gameRefObject, oldItemPrice);
		}
		
		
		PropertyDictionary pd = info.getProperty();
		int limiteType = MGPropertyAccesser.getItemLimitType(pd);
		if (limiteType < 0 || limiteType > 2) {
			error(gameRefObject, "商城商品<itemLimitType>错误 !!! 错误的itemLimitType为: " + limiteType);
		}

		String storeType = MGPropertyAccesser.getStoreType(pd);
		if (!storeType.startsWith("mall_")) {
			error(gameRefObject, "商城商品<storeType>错误 , 请以mall_开头!!! 错误的storeType为: " + storeType);
		}

		String itemRefId = MGPropertyAccesser.getItemId(pd);
		if (GameRoot.getGameRefObjectManager().getManagedObject(itemRefId) == null) {
			error(gameRefObject, "商城商品RefID不存在：" + itemRefId);
		}
	}
	
	public void checkItemPrice(GameRefObject gameRefObject, ItemPrice itemPrice) {
		Map<String, Integer> experPrice = itemPrice.getExperPrice();
		for (Entry<String, Integer> entry : experPrice.entrySet()) {
			if (!entry.getKey().startsWith("item_")) {
				error(gameRefObject, "商城商品<priceData>错误 ,道具ItemRef错误,请以item_开头!!! 错误的道具ItemRef为: " + entry.getKey());
			}
			if (entry.getValue() < 0) {
				error(gameRefObject, "商城商品<priceData>错误 ,道具购买数量错误!!! 错误的道具数量为: " + entry.getValue());
			}
		}
		itemPrice.getOrigenPrice();
	}

}
