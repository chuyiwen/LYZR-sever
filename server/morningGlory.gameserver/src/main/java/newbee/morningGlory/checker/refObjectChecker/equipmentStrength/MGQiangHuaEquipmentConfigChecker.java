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
package newbee.morningGlory.checker.refObjectChecker.equipmentStrength;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;

import org.apache.commons.lang3.StringUtils;

import sophia.game.ref.GameRefObject;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaDataRef;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaEquipmentConfig;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaRefKey;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaScrollDataRef;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGSpecialEquipmentQiangHuaDataRef;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGSpecialEquipmentQiangHuaDataRefKey;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGQiangHuaEquipmentConfigChecker extends BaseRefChecker<MGQiangHuaEquipmentConfig> {

	@Override
	public void check(GameRefObject gameRefObject) {
		MGQiangHuaEquipmentConfig equipmentConfig = (MGQiangHuaEquipmentConfig) gameRefObject;
		String id = equipmentConfig.getId();
		if (StringUtils.equals(id, MGQiangHuaEquipmentConfig.QiangHua_Id)) {
			checkQiangHua(equipmentConfig);
		} else if (StringUtils.equals(id, MGQiangHuaEquipmentConfig.Special_Id)) {
			checkExpandQiangHua(equipmentConfig);
		} else if (StringUtils.equals(id, MGQiangHuaEquipmentConfig.QiangHuaScroll_Id)) {
			checkScrollQiangHua(equipmentConfig);
		}
	}

	@Override
	public String getDescription() {

		return "装备强化";
	}

	private void checkQiangHua(MGQiangHuaEquipmentConfig equipmentConfig) {
		for (java.util.Map.Entry<MGQiangHuaRefKey, MGQiangHuaDataRef> entry : equipmentConfig.getQiangHuaDataRefMap().entrySet()) {
			MGQiangHuaDataRef dataRef = entry.getValue();

			byte professionId = MGPropertyAccesser.getProfessionId(dataRef.getProperty());
			byte kind = MGPropertyAccesser.getKind(dataRef.getProperty());
			byte strengLevel = MGPropertyAccesser.getStrengtheningLevel(dataRef.getProperty());
			String itemRefId = MGPropertyAccesser.getItemRefId(dataRef.getProperty());
			if (professionId < 0 || professionId > 3) {
				error(equipmentConfig, "装备强化 professionId 范围错误，应该是[0,3], 错误的refId为: " + itemRefId);
			} else if (kind < 1 || kind > 9) {
				error(equipmentConfig, "装备强化 kind 范围错误，应该是[1,9] ,错误的条目为: " + professionId + " " + kind + " " + strengLevel);
			} else if (strengLevel < 1 || strengLevel > 12) {
				error(equipmentConfig, "装备强化 strengLevel 范围错误，应该是[1,12] ,错误的refId为: " + itemRefId);
			} else if (!StringUtils.equals(itemRefId, "item_qianghuashi")) {
				error(equipmentConfig, "装备强化 所需道具id 错误，应该是 item_qianghuashi,错误的refId为: " + itemRefId);
			} else if (StringUtils.containsWhitespace(itemRefId)) {
				error(equipmentConfig, "装备强化<refId>错误 , 请不要包含空格!!! 错误的refId为: " + itemRefId);
			}

		}
	}

	private void checkExpandQiangHua(MGQiangHuaEquipmentConfig equipmentConfig) {
		for (java.util.Map.Entry<MGSpecialEquipmentQiangHuaDataRefKey, MGSpecialEquipmentQiangHuaDataRef> entry : equipmentConfig.getSpecialEquipmentQiangHuaDataRefMap()
				.entrySet()) {
			MGSpecialEquipmentQiangHuaDataRef dataRef = entry.getValue();
			String itemRefId = MGPropertyAccesser.getItemRefId(dataRef.getProperty());
			if (!itemRefId.startsWith("equip_")) {
				error(equipmentConfig, "扩展强化<refId>错误 , 请以equip_开头!!! 错误的refId为: " + itemRefId);
			} else if (StringUtils.containsWhitespace(itemRefId)) {
				error(equipmentConfig, "扩展强化<refId>错误 , 请不要包含空格!!! 错误的refId为: " + itemRefId);
			}

		}
	}

	private void checkScrollQiangHua(MGQiangHuaEquipmentConfig equipmentConfig) {
		Map<String, MGQiangHuaScrollDataRef> mgQiangHuaDScrollataRefMap = equipmentConfig.getMgQiangHuaDScrollataRefMap();
		for (Entry<String, MGQiangHuaScrollDataRef> entry : mgQiangHuaDScrollataRefMap.entrySet()) {
			MGQiangHuaScrollDataRef dataRef = entry.getValue();
			String itemRefId = dataRef.getId();

			if (!itemRefId.startsWith("item_qianghuajuan_")) {
				error(equipmentConfig, "强化卷强化<refId>错误 , 请以item_qianghuajuan_开头!!! 错误的refId为: " + itemRefId);
			} else if (StringUtils.containsWhitespace(itemRefId)) {
				error(equipmentConfig, "强化卷强化<refId>错误 , 请不要包含空格!!! 错误的refId为: " + itemRefId);
			}

		}
	}

}
