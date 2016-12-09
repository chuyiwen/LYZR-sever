/**
 * 
 */
package sophia.mmorpg.equipmentSmith.smith.highestEquipment;

import java.util.Collection;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;

/**
 * @author yinxinglin
 * 
 */
public final class HightestEquipmentFacade {

	private static final String RefId = "highestEquipment";
	private static HighestEquipmentRef ref = null;
	private static short[][] randomPropertys = new short[5][2];
	static {
		randomPropertys[0][0] = MGPropertySymbolDefines.MaxPAtk_Id;
		randomPropertys[0][1] = MGPropertySymbolDefines.MinPAtk_Id;
		randomPropertys[1][0] = MGPropertySymbolDefines.MaxPDef_Id;
		randomPropertys[1][1] = MGPropertySymbolDefines.MinPDef_Id;
		randomPropertys[2][0] = MGPropertySymbolDefines.MaxMAtk_Id;
		randomPropertys[2][1] = MGPropertySymbolDefines.MinMAtk_Id;
		randomPropertys[3][0] = MGPropertySymbolDefines.MaxMDef_Id;
		randomPropertys[3][1] = MGPropertySymbolDefines.MinMDef_Id;
		randomPropertys[4][0] = MGPropertySymbolDefines.MaxTao_Id;
		randomPropertys[4][1] = MGPropertySymbolDefines.MinTao_Id;
		ref = (HighestEquipmentRef) GameRoot.getGameRefObjectManager().getManagedObject(RefId);
	}

	public static void changeEquipmentToBest(Collection<Item> items, byte source) {
		for (Item equipItem : items) {
			changeEquipmentToBest(equipItem, source);
		}
	}

	/**
	 * 某些途径获得的装备要进行一次极品优化（目前有掉落，挖宝，商城购买）
	 * 
	 * @param equipment
	 */
	public static void changeEquipmentToBest(Item equipment, byte source) {

		Preconditions.checkNotNull(equipment, "equipment is null");
		if (!equipment.isEquip()) {
			return;
		}
		if (!isNeedChange(equipment, source)) {
			return;
		}

		int maxMainSymbolValue = getMainSymbolValue(equipment);

		int randomNumber = getRandomNumber();

		changeEquipment(equipment, maxMainSymbolValue, randomNumber);
	}

	private static void changeEquipment(Item equipment, int maxMainSymbolValue, int randomNumber) {

		int MinRate = ref.getMinRate();
		int MaxRate = ref.getMaxRate();
		PropertyDictionary pd = equipment.getItemRef().getEffectProperty();

		for (int k = 0; k < randomNumber; k++) {

			double rate = SFRandomUtils.random(MinRate, MaxRate);
			int resultProperty = SFRandomUtils.random(0, randomPropertys.length - 1);
			short maxModifySymbol = randomPropertys[resultProperty][0]; // 被随机选出的属性字段
			short minModifySymbol = randomPropertys[resultProperty][1];

			int maxSymbolValue = (int) (maxMainSymbolValue * (rate / 100.0));
			int minSymbolValue = 0; // 最小不为变

			if (maxSymbolValue <= 0) {
				continue;
			}

			if (equipment.isNonPropertyItem()) {
				equipment.changePropertyItem();
			}

			MGPropertyAccesser.setOrPutIsHighestEquipment(equipment.getProperty(), Item.IsHighestEquipment);

			int oldMaxSymbolValue = pd.getValue(maxModifySymbol);
			int oldMinSymbolValue = pd.getValue(minModifySymbol);
			
			int oldAttachMaxSymbolValue = equipment.getNewAttachPropertyIfNull().getValue(maxModifySymbol);
			int oldAttachMinSymbolValue = equipment.getNewAttachPropertyIfNull().getValue(minModifySymbol);

			int newMaxSymbolValue = oldMaxSymbolValue + maxSymbolValue;
			int newMinSymbolValue = oldMinSymbolValue + minSymbolValue;

			equipment.getProperty().setOrPutValue(maxModifySymbol, newMaxSymbolValue);
			equipment.getProperty().setOrPutValue(minModifySymbol, newMinSymbolValue);

			equipment.getNewAttachPropertyIfNull().setOrPutValue(maxModifySymbol, oldAttachMaxSymbolValue + maxSymbolValue);
			equipment.getNewAttachPropertyIfNull().setOrPutValue(minModifySymbol, oldAttachMinSymbolValue + minSymbolValue);

		}
	}

	/**
	 * 随机属性种类数量
	 * 
	 * @param probability
	 * @param defaultCount
	 * @return
	 */
	private static int getRandomNumber() {
		int[] probability = ref.getProbability();
		int[] defaultCount = ref.getRandomCount();
		int random = SFRandomUtils.random100();
		int totalProbability = 0;
		int randomIndex = 0;
		for (int j = 0; j < probability.length; j++) {
			totalProbability += probability[j];
			if (random <= totalProbability) {
				randomIndex = j;
				break;
			}
		}

		int randomNumber = defaultCount[randomIndex]; // 获得的随机属性数量

		return randomNumber;
	}

	/**
	 * 获得主属性的值
	 * 
	 * @param pd
	 * @return
	 */
	private static int getMainSymbolValue(Item equipment) {
		PropertyDictionary pd = equipment.getItemRef().getEffectProperty();
		int[] propertys = new int[5];
		propertys[0] = MGPropertyAccesser.getMaxPAtk(pd);
		propertys[1] = MGPropertyAccesser.getMaxPDef(pd);
		propertys[2] = MGPropertyAccesser.getMaxMAtk(pd);
		propertys[3] = MGPropertyAccesser.getMaxMDef(pd);
		propertys[4] = MGPropertyAccesser.getMaxTao(pd);
		int mainProperty = 0;
		for (int i = 0; i < propertys.length; i++) {
			if (propertys[i] > mainProperty) {
				mainProperty = propertys[i];
			}
		}
		return mainProperty;
	}

	/**
	 * 是否能变为极品装备
	 * 
	 * @param equipItem
	 * @param source
	 * @return
	 */
	private static boolean isNeedChange(Item equipItem, byte source) {
		if (equipItem.isEquip() && (source == ItemOptSource.Digs || source == ItemOptSource.Loot || source == ItemOptSource.Store)) {
			return true;
		}
		return false;
	}
}
