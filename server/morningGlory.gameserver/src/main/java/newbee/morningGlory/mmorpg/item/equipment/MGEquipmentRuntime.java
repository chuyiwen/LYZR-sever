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
package newbee.morningGlory.mmorpg.item.equipment;

import groovy.lang.Closure;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.EquipClosureRef;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.PlayerEquipBodyRuntime;
import sophia.mmorpg.utils.RuntimeResult;

public final class MGEquipmentRuntime implements PlayerEquipBodyRuntime {
	private static final Logger logger = Logger.getLogger(MGEquipmentRuntime.class);

	private final Set<String> commEquipmentItemRefIdSet;

	private final Closure<RuntimeResult> commEquipmentEquipTo;

	private final Closure<RuntimeResult> commEquipmentDeequipFrom;

	private final Map<String, Closure<RuntimeResult>> specalItemRefIdToClosureMap;

	public MGEquipmentRuntime(MGEquipmentClosures mgEquipmentClosures) {
		this.commEquipmentItemRefIdSet = mgEquipmentClosures.getCommEquipmentIdSet();

		this.commEquipmentEquipTo = MGEquipmentClosures.getCommEquipmentEquipTo();

		this.commEquipmentDeequipFrom = MGEquipmentClosures.getCommEquipmentDeequipFrom();

		this.specalItemRefIdToClosureMap = mgEquipmentClosures.getSpecalEquipmentMap();
	}

	public RuntimeResult equipTo(Player player, Item equipment) {
		RuntimeResult result = null;
		if (logger.isDebugEnabled()) {
			logger.debug("正在穿装备");
		}

		ItemRef itemRef = equipment.getItemRef();
		String equipClosure = itemRef.getComponentRef(EquipClosureRef.class).getEquipClosure();
		Closure<RuntimeResult> closure = specalItemRefIdToClosureMap.get(equipClosure);
		if (closure != null) {
			result = closure.call(player, equipment);
		} else {
			result = RuntimeResult.ParameterError("装备id=" + itemRef.getId() + ",的物品。在装备的时候，没有找到对应的使用调用函数。");
		}
		return result;
	}

	public RuntimeResult deequipFrom(Player player, Item equipment) {
		RuntimeResult result = null;
		if (logger.isDebugEnabled()) {
			logger.debug("正在脱装备");
		}

		ItemRef itemRef = equipment.getItemRef();
		String deequipClosure = itemRef.getComponentRef(EquipClosureRef.class).getDeequipClosure();
		Closure<RuntimeResult> closure = specalItemRefIdToClosureMap.get(deequipClosure);
		if (closure != null) {
			result = closure.call(player, equipment);
		} else {
			result = RuntimeResult.ParameterError("装备id=" + itemRef.getId() + ",的物品。在卸下的时候，没有找到对应的使用调用函数。");
		}

		return result;
	}
}
