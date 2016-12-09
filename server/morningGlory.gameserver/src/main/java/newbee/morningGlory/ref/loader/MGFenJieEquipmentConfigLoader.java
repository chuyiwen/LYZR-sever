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
package newbee.morningGlory.ref.loader;

import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.ref.RefKey;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieEquipmentConfig;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieItem;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieRef;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieScrollRef;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class MGFenJieEquipmentConfigLoader extends AbstractGameRefObjectLoader<MGFenJieEquipmentConfig> {

	public MGFenJieEquipmentConfigLoader() {
		super(RefKey.equipResolve);
	}

	@Override
	protected MGFenJieEquipmentConfig create() {

		return new MGFenJieEquipmentConfig();
	}

	@Override
	protected void fillNonPropertyDictionary(MGFenJieEquipmentConfig ref, JsonObject refData) {
		String refId = refData.getAsJsonObject().get("refId").getAsString();
		if (refId.equals("equip_resolve_base")) {
			JsonArray fenJieConfigData = refData.getAsJsonObject().get("configData").getAsJsonArray();

			Map<String, MGFenJieRef> fenJieConfigMap = new HashMap<String, MGFenJieRef>();

			if (fenJieConfigData != null) {
				for (JsonElement jsonElement : fenJieConfigData) {
					String key = jsonElement.getAsJsonObject().get("evaluateLevel").getAsString();
					MGFenJieRef fenJieRef = new MGFenJieRef();
					JsonElement itemElementList = jsonElement.getAsJsonObject().get("itemList");
					if( !itemElementList.isJsonNull()){
					JsonArray itemList = jsonElement.getAsJsonObject().get("itemList").getAsJsonArray();
						for (JsonElement itemElement : itemList) {
							String itemRefId = itemElement.getAsJsonObject().get("refId").getAsString();
							int probability = itemElement.getAsJsonObject().get("probability").getAsInt();
							int number = itemElement.getAsJsonObject().get("number").getAsInt();
							MGFenJieItem fenJieItem = new MGFenJieItem(itemRefId, probability, number);
							fenJieRef.getFenJieItem().add(fenJieItem);
						}
					}
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();

					fillPropertyDictionary(fenJieRef.getProperty(), property);

					fenJieConfigMap.put(key, fenJieRef);
				}
			}
			ref.setId(MGFenJieEquipmentConfig.FenJie_Id);
			ref.setFenJieConfigMap(fenJieConfigMap);

		} else if (refId.equals("equip_resolve_scroll")) {
			JsonArray fenJieConfigData = refData.getAsJsonObject().get("configData").getAsJsonArray();
			Map<Integer, MGFenJieScrollRef> fenJieScrollConfigMap = new HashMap<Integer, MGFenJieScrollRef>();
			if (fenJieConfigData != null) {

				for (JsonElement jsonElement : fenJieConfigData) {

					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();
					int strengtheningLevel = property.get("strengtheningLevel").getAsInt();
					MGFenJieScrollRef scrollRef = new MGFenJieScrollRef();

					fillPropertyDictionary(scrollRef.getProperty(), property);

					fenJieScrollConfigMap.put(strengtheningLevel, scrollRef);
				}

			}
			ref.setId(MGFenJieEquipmentConfig.FenJieScroll_Id);
			ref.setFenJieScrollConfigMap(fenJieScrollConfigMap);
		}
		super.fillNonPropertyDictionary(ref, refData);
	}
}
