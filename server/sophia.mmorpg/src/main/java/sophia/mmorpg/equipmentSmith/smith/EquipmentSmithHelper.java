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
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/** 
 * Copyright (c) 2014 by 游爱.
 *
 * @author 尹杏林  Create on 2014-7-9 下午3:21:30 
 * @version 1.0 
 */
public class EquipmentSmithHelper {

	public static void updateEquipmentProperty(Player player,Item equipment, PropertyDictionary newProperty) {
		for (short symbol : FightEffectProperty.fightEffectSymbols) {
			if ((int) newProperty.getValue(symbol) > 0) {
				equipment.getProperty().setOrPutValue(symbol, newProperty.getValue(symbol));
			}

		}
		int fightValue = player.getFightPower(equipment.getProperty());
		MGPropertyAccesser.setOrPutFightValue(equipment.getProperty(), fightValue);
	}

}
