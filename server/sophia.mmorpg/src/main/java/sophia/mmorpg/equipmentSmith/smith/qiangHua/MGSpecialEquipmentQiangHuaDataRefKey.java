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

import sophia.mmorpg.item.Item;

public final class MGSpecialEquipmentQiangHuaDataRefKey {
	
	private String itemRefId;
	
	private int strengtheningLevel;

	public static MGSpecialEquipmentQiangHuaDataRefKey get(){
		return new MGSpecialEquipmentQiangHuaDataRefKey();
	}
	public static MGSpecialEquipmentQiangHuaDataRefKey get(String itemRefId,int strengtheningLevel){
		return new MGSpecialEquipmentQiangHuaDataRefKey(itemRefId,strengtheningLevel);
	}
	
	public static  MGSpecialEquipmentQiangHuaDataRefKey get(Item equipment){
		return null;
	}
	
	private MGSpecialEquipmentQiangHuaDataRefKey(){
		
	}
	private MGSpecialEquipmentQiangHuaDataRefKey(String itemRefId,int strengtheningLevel){
		this.itemRefId = itemRefId;
		this.strengtheningLevel = strengtheningLevel;
	}
	
	public String getItemRefId() {
		return itemRefId;
	}

	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public int getStrengtheningLevel() {
		return strengtheningLevel;
	}

	public void setStrengtheningLevel(int strengtheningLevel) {
		this.strengtheningLevel = strengtheningLevel;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + strengtheningLevel;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MGSpecialEquipmentQiangHuaDataRefKey other = (MGSpecialEquipmentQiangHuaDataRefKey) obj;
		if (strengtheningLevel != other.strengtheningLevel)
			return false;
		if (!itemRefId.equals(other.itemRefId))
			return false;
		
		return true;
	}
	
}
