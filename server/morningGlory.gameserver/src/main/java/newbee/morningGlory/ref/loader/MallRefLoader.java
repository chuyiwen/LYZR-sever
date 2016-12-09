package newbee.morningGlory.ref.loader;

import java.util.Iterator;
import java.util.List;

import newbee.morningGlory.mmorpg.store.ItemPrice;
import newbee.morningGlory.mmorpg.store.ref.MallItemRef;
import newbee.morningGlory.mmorpg.store.ref.StoreItemRefMgr;
import newbee.morningGlory.ref.RefKey;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MallRefLoader extends AbstractGameRefObjectLoader<MallItemRef> {

	public MallRefLoader() {
		super(RefKey.mall);
	}

	@Override
	protected MallItemRef create() {
		return new MallItemRef();
	}

	@Override
	protected void fillNonPropertyDictionary(MallItemRef ref, JsonObject refData) {
		String refId = refData.get("refId").getAsString();
		ref.setId(refId);
		JsonObject property = refData.get("property").getAsJsonObject();
		int limit = property.get("itemLimitNum").getAsInt();
		int limitType = property.get("itemLimitType").getAsInt();
		if (limitType == 2 && limit > 0) {
			StoreItemRefMgr.AllLimit.put(refId, (short) limit);
		}
		int refreshType = property.get("number").getAsInt();
		if (!StoreItemRefMgr.refreshTypeList.contains(refreshType)) {
			StoreItemRefMgr.refreshTypeList.add(refreshType);
		}
		if (refData.has("priceData") && (!refData.get("priceData").isJsonNull())) {
			JsonObject priceData = refData.get("priceData").getAsJsonObject();
			if (priceData.has("nowPriceData")) {
				// 真是价格
				JsonElement price = priceData.get("nowPriceData");
				setItemPrice(price, ref, 1);
				// 原价
				if (priceData.has("formerPriceData")) {
					JsonElement formPrice = priceData.get("formerPriceData");
					setItemPrice(formPrice, ref, 2);
				}
			}
		}
		StoreItemRefMgr.MallItemList.put(refId, ref);
		super.fillNonPropertyDictionary(ref, refData);
	}

	public void setItemPrice(JsonElement price, MallItemRef ref, int type) {
		if (price.isJsonArray()) {
			Iterator<JsonElement> orderFieldData = price.getAsJsonArray().iterator();
			ItemPrice shopPrice = new ItemPrice();
			while (orderFieldData.hasNext()) {
				JsonObject elment = orderFieldData.next().getAsJsonObject();
				String priceRefid = elment.get("refId").getAsString();
				int priceNum = elment.get("number").getAsInt();
				if (priceRefid.startsWith("item_")) {
					shopPrice.addExperPrice(priceRefid, priceNum);
				} else {
					addOrigenPrice(priceRefid, priceNum, shopPrice);
				}
			}
			if (type == 1) {
				ref.setNowItemPrice(shopPrice);
			} else if (type == 2) {
				ref.setOldItemPrice(shopPrice);
			}
		}
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