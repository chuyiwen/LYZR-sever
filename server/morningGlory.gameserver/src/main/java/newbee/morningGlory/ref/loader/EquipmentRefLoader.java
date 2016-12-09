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

import newbee.morningGlory.ref.RefKey;
import sophia.mmorpg.item.ref.EquipClosureRef;
import sophia.mmorpg.item.ref.ItemRef;

import com.google.gson.JsonObject;

public class EquipmentRefLoader extends AbstractItemRefLoader {

	public EquipmentRefLoader() {
		super(RefKey.equipment);
	}

	@Override
	protected void fillNonPropertyDictionary(ItemRef ref, JsonObject refData) {

		String equipClosure = refData.get("equipClosure").getAsString();
		String deequipClosure = refData.get("deequipClosure").getAsString();
		ref.addComponentRef(new EquipClosureRef(equipClosure, deequipClosure));
		super.fillNonPropertyDictionary(ref, refData);
	}
}
