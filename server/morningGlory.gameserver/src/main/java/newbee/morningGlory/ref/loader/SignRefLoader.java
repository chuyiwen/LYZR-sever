package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.activity.constant.ActivityData;
import newbee.morningGlory.mmorpg.player.activity.ref.SignRef;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SignRefLoader extends AbstractGameRefObjectLoader<SignRef> {
	private static final Logger logger = Logger.getLogger(SignRefLoader.class);

	@Override
	protected SignRef create() {
		return new SignRef();
	}

	public SignRefLoader() {
		super(RefKey.sign);
	}

	@Override
	protected void fillNonPropertyDictionary(SignRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load sign info");
		}
		if (!ref.getId().equals("sign_1")) {
			ActivityData.getSignRerIdList().add(ref.getId());
		}

		JsonObject signData = refData.get("signData").getAsJsonObject();
		if (signData == null)
			return;
		
		// 存放rewardField
		if (signData.has("rewardField") && signData.get("rewardField") != null) {
			JsonObject rewardField = signData.get("rewardField").getAsJsonObject();
			List<ItemPair> itemPairs = new ArrayList<ItemPair>();
			Map<Byte, List<ItemPair>> map = new HashMap<Byte, List<ItemPair>>();

			if (rewardField.isJsonObject()) {
				// 属性奖励部分
				if (rewardField.has("propertyReward") && (!rewardField.get("propertyReward").isJsonNull())) {
					JsonObject propertyReward = rewardField.get("propertyReward").getAsJsonObject();
					if (propertyReward.has("exp")) {
						int expNum = propertyReward.get("exp").getAsInt();
						ItemPair itempair = new ItemPair("exp", expNum, false);
						itemPairs.add(itempair);
					}

					if (propertyReward.has("gold")) {
						int goldNum = propertyReward.get("gold").getAsInt();
						ItemPair itempair = new ItemPair("gold", goldNum, false);
						itemPairs.add(itempair);
					}

					if (propertyReward.has("unbindgold")) {
						int unbindgoldNum = propertyReward.get("unbindgold").getAsInt();
						ItemPair itempair = new ItemPair("unbindgold", unbindgoldNum, false);
						itemPairs.add(itempair);
					}

					if (propertyReward.has("bindedGold")) {
						int bindgoldNum = propertyReward.get("bindedGold").getAsInt();
						ItemPair itempair = new ItemPair("bindedGold", bindgoldNum, false);
						itemPairs.add(itempair);
					}
				}
				if (rewardField.has("itemReward") && (!rewardField.get("itemReward").isJsonNull())) {
					JsonObject itemReward = rewardField.get("itemReward").getAsJsonObject();
					int relatedType = itemReward.get("relatedType").getAsInt();
					// 不区分职业
					if (relatedType == 0) {
						Iterator<JsonElement> itemList = itemReward.get("itemList").getAsJsonArray().iterator();
						while (itemList.hasNext()) {
							JsonObject elem = itemList.next().getAsJsonObject();
							String itemRefId = elem.get("itemRefId").getAsString();
							int itemCount = elem.get("itemCount").getAsInt();
							byte bindStatus = elem.get("bindStatus").getAsByte();
							ItemPair itempair = new ItemPair(itemRefId, itemCount, bindStatus);
							itemPairs.add(itempair);
						}
					}

					// 区分职业
					else if (relatedType == 1) {
						Iterator<JsonElement> professionList = itemReward.get("professionList").getAsJsonArray().iterator();
						while (professionList.hasNext()) {
							JsonObject elem = professionList.next().getAsJsonObject();
							String proffessionRefId = elem.get("proffessionRefId").getAsString();
							Iterator<JsonElement> itemList = elem.get("itemList").getAsJsonArray().iterator();
							List<ItemPair> list = new ArrayList<ItemPair>();
							while (itemList.hasNext()) {
								JsonObject temp = itemList.next().getAsJsonObject();
								String itemRefId = temp.get("itemRefId").getAsString();
								int itemCount = temp.get("itemCount").getAsInt();
								byte bindStatus = temp.get("bindStatus").getAsByte();
								ItemPair itemPair = new ItemPair(itemRefId, itemCount, bindStatus);
								list.add(itemPair);
							}
							map.put(Byte.parseByte(proffessionRefId), list);
						}

					}
				}
			}
			ref.setMap(map);
			ref.setItemPairs(itemPairs);
		}
		super.fillNonPropertyDictionary(ref, refData);
	}
}
