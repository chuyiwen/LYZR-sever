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
package sophia.mmorpg.equipmentSmith.smith.qiangHua;

import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.item.Item;

/**
 * 装备-强化配置数据
 */
public final class MGQiangHuaEquipmentConfig extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -2183574600473077530L;

	public static final String QiangHua_Id = "MGQiangHuaEquipmentConfig_RefId";

	public static final String Special_Id = "MGSpecialQiangHuaEquipmentConfig_RefId";

	public static final String QiangHuaScroll_Id = "MGQiangHuaScrollEquipmentConfig_RefId";

	private String id = QiangHua_Id;

	private static final int Default_Max_QiangHua_Level = 12;

	private int maxQiangHuaLevel = Default_Max_QiangHua_Level;

	private static final int Default_LockedLevel_Deposit = 30;

	private int lockedLevelDeposit = Default_LockedLevel_Deposit;

	private Map<MGQiangHuaRefKey, MGQiangHuaDataRef> qiangHuaDataRefMap;

	private Map<MGSpecialEquipmentQiangHuaDataRefKey, MGSpecialEquipmentQiangHuaDataRef> specialEquipmentQiangHuaDataRefMap;

	private Map<String, MGQiangHuaScrollDataRef> mgQiangHuaDScrollataRefMap;

	public MGQiangHuaEquipmentConfig() {

	}

	public int getMaxQiangHuaLevel() {
		return maxQiangHuaLevel;
	}

	public void setMaxQiangHuaLevel(int maxQiangHuaLevel) {
		this.maxQiangHuaLevel = maxQiangHuaLevel;
	}

	public int getLockedLevelDeposit() {
		return lockedLevelDeposit;
	}

	public void setLockedLevelDeposit(int lockedLevelDeposit) {
		this.lockedLevelDeposit = lockedLevelDeposit;
	}

	public MGQiangHuaDataRef getQiangHuaDataRef(Item equipment) {
		MGQiangHuaRefKey qiangHuaRefKey = getQiangHuaRefKey(equipment);
		return qiangHuaDataRefMap.get(qiangHuaRefKey);
	}

	public Map<MGQiangHuaRefKey, MGQiangHuaDataRef> getQiangHuaDataRefMap() {
		return qiangHuaDataRefMap;
	}

	public void setQiangHuaDataRefMap(Map<MGQiangHuaRefKey, MGQiangHuaDataRef> qiangHuaDataRefMap) {
		this.qiangHuaDataRefMap = qiangHuaDataRefMap;
	}

	private MGQiangHuaRefKey getQiangHuaRefKey(Item equipment) {
		MGQiangHuaRefKey ret = MGQiangHuaRefKey.get(equipment);

		return ret;
	}

	public Map<MGSpecialEquipmentQiangHuaDataRefKey, MGSpecialEquipmentQiangHuaDataRef> getSpecialEquipmentQiangHuaDataRefMap() {
		return this.specialEquipmentQiangHuaDataRefMap;
	}

	public void setSpecialEquipmentQiangHuaDataRefMap(Map<MGSpecialEquipmentQiangHuaDataRefKey, MGSpecialEquipmentQiangHuaDataRef> specialEquipmentQiangHuaDataRefMap) {
		this.specialEquipmentQiangHuaDataRefMap = specialEquipmentQiangHuaDataRefMap;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public Map<String, MGQiangHuaScrollDataRef> getMgQiangHuaDScrollataRefMap() {
		return mgQiangHuaDScrollataRefMap;
	}

	public void setMgQiangHuaDScrollataRefMap(Map<String, MGQiangHuaScrollDataRef> mgQiangHuaDScrollataRefMap) {
		this.mgQiangHuaDScrollataRefMap = mgQiangHuaDScrollataRefMap;
	}

}
