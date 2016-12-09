package sophia.mmorpg.player.equipment;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class EquipEffectMgr {
	private Player player;

	public EquipEffectMgr(Player player) {
		this.player = player;
	}

	public void restore() {
		PlayerEquipBody equipBody = player.getPlayerEquipBodyConponent().getPlayerBody();
		for (PlayerEquipBodyArea playerEquipBodyArea : equipBody.getBodyAreaList()) {
			Item[] equipArray = playerEquipBodyArea.getEquipmentArray();
			Item item = null;
			if (equipArray.length == 1) {
				if (equipArray[0] != null) {
					item = equipArray[0];
					attachWithoutSnapshot(item);
				}
			} else {
				// 左部位
				if (equipArray[0] != null) {
					item = equipArray[0];
					attachWithoutSnapshot(item);
				}
				// 右部位
				if (equipArray[1] != null) {
					item = equipArray[1];
					attachWithoutSnapshot(item);
				}
			}
			
			changeProperty(playerEquipBodyArea.getId());
		}

	}

	public void attach(Item equipment) {

		PropertyDictionary pd = getProperty(equipment);
		FightPropertyEffectFacade.attachAndNotify(player, pd);
	}

	public void detach(Item equipment) {

		PropertyDictionary pd = getProperty(equipment);
		FightPropertyEffectFacade.detachAndNotify(player, pd);
	}

	public void detachAndSnapshot(Item equipment) {

		PropertyDictionary pd = getProperty(equipment);
		FightPropertyEffectFacade.detachAndSnapshot(player, pd);
	}

	public void attachWithoutSnapshot(Item equipment) {

		FightPropertyEffectFacade.attachWithoutSnapshot(player, getProperty(equipment));
	}

	/**
	 * 改变玩家装备模型属性
	 * 
	 * @param bodyAreaId
	 */
	private void changeProperty(byte bodyAreaId) {
		if (bodyAreaId == PlayerEquipBodyArea.weaponBodyId) {
			int modleId = player.getPlayerEquipBodyConponent().getModleId(bodyAreaId);
			MGPropertyAccesser.setOrPutWeaponModleId(player.getProperty(), modleId);
		} else if (bodyAreaId == PlayerEquipBodyArea.clothesBodyId) {
			int modleId = player.getPlayerEquipBodyConponent().getModleId(bodyAreaId);
			MGPropertyAccesser.setOrPutArmorModleId(player.getProperty(), modleId);
		}
	}

	// private PropertyDictionary getProperty(Item equipment) {
	// PropertyDictionary pd = null;
	// if (equipment.isNonPropertyItem())
	// pd = equipment.getItemRef().getEffectProperty();
	// else {
	// pd = new PropertyDictionary();
	// for (Short symbol : FightEffectProperty.fightEffectSymbols) {
	// if ((int) equipment.getProperty().getValue(symbol) >= 0)
	// pd.setOrPutValue(symbol, equipment.getProperty().getValue(symbol));
	// }
	// }
	//
	// return pd;
	// }

	private PropertyDictionary getProperty(Item equipment) {
		PropertyDictionary pd = new PropertyDictionary();
		PropertyDictionary itemPd = null;
		if (equipment.isNonPropertyItem())
			itemPd = equipment.getItemRef().getEffectProperty();
		else {
			itemPd = equipment.getProperty();
		}

		for (Short symbol : FightEffectProperty.fightEffectSymbols) {
			if ((int) itemPd.getValue(symbol) >= 0)
				pd.setOrPutValue(symbol, itemPd.getValue(symbol));
		}

		return pd;
	}

}
