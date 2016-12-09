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
package newbee.morningGlory.ref.loader.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryCountDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryRewardDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryVipDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGVipLotteryDataConfig;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGVipLotteryRefLoader extends
		AbstractGameRefObjectLoader<MGVipLotteryDataConfig> {
	
	public MGVipLotteryRefLoader() {
		super(RefKey.viplottery);
	}

	
	@Override
	protected MGVipLotteryDataConfig create() {
		return new MGVipLotteryDataConfig();
	}

	@Override
	protected void fillNonPropertyDictionary(MGVipLotteryDataConfig ref, JsonObject refData) {
		
		String refId = refData.getAsJsonObject().get("refId").getAsString();
		if ("lotterycount".equals(refId)) {
			JsonObject lotteryCountConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();

			Map<String,MGLotteryCountDataRef> lotteryCountMaps = new HashMap<String,MGLotteryCountDataRef>();
			if (lotteryCountConfigData != null) {
				for (Entry<String,JsonElement> entry : lotteryCountConfigData.entrySet()) {
					
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();

					String countRefId = jsonElement.getAsJsonObject().get("refId").getAsString();
					
					MGLotteryCountDataRef lotteryCountRef = new MGLotteryCountDataRef();
					fillPropertyDictionary(lotteryCountRef.getProperty(), property);
					lotteryCountRef.setId(countRefId);
					lotteryCountMaps.put(countRefId, lotteryCountRef);

				}
			}
			ref.setId(MGVipLotteryDataConfig.LotteryCount_Id);
			ref.setLotteryCountMaps(lotteryCountMaps);
		} else if ("lotteryreward".equals(refId)) {
			JsonObject rewardConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();
			Map<String,MGLotteryRewardDataRef> lotteryRewardMaps = new HashMap<String,MGLotteryRewardDataRef>();
			if (rewardConfigData != null) {
				for (Entry<String,JsonElement> entry : rewardConfigData.entrySet()) {
				
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();
					String rewardRefId = jsonElement.getAsJsonObject().get("refId").getAsString();
									
					MGLotteryRewardDataRef rewardRef = new MGLotteryRewardDataRef();
					fillPropertyDictionary(rewardRef.getProperty(), property);
					rewardRef.setId(rewardRefId);
					lotteryRewardMaps.put(rewardRefId, rewardRef);

				}
			}
			ref.setId(MGVipLotteryDataConfig.LotteryReward_Id);
			ref.setLotteryRewardMaps(lotteryRewardMaps);
		} else if ("lotteryvip".equals(refId)) {
			JsonObject vipConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();
			Map<String,MGLotteryVipDataRef> lotteryVipMaps = new HashMap<String,MGLotteryVipDataRef>();
			if (vipConfigData != null) {
				for (Entry<String,JsonElement> entry : vipConfigData.entrySet()) {
				
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();
					String vipRefId = jsonElement.getAsJsonObject().get("refId").getAsString();
									
					MGLotteryVipDataRef rewardRef = new MGLotteryVipDataRef();
					fillPropertyDictionary(rewardRef.getProperty(), property);
					rewardRef.setId(vipRefId);
					lotteryVipMaps.put(vipRefId, rewardRef);

				}
			}
			ref.setId(MGVipLotteryDataConfig.LotteryVip_Id);
			ref.setLotteryVipMaps(lotteryVipMaps);
		}
	}
}
