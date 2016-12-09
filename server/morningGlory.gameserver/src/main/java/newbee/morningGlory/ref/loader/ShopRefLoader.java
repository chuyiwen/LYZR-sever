package newbee.morningGlory.ref.loader;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.mmorpg.store.ItemPrice;
import newbee.morningGlory.mmorpg.store.ref.ShopItemRef;
import newbee.morningGlory.mmorpg.store.ref.StoreItemRefMgr;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ShopRefLoader extends AbstractGameRefObjectLoader<ShopItemRef>{

	
	public ShopRefLoader() {
		super(RefKey.shop);
	}
	
	@Override
	protected ShopItemRef create() {
		return new ShopItemRef();
	}
	 
	@Override
	protected void fillNonPropertyDictionary(ShopItemRef ref, JsonObject refData) {
		String refId = refData.get("refId").getAsString();
		JsonObject property = refData.get("property").getAsJsonObject();
		int limit = property.get("itemLimitNum").getAsInt();
		int limitType = property.get("itemLimitType").getAsInt();
		if (limitType == 2 && limit > 0){
			StoreItemRefMgr.AllLimit.put(refId, (short) limit);
		}
		int refreshType = property.get("number").getAsInt();
		if (!StoreItemRefMgr.refreshTypeList.contains(refreshType)) {
			StoreItemRefMgr.refreshTypeList.add(refreshType);
		}
		if (refData.has("priceData") && (!refData.get("priceData").isJsonNull())){
			JsonObject priceData = refData.get("priceData").getAsJsonObject();
			if(priceData.has("priceData")){
				JsonElement price = priceData.get("priceData");
				if(price.isJsonArray()){
					Iterator<JsonElement> orderFieldData = price.getAsJsonArray().iterator();
					ItemPrice shopPrice = new ItemPrice();
					while(orderFieldData.hasNext()){
						JsonObject elment = orderFieldData.next().getAsJsonObject();
						String priceRefid = elment.get("refId").getAsString();
						int priceNum = elment.get("number").getAsInt();
						if (priceRefid.startsWith("item_")) {
							shopPrice.addExperPrice(priceRefid, priceNum);
						} else {
							addOrigenPrice(priceRefid, priceNum, shopPrice);
						}
					}
					ref.setShopPrice(shopPrice);
				}
			}
		}
		StoreItemRefMgr.ShopItemList.put(refId, ref);
		super.fillNonPropertyDictionary(ref, refData);
	}

	private void addOrigenPrice(String priceRefid, int priceNum, ItemPrice shopPrice) {
		List<Integer> origenPrice = shopPrice.getOrigenPrice();
		if (StringUtils.equals(priceRefid, "gold")) {
			origenPrice.set(0, priceNum);
		}
		if (StringUtils.equals(priceRefid, "unbindedGold")) {
			origenPrice.set(1, priceNum);
		}
		if (StringUtils.equals(priceRefid, "bindedGold")) {
			origenPrice.set(2, priceNum);
		}
		shopPrice.setOrigenPrice(origenPrice);
	}
}
