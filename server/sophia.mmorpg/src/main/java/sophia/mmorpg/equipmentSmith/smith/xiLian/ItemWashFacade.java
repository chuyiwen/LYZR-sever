/**
 * 
 */
package sophia.mmorpg.equipmentSmith.smith.xiLian;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class ItemWashFacade {

	/**
	 * 装备洗练外部调用
	 * @param equipment
	 * @param player
	 * @return
	 */
	public static boolean washItem(Item equipment) {
		if (equipment == null) {
			return false;
		}
		if (!equipment.isEquip()) {
			return false;
		}

		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);

		MGXiLianEquipmentSmith xiLianEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getXiLianEquipmentSmith();

		if (xiLianEquipmentSmith.xiLian()) {
			MGPropertyAccesser.setOrPutIsNonPropertyItem(equipment.getProperty(), Item.Is_Property_Item);
			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			updateEquipmentProperty(equipment, newProperty);
			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();
			return true;
		}

		return false;
	}

	private static void updateEquipmentProperty(Item equipment, PropertyDictionary newProperty) {
		for (short symbol : FightEffectProperty.fightEffectSymbols) {
			if ((int) newProperty.getValue(symbol) >= 0) {
				equipment.getProperty().setOrPutValue(symbol, newProperty.getValue(symbol));
			}

		}
//		int fightValue = player.getFightPower(equipment.getProperty());
//		MGPropertyAccesser.setOrPutFightValue(equipment.getProperty(), fightValue);
	}
}
