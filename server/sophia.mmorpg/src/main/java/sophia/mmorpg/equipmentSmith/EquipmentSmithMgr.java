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
package sophia.mmorpg.equipmentSmith;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.core.propertySmith.PropertyEffectSmithMgr;
import sophia.mmorpg.equipmentSmith.smith.EquipmentSmithRoot;
import sophia.mmorpg.equipmentSmith.smith.MGEquipmentBaseSmith;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaEquipmentSmith;
import sophia.mmorpg.equipmentSmith.smith.xiLian.MGXiLianEquipmentSmith;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.equipment.EquipmentSmith;

public final class EquipmentSmithMgr extends
		PropertyEffectSmithMgr<EquipmentSmith> {

	private EquipmentSmithRoot equipmentSmithRoot;
	private MGQiangHuaEquipmentSmith qiangHuaEquipmentSmith;
	private MGXiLianEquipmentSmith xiLianEquipmentSmith;
	private MGEquipmentBaseSmith equipmentBaseSmith;

	// 构建属性树
	public EquipmentSmithMgr(Item item) {
		equipmentSmithRoot = new EquipmentSmithRoot(item,this.getMountSimithTree());
		qiangHuaEquipmentSmith = new MGQiangHuaEquipmentSmith(item);
		xiLianEquipmentSmith = new MGXiLianEquipmentSmith(item);
		equipmentBaseSmith = new MGEquipmentBaseSmith(item);
		qiangHuaEquipmentSmith.setParent(equipmentSmithRoot);
		xiLianEquipmentSmith.setParent(equipmentSmithRoot);
		equipmentBaseSmith.setParent(equipmentSmithRoot);
		//TODO
		add(equipmentSmithRoot);
		add(qiangHuaEquipmentSmith);
		add(xiLianEquipmentSmith);
		add(equipmentBaseSmith);
	}

	/**
	 * 获取装备总属性
	 * @return
	 */
	public PropertyDictionary getPropertyDictionary(){
		
		return getEquipmentSmithRoot().getPropertyDictionary();
	}
	
	public EquipmentSmithRoot getEquipmentSmithRoot() {
		return equipmentSmithRoot;
	}

	public MGQiangHuaEquipmentSmith getQiangHuaEquipmentSmith() {
		return qiangHuaEquipmentSmith;
	}

	public MGXiLianEquipmentSmith getXiLianEquipmentSmith() {
		return xiLianEquipmentSmith;
	}

	public MGEquipmentBaseSmith getEquipmentBaseSmith() {
		return equipmentBaseSmith;
	}

	
}
