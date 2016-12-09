package newbee.morningGlory.ref.loader.activity;

import java.util.LinkedList;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.discount.BatchMgr;
import newbee.morningGlory.mmorpg.store.ref.DiscountItemRef;
import newbee.morningGlory.mmorpg.store.ref.DiscountRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.emory.mathcs.backport.java.util.Collections;

public class DiscountRefLoader extends AbstractGameRefObjectLoader<DiscountRef> {

	@Override
	protected DiscountRef create() {
		return new DiscountRef();
	}

	public DiscountRefLoader() {
		super(RefKey.discount);
	}

	@Override
	protected void fillNonPropertyDictionary(DiscountRef ref, JsonObject refData) {
		String refId = refData.getAsJsonObject().get("refId").getAsString();
		LinkedList<Short> batchList = new LinkedList<Short>();
		if (DiscountRef.discount_item.equals(refId)) {
			JsonObject discountItemData = refData.getAsJsonObject().get("configData").getAsJsonObject();

			if (discountItemData != null) {
				for (Entry<String, JsonElement> entry : discountItemData.entrySet()) {
					JsonElement jsonElement = entry.getValue();
					DiscountItemRef discountItemRef = new DiscountItemRef();
					String discountRefId = jsonElement.getAsJsonObject().get("refId").getAsString();
					JsonObject discountData = jsonElement.getAsJsonObject().get("discountData").getAsJsonObject();

					int originalsaleCurrency = discountData.get("OriginalsaleCurrency").getAsInt();
					int originalsalePrice = discountData.get("OriginalsalePrice").getAsInt();
					String itemRefId = discountData.get("itemId").getAsString();
					short itemLimitNum = discountData.get("itemLimitNum").getAsShort();
					short personalLimitNum = discountData.get("privateLimitNum").getAsShort();
					int newSaleCurrency = discountData.get("saleCurrency").getAsInt();
					int newSalePrice = discountData.get("salePrice").getAsInt();
					byte bindStatus = discountData.get("bindStatus").getAsByte();

					discountItemRef.setOriginalsaleCurrency(originalsaleCurrency);
					discountItemRef.setOriginalsalePrice(originalsalePrice);
					discountItemRef.setItemRefId(itemRefId);
					discountItemRef.setItemLimitNum(itemLimitNum);
					discountItemRef.setPersonalLimitNum(personalLimitNum);
					discountItemRef.setNewSaleCurrency(newSaleCurrency);
					discountItemRef.setNewSalePrice(newSalePrice);
					discountItemRef.setId(discountRefId);
					discountItemRef.setBindStatus(bindStatus);

					short length = (short) "discount_batch".length();
					short batch = Short.parseShort(discountRefId.substring(length, length + 1));
					ref.putDiscountItemRefData(batch, discountRefId, discountItemRef);
					
					if(!batchList.contains(batch)) {
						batchList.add(batch);
					}
				}
			}
			ref.setId(DiscountRef.discount_item);
			Collections.sort(batchList);
			BatchMgr.setBatchList(batchList);
		}
	}
}
