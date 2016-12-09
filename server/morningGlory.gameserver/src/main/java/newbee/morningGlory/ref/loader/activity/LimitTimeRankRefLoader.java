package newbee.morningGlory.ref.loader.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeActivityMgr;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.LimitTimeRankRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LimitTimeRankRefLoader extends AbstractGameRefObjectLoader<LimitTimeRankRef> {
	private static Logger logger = Logger.getLogger(LimitTimeRankRefLoader.class);

	@Override
	protected LimitTimeRankRef create() {
		return new LimitTimeRankRef();
	}

	public LimitTimeRankRefLoader() {
		super(RefKey.limitTimeRank);
	}

	@Override
	protected void fillNonPropertyDictionary(LimitTimeRankRef ref, JsonObject refData) {
		String refId = ref.getId();
		
		if (logger.isDebugEnabled()) {
			logger.debug("load limitTimeRank info! refId = " + refId);
		}

		JsonObject rankReward = refData.get("rankReward").getAsJsonObject();
		String rankInterval = refData.get("rankInterval").getAsString();
		byte rankType = refData.get("rankType").getAsByte();
		
		if (rankReward == null) {
			logger.error("error data table! has no rankReward...");
			return;
		}

		// 存放rankReward
		if (rankReward.has("rankReward") && rankReward.get("rankReward") != null) {
			JsonObject rewardField = rankReward.get("rankReward").getAsJsonObject();
			// 不区分职业的奖励
			List<ItemPair> itemPairs = new ArrayList<ItemPair>();
			// 区分职业的奖励 <professionId, List<ItemPair>>
			Map<Byte, List<ItemPair>> proffesionRewardMap = new HashMap<Byte, List<ItemPair>>();

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

					if (propertyReward.has("unbindedGold")) {
						int unbindgoldNum = propertyReward.get("unbindedGold").getAsInt();
						ItemPair itempair = new ItemPair("unbindedGold", unbindgoldNum, false);
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
								byte bindStatus = elem.get("bindStatus").getAsByte();
								ItemPair itemPair = new ItemPair(itemRefId, itemCount, bindStatus);
								list.add(itemPair);
							}
							proffesionRewardMap.put(Byte.parseByte(proffessionRefId), list);
						}

					}
				}
			}
			
			ref.setRankType(rankType);
			ref.setRankInterval(rankInterval);
			ref.professionRewardMap(proffesionRewardMap);
			ref.setItemPairs(itemPairs);

			LimitTimeActivityMgr.getLimitTimeDataMaps().put(refId, rankType);
		}
		super.fillNonPropertyDictionary(ref, refData);
	}
}
