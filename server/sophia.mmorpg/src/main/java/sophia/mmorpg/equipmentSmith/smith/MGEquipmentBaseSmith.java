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
package sophia.mmorpg.equipmentSmith.smith;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.equipment.AbstractEquipmentSmith;

public final class MGEquipmentBaseSmith extends AbstractEquipmentSmith {

	private PropertyDictionary propertyDictionary;

	public MGEquipmentBaseSmith(Item owner) {
		super(owner);

	}

	@Override
	public PropertyDictionary getPropertyDictionary() {
		calculate();
		return propertyDictionary;
	}

	@Override
	protected void calculate() {
		Item equipment = getOwner();
		propertyDictionary = new PropertyDictionary();
		PropertyDictionary refDictionary = equipment.getItemRef().getEffectProperty();
		for (short symbol : FightEffectProperty.fightEffectSymbols) {
			int baseValue = (int) refDictionary.getValue(symbol);
			baseValue = baseValue < 0 ? 0 : baseValue;
			if (equipment.getAttachProperty() != null && (int) equipment.getAttachProperty().getValue(symbol) >= 0) {
				baseValue += (int) equipment.getNewAttachPropertyIfNull().getValue(symbol);
			}
			propertyDictionary.setOrPutValue(symbol, baseValue);
		}

	}

}
