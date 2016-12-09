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

public final class MGQiangHuaRefKey {
	private byte playerPrefessionId;
	
	private byte equipmentBodyAreaId;
	
	private byte qiangHuaLevel;
	
	public static final MGQiangHuaRefKey get() {
		return new MGQiangHuaRefKey();
	}
	
	public static final MGQiangHuaRefKey get(byte playerPrefessionId, byte equipmentBodyAreaId, byte qiangHuaLevel) {
		return new MGQiangHuaRefKey(playerPrefessionId, equipmentBodyAreaId, qiangHuaLevel);
	}
	
	public static final MGQiangHuaRefKey get(Item item) {
		// TODO
		return null;
	}
	
	private MGQiangHuaRefKey() {
		
	}
	
	private MGQiangHuaRefKey(byte playerPrefessionId, byte equipmentBodyAreaId, byte qiangHuaLevel) {
		this.playerPrefessionId = playerPrefessionId;
		this.equipmentBodyAreaId = equipmentBodyAreaId;
		this.qiangHuaLevel = qiangHuaLevel;
	}

	public byte getPlayerPrefessionId() {
		return playerPrefessionId;
	}

	public void setPlayerPrefessionId(byte playerPrefessionId) {
		this.playerPrefessionId = playerPrefessionId;
	}

	public byte getEquipmentBodyAreaId() {
		return equipmentBodyAreaId;
	}

	public void setEquipmentBodyAreaId(byte equipmentBodyAreaId) {
		this.equipmentBodyAreaId = equipmentBodyAreaId;
	}

	

	public byte getQiangHuaLevel() {
		return qiangHuaLevel;
	}

	public void setQiangHuaLevel(byte qiangHuaLevel) {
		this.qiangHuaLevel = qiangHuaLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + equipmentBodyAreaId;
		result = prime * result + playerPrefessionId;
		result = prime * result + qiangHuaLevel;
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
		MGQiangHuaRefKey other = (MGQiangHuaRefKey) obj;
		if (equipmentBodyAreaId != other.equipmentBodyAreaId)
			return false;
		if (playerPrefessionId != other.playerPrefessionId)
			return false;
		if (qiangHuaLevel != other.qiangHuaLevel)
			return false;
		return true;
	}
	@Override
	public String toString(){
		return "职业:"+playerPrefessionId + " , 装备部位:"+equipmentBodyAreaId +", 强化级别:"+qiangHuaLevel;
	}
}
