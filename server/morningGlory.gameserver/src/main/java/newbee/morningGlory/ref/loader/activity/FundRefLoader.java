package newbee.morningGlory.ref.loader.activity;

import java.util.Map;

import newbee.morningGlory.mmorpg.player.activity.fund.FundMgr;
import newbee.morningGlory.mmorpg.player.activity.fund.FundType;
import newbee.morningGlory.mmorpg.player.activity.fund.ref.FundRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FundRefLoader extends AbstractGameRefObjectLoader<FundRef> {
	private static Logger logger = Logger.getLogger(FundRefLoader.class);

	@Override
	protected FundRef create() {
		// TODO Auto-generated method stub
		return new FundRef();
	}

	public FundRefLoader() {
		super(RefKey.fund);
	}

	@Override
	protected void fillNonPropertyDictionary(FundRef ref, JsonObject refData) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("load 基金活动 info...:" + ref.getId());
		}
		JsonObject giftDataArray = refData.get("giftData").getAsJsonObject().get("giftData").getAsJsonObject();
		int size = giftDataArray.entrySet().size();
		ItemPair giftArrays[] = new ItemPair[size];
		ref.setGiftArrays(giftArrays);

		for (Map.Entry<String, JsonElement> entry : giftDataArray.entrySet()) {
			int day = Integer.parseInt(entry.getKey().substring("day".length()));
			JsonObject obj = entry.getValue().getAsJsonObject();
			String refId = obj.get("refId").getAsString();
			int number = obj.get("number").getAsInt();
			byte bindStatus = obj.get("bindStatus").getAsByte();
			giftArrays[day - 1] = new ItemPair(refId, number, bindStatus);
		}
		ref.setBuyPrice(refData.get("buyPrice").getAsJsonObject().get("buyPrice").getAsInt());
		ref.setMoneyType(refData.get("moneyType").getAsJsonObject().get("moneyType").getAsByte());

		int fundType = Integer.parseInt(ref.getId().substring(ref.getId().indexOf("_") + 1));
		FundMgr.getGiftMap().put(FundType.getFundType(fundType), ref);
		super.fillNonPropertyDictionary(ref, refData);
	}
}
