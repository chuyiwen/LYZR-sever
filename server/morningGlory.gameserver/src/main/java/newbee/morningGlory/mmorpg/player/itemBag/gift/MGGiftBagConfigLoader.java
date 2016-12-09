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
package newbee.morningGlory.mmorpg.player.itemBag.gift;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGGiftBagConfigLoader extends AbstractGameRefObjectLoader<MGGiftBagConfig> {

	@Override
	protected MGGiftBagConfig create() {
		return new MGGiftBagConfig();
	}

	public MGGiftBagConfigLoader() {
		super(RefKey.gift);
	}

	@Override
	protected void fillNonPropertyDictionary(MGGiftBagConfig ref, JsonObject refData) {

		JsonObject giftConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();
		Map<String, List<MGGiftRef>> giftConfigMap = ref.getGiftConfigMap();
		List<MGGiftRef> list = null;
		if (giftConfigData != null) {
				for (Entry<String,JsonElement> entry : giftConfigData.entrySet()) {					
					JsonElement jsonElement = entry.getValue();
					String key = jsonElement.getAsJsonObject().get("giftRefId").getAsString();
					String itemRefId = jsonElement.getAsJsonObject().get("itemRefId").getAsString();
					int number = jsonElement.getAsJsonObject().get("number").getAsInt();
					byte bindStatus = jsonElement.getAsJsonObject().get("bindStatus").getAsByte();
					int probability = jsonElement.getAsJsonObject().get("probability").getAsInt();
					MGGiftRef giftRef = new MGGiftRef(key, itemRefId, number,bindStatus, probability);
					if (giftConfigMap.containsKey(key)) {
						giftConfigMap.get(key).add(giftRef);
					} else {
						list = new ArrayList<MGGiftRef>();
						list.add(giftRef);
						giftConfigMap.put(key, list);
					}
				}
			}
		ref.setId(MGGiftBagConfig.Gift_Data_Id);
	}

}
