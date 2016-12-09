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
import java.util.Map.Entry;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaDataRef;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaEquipmentConfig;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaRefKey;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaScrollDataRef;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGSpecialEquipmentQiangHuaDataRef;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGSpecialEquipmentQiangHuaDataRefKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class MGQiangHuaEquipmentConfigLoader extends AbstractGameRefObjectLoader<MGQiangHuaEquipmentConfig> {
	private static final Logger logger = Logger.getLogger(MGQiangHuaEquipmentConfigLoader.class);

	public MGQiangHuaEquipmentConfigLoader() {
		super(RefKey.equipStrengthening);
	}

	@Override
	protected MGQiangHuaEquipmentConfig create() {
		return new MGQiangHuaEquipmentConfig();
	}

	@Override
	protected void fillNonPropertyDictionary(MGQiangHuaEquipmentConfig ref, JsonObject refData) {

		String refId = refData.getAsJsonObject().get("refId").getAsString();
		if ("equip_strengthening_base".equals(refId)) {
			JsonObject qiangHuaConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();

			Map<MGQiangHuaRefKey, MGQiangHuaDataRef> qiangHuaDataRefMap = new HashMap<MGQiangHuaRefKey, MGQiangHuaDataRef>();
			if (qiangHuaConfigData != null) {
				for (Entry<String,JsonElement> entry : qiangHuaConfigData.entrySet()) {
					
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();

					byte playerPrefessionId = property.get("professionId").getAsByte();
					byte equipmentBodyAreaId = property.get("kind").getAsByte();
					byte qiangHuaLevel = property.get("strengtheningLevel").getAsByte();
					MGQiangHuaRefKey qiangHuaKey = MGQiangHuaRefKey.get(playerPrefessionId, equipmentBodyAreaId, qiangHuaLevel);

					MGQiangHuaDataRef qiangHuaRef = new MGQiangHuaDataRef();
					fillPropertyDictionary(qiangHuaRef.getProperty(), property);

					qiangHuaDataRefMap.put(qiangHuaKey, qiangHuaRef);

				}
			}
			ref.setId(MGQiangHuaEquipmentConfig.QiangHua_Id);
			ref.setQiangHuaDataRefMap(qiangHuaDataRefMap);
		} else if ("equip_strengthening_expand".equals(refId)) {
			JsonObject expandConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();

			Map<MGSpecialEquipmentQiangHuaDataRefKey, MGSpecialEquipmentQiangHuaDataRef> specialEquipmentQiangHuaDataRefMap = new HashMap<MGSpecialEquipmentQiangHuaDataRefKey, MGSpecialEquipmentQiangHuaDataRef>();
			if (expandConfigData != null) {
				for (Entry<String,JsonElement> entry : expandConfigData.entrySet()) {
				
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();
					String itemRefId = property.get("itemRefId").getAsString();
					String id = jsonElement.getAsJsonObject().get("id").getAsString();
					int strengtheningLevel = property.get("strengtheningLevel").getAsInt();
					MGSpecialEquipmentQiangHuaDataRefKey specialEquipmentQiangHuaDataRefKey = MGSpecialEquipmentQiangHuaDataRefKey.get(itemRefId, strengtheningLevel);
					MGSpecialEquipmentQiangHuaDataRef specialEquipmentQiangHuaDataRef = new MGSpecialEquipmentQiangHuaDataRef();
					specialEquipmentQiangHuaDataRef.setId(id);
					fillPropertyDictionary(specialEquipmentQiangHuaDataRef.getProperty(), property);

					specialEquipmentQiangHuaDataRefMap.put(specialEquipmentQiangHuaDataRefKey, specialEquipmentQiangHuaDataRef);

				}
			}
			ref.setId(MGQiangHuaEquipmentConfig.Special_Id);
			ref.setSpecialEquipmentQiangHuaDataRefMap(specialEquipmentQiangHuaDataRefMap);
		} else if ("equip_strengthening_scroll".equals(refId)) {
			JsonObject scrollConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();

			Map<String, MGQiangHuaScrollDataRef> mgQiangHuaScrollDataRefMap = new HashMap<String, MGQiangHuaScrollDataRef>();
			
			if (scrollConfigData != null) {
				for (Entry<String,JsonElement> entry : scrollConfigData.entrySet()) {
					JsonElement jsonElement = entry.getValue();
					JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();

					String itemRefId =jsonElement.getAsJsonObject().get("itemRefId").getAsString();
					MGQiangHuaScrollDataRef mgQiangHuaScrollDataRef = new MGQiangHuaScrollDataRef();
					mgQiangHuaScrollDataRef.setId(itemRefId);
					fillPropertyDictionary(mgQiangHuaScrollDataRef.getProperty(), property);
					
					mgQiangHuaScrollDataRefMap.put(itemRefId, mgQiangHuaScrollDataRef);

				}
			}
			ref.setId(MGQiangHuaEquipmentConfig.QiangHuaScroll_Id);
			ref.setMgQiangHuaDScrollataRefMap(mgQiangHuaScrollDataRefMap);
		}
	}
}
