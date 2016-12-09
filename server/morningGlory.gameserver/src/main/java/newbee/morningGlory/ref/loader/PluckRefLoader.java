package newbee.morningGlory.ref.loader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.pluck.PluckRef;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PluckRefLoader extends AbstractGameRefObjectLoader<PluckRef> {
	private static Logger logger = Logger.getLogger(PluckRefLoader.class);

	public PluckRefLoader() {
		super(RefKey.collect);
	}

	@Override
	protected PluckRef create() {
		// TODO Auto-generated method stub
		return new PluckRef();
	}

	@Override
	protected void fillNonPropertyDictionary(PluckRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load pluck refId:" + ref.getId());
		}
		// <itemRefId, <count, probability>
		Map<String, HashMap<Integer, Float>> itemRewardMapping = new HashMap<String, HashMap<Integer, Float>>();
		Map<String, Byte> itemBindStatusMapping = new HashMap<String, Byte>();
		Map<String, Integer> propertyRewardMapping = new HashMap<String, Integer>();

		if (refData.has("pluck") && refData.get("pluck") != null) {
			JsonObject pluckObject = refData.get("pluck").getAsJsonObject();

			if (pluckObject.has("reward") && pluckObject.get("reward") != null) {
				JsonObject rewardObject = pluckObject.get("reward").getAsJsonObject();

				if (rewardObject.has("itemReward") && rewardObject.get("itemReward") != null) {
					Iterator<JsonElement> itemRewardElement = rewardObject.get("itemReward").getAsJsonArray().iterator();
					while (itemRewardElement.hasNext()) {
						JsonObject object = itemRewardElement.next().getAsJsonObject();
						HashMap<Integer, Float> rewardCountMapping = null;
						String itemRefId = null;
						byte bindStatus = 0;
						if (object.has("itemRefId") && object.get("itemRefId") != null) {
							itemRefId = object.get("itemRefId").getAsString();
						}
						
						if (object.has("bindStatus") && object.get("bindStatus") != null) {
							bindStatus = object.get("bindStatus").getAsByte();
						}

						if (object.has("itemCount") && object.get("itemCount") != null && object.get("itemCount").isJsonArray()) {
							rewardCountMapping = new HashMap<Integer, Float>();
							Iterator<JsonElement> itemCountElement = object.get("itemCount").getAsJsonArray().iterator();
							while (itemCountElement.hasNext()) {
								JsonObject itemCountObject = itemCountElement.next().getAsJsonObject();
								int count = 0;
								float probability = 0f;
								if (itemCountObject.has("count") && itemCountObject.get("count") != null) {
									count = itemCountObject.get("count").getAsInt();
								}

								if (itemCountObject.has("probability") && itemCountObject.get("probability") != null) {
									probability = itemCountObject.get("probability").getAsFloat();
								}

								rewardCountMapping.put(count, probability);
							}
							itemRewardMapping.put(itemRefId, rewardCountMapping);
							itemBindStatusMapping.put(itemRefId, bindStatus);
						} else {
							rewardCountMapping = new HashMap<Integer, Float>();
							int count = object.get("itemCount").getAsInt();
							float probability = 1.0f;
							rewardCountMapping.put(count, probability);
							itemRewardMapping.put(itemRefId, rewardCountMapping);
							itemBindStatusMapping.put(itemRefId, bindStatus);
						}

					}
				} 
				
				if (rewardObject.has("propertyReward") && rewardObject.get("propertyReward") != null) {
					JsonObject propertyRewardObject = rewardObject.get("propertyReward").getAsJsonObject();
					if (propertyRewardObject.has("gold") && propertyRewardObject.get("gold") != null) {
						int goldNum = propertyRewardObject.get("gold").getAsInt();
						propertyRewardMapping.put("gold", goldNum);
					}

					if (propertyRewardObject.has("exp") && propertyRewardObject.get("exp") != null) {
						int expNum = propertyRewardObject.get("exp").getAsInt();
						propertyRewardMapping.put("exp", expNum);
					}

				}
			}
		}

		ref.setItemRewardMapping(itemRewardMapping);
		ref.setPropertyRewardMapping(propertyRewardMapping);
		ref.setItemBindStatusMapping(itemBindStatusMapping);
	}

}
