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

import org.apache.log4j.Logger;

import sophia.mmorpg.item.Item;

public final class EqiupmentComponentProvider {

	private static Logger logger = Logger.getLogger(EqiupmentComponentProvider.class);

	public static EquipmentSmithComponent getEquipmentSmithComponent(Item equipment) {

		EquipmentSmithComponent equipmentSmithComponent = (EquipmentSmithComponent) equipment.getTagged(EquipmentSmithComponent.Tag);
		if (equipmentSmithComponent == null) {
			try {
				equipmentSmithComponent = EquipmentSmithComponent.class.newInstance();
				equipmentSmithComponent.setParent(equipment);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			equipment.tag(EquipmentSmithComponent.Tag, equipmentSmithComponent);
		}
		return equipmentSmithComponent;
	}

	public static boolean isHadSmithEquipment(Item equipment) {

		EquipmentSmithComponent equipmentSmithComponent = (EquipmentSmithComponent) equipment.getTagged(EquipmentSmithComponent.Tag);

		return equipmentSmithComponent != null;
	}

}
