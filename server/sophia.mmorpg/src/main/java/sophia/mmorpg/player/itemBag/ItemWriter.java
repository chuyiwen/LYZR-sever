/**
 * 
 */
package sophia.mmorpg.player.itemBag;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

/**
 * @author yinxinglin
 * 
 */
public class ItemWriter {

	public static void write(Player player, ByteArrayReadWriteBuffer dataBuffer, Item item, int isFightValueUpdate) {
		write(player, dataBuffer, item);
	}

	public static void write(Player player, IoBuffer buffer, Item item) {
		// 数据字典的数量
		byte pdCount = 1;
		PropertyDictionary xiLianPd = null;
		if (item != null) {
			EquipmentSmithComponent equipmentSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(item);
			xiLianPd = equipmentSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary();
			if (xiLianPd != null && xiLianPd.getDictionary().size() != 0) {
				pdCount++;
			}
		}
		buffer.put(pdCount);
		// 物品属性pd
		PropertyDictionary dictionary = null;
		if (item != null && !item.isNonPropertyItem()) {
			dictionary = item.getProperty();
		} else {
			dictionary = new PropertyDictionary();
			if (item != null) {
				dictionary.copyFrom(item.getItemRef().getEffectProperty());
			}
		}

		int number = item == null ? 0 : item.getNumber();
		byte bindStatus = item == null ? 0 : item.getBindStatus();

		MGPropertyAccesser.setOrPutNumber(dictionary, number);
		MGPropertyAccesser.setOrPutBindStatus(dictionary, bindStatus);
		if (item != null && item.isEquip()) {
			int fightValue = player.getFightPower(dictionary);
			MGPropertyAccesser.setOrPutFightValue(dictionary, fightValue);
			if (!dictionary.contains(MGPropertySymbolDefines.StrengtheningLevel_Id)) {
				MGPropertyAccesser.setOrPutStrengtheningLevel(dictionary, (byte) 0);
			}
		}
		buffer.put(ItemCode.TOTAL_PD_UPDATE);
		byte[] byteArray = dictionary.toByteArray();
		buffer.putShort((short) byteArray.length);
		buffer.put(byteArray);
		if (item != null && xiLianPd != null && xiLianPd.getDictionary().size() != 0) {
			buffer.put(ItemCode.WASH_PD_UPDATE);
			byte[] xiLianByteArray = xiLianPd.toByteArray();
			buffer.putShort((short) xiLianByteArray.length);
			buffer.put(xiLianByteArray);
		}
	}

	public static void write(Player player, ByteArrayReadWriteBuffer dataBuffer, Item item) {

		// 数据字典的数量
		byte pdCount = 1;
		PropertyDictionary xiLianPd = null;
		if (item != null) {
			EquipmentSmithComponent equipmentSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(item);
			xiLianPd = equipmentSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary();
			if (xiLianPd != null && xiLianPd.getDictionary().size() != 0) {
				pdCount++;
			}
		}
		dataBuffer.writeByte(pdCount);
		// 物品属性pd
		PropertyDictionary dictionary = null;
		if (item != null && !item.isNonPropertyItem()) {
			dictionary = item.getProperty();
		} else {
			dictionary = new PropertyDictionary();
			if (item != null) {
				dictionary.copyFrom(item.getItemRef().getEffectProperty());
			}
		}

		int number = item == null ? 0 : item.getNumber();
		byte bindStatus = item == null ? 0 : item.getBindStatus();

		MGPropertyAccesser.setOrPutNumber(dictionary, number);
		MGPropertyAccesser.setOrPutBindStatus(dictionary, bindStatus);
		if (item != null && item.isEquip()) {
			int fightValue = player.getFightPower(dictionary);
			MGPropertyAccesser.setOrPutFightValue(dictionary, fightValue);
			if (!dictionary.contains(MGPropertySymbolDefines.StrengtheningLevel_Id)) {
				MGPropertyAccesser.setOrPutStrengtheningLevel(dictionary, (byte) 0);
			}

		}
		dataBuffer.writeByte(ItemCode.TOTAL_PD_UPDATE);
		byte[] byteArray = dictionary.toByteArray();
		dataBuffer.writeShort((short) byteArray.length);
		dataBuffer.writeBytes(byteArray);
		if (item != null && xiLianPd != null && xiLianPd.getDictionary().size() != 0) {
			dataBuffer.writeByte(ItemCode.WASH_PD_UPDATE);
			byte[] xiLianByteArray = xiLianPd.toByteArray();
			dataBuffer.writeShort((short) xiLianByteArray.length);
			dataBuffer.writeBytes(xiLianByteArray);
		}

	}

}
