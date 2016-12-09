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

import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsDataConfig;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsRewardRef;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsTypeRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGDigsRefLoader extends
		AbstractGameRefObjectLoader<MGDigsDataConfig> {
	
	public MGDigsRefLoader() {
		super(RefKey.digs);
	}

	
	@Override
	protected MGDigsDataConfig create() {
		return new MGDigsDataConfig();
	}

	@Override
	protected void fillNonPropertyDictionary(MGDigsDataConfig ref, JsonObject refData) {
		
		String refId = refData.getAsJsonObject().get("refId").getAsString();
		if ("digstype".equals(refId)) {
			JsonObject digTypeConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();

			Map<String,MGDigsTypeRef> digsTypeMaps = new HashMap<String,MGDigsTypeRef>();
			if (digTypeConfigData != null) {
				for (Entry<String,JsonElement> entry : digTypeConfigData.entrySet()) {
					
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();

					String digsTypeRefId = jsonElement.getAsJsonObject().get("refId").getAsString();
					
					MGDigsTypeRef digsTypeRef = new MGDigsTypeRef();
					fillPropertyDictionary(digsTypeRef.getProperty(), property);
					digsTypeRef.setId(digsTypeRefId);
					digsTypeMaps.put(digsTypeRefId, digsTypeRef);

				}
			}
			ref.setId(MGDigsDataConfig.DigsType_Id);
			ref.setDigsTypeMaps(digsTypeMaps);
		} else if ("digsreward".equals(refId)) {
			JsonObject rewardConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();
			Map<String,MGDigsRewardRef> digsRewardMaps = new HashMap<String,MGDigsRewardRef>();
			if (rewardConfigData != null) {
				for (Entry<String,JsonElement> entry : rewardConfigData.entrySet()) {
				
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();
					String rewardRefId = jsonElement.getAsJsonObject().get("refId").getAsString();
									
					MGDigsRewardRef rewardRef = new MGDigsRewardRef();
					fillPropertyDictionary(rewardRef.getProperty(), property);
					rewardRef.setId(rewardRefId);
					digsRewardMaps.put(rewardRefId, rewardRef);

				}
			}
			ref.setId(MGDigsDataConfig.DigsReward_Id);
			ref.setDigsRewardMaps(digsRewardMaps);
		} 
	}
}
