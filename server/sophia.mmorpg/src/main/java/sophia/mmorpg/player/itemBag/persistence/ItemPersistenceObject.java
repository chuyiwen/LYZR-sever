/**
 * 
 */
package sophia.mmorpg.player.itemBag.persistence;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.itemBag.ItemType;

/**
 * @author Administrator
 * 
 */
public class ItemPersistenceObject {

	public static ByteArrayReadWriteBuffer toBytes(int version, ByteArrayReadWriteBuffer buffer, Item item) {
		if(version == 10000){
			return toBytes10000(buffer, item);
		}else if(version == 10001){
			return toBytes10001(buffer, item);
		}
		return toBytes10001(buffer, item);
	}
	
	public static Item fromBytes(int version, ByteArrayReadWriteBuffer buffer) {
		if(version == 10000){
			return fromBytes10000(buffer);
		}else if(version == 10001){
			return fromBytes10001(buffer);
		}
		return fromBytes10001(buffer);
	}

	public static ByteArrayReadWriteBuffer toBytes10001(ByteArrayReadWriteBuffer buffer ,Item item) {
		buffer.writeString(item.getId());
		buffer.writeString(item.getItemRef().getId());
		buffer.writeInt(item.getNumber());
		buffer.writeByte(item.getBindStatus());

		if (!item.isNonPropertyItem()) {
			buffer.writeByte((byte) 1); // 用于标识 isNonPropertyItem
			byte[] itemProperties = item.getProperty().toByteArray();
			buffer.writeInt(itemProperties.length);
			buffer.writeBytes(itemProperties);
			buffer.writeByte(item.getItemType());
			if (item.getItemType() == ItemType.Equip && EqiupmentComponentProvider.isHadSmithEquipment(item)) {
				buffer.writeByte((byte) 1);
				byte[] washProperties = EqiupmentComponentProvider.getEquipmentSmithComponent(item).getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary()
						.toByteArray();
				buffer.writeInt(washProperties.length);
				buffer.writeBytes(washProperties);
			} else {
				buffer.writeByte((byte) 0);
			}
			if (item.getAttachProperty() != null) {
				buffer.writeByte((byte) 1);
				byte[] attachProperties = item.getAttachProperty().toByteArray();
				buffer.writeInt(attachProperties.length);
				buffer.writeBytes(attachProperties);
			} else {
				buffer.writeByte((byte) 0);
			}
		} else
			buffer.writeByte((byte) 0);

		return buffer;
	}

	public static Item fromBytes10001(ByteArrayReadWriteBuffer buffer) {
		String id = buffer.readString();
		String itemRefId = buffer.readString();
		int number = buffer.readInt();
		byte bindStatus = buffer.readByte();
		Item item = GameObjectFactory.getItem(itemRefId, id);
		item.setNumber(number);
		item.setBindStatus(bindStatus);

		byte isNonPropertyItem = buffer.readByte();
		if (isNonPropertyItem == 1) {
			int itemLength = buffer.readInt();
			byte[] itemProperties = buffer.readBytes(itemLength);
			item.getProperty().loadDictionary(itemProperties);
			byte itemType = buffer.readByte();
			byte hasSmith = buffer.readByte();
			if (itemType == ItemType.Equip && hasSmith == 1) {
				int washLength = buffer.readInt();
				byte[] washProperties = buffer.readBytes(washLength);
				EquipmentSmithComponent equipmentSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(item);
				equipmentSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary().loadDictionary(washProperties);
				equipmentSmithComponent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith().onPropertyChange();

			}
			byte hasAttachPd = buffer.readByte();
			if (hasAttachPd == 1) {
				int attachLenght = buffer.readInt();
				byte[] attachProperties = buffer.readBytes(attachLenght);
				item.getNewAttachPropertyIfNull().loadDictionary(attachProperties);
			}
		}

		return item;
	}

	public static Item fromBytes10000(ByteArrayReadWriteBuffer buffer) {
		String id = buffer.readString();
		String itemRefId = buffer.readString();
		int number = buffer.readInt();
		byte bindStatus = buffer.readByte();
		Item item = GameObjectFactory.getItem(itemRefId, id);
		item.setNumber(number);
		item.setBindStatus(bindStatus);

		byte isNonPropertyItem = buffer.readByte();
		if (isNonPropertyItem == 1) {

			int itemLength = buffer.readInt();
			byte[] itemProperties = buffer.readBytes(itemLength);
			item.getProperty().loadDictionary(itemProperties);
			byte itemType = buffer.readByte();
			if (itemType == ItemType.Equip) {
				int washLength = buffer.readInt();
				byte[] washProperties = buffer.readBytes(washLength);
				EquipmentSmithComponent equipmentSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(item);
				equipmentSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary().loadDictionary(washProperties);
				equipmentSmithComponent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith().onPropertyChange();

			}
			int attachLenght = buffer.readInt();
			byte[] attachProperties = buffer.readBytes(attachLenght);
			item.getNewAttachPropertyIfNull().loadDictionary(attachProperties);
		}

		return item;
	}

	public static ByteArrayReadWriteBuffer toBytes10000(ByteArrayReadWriteBuffer buffer, Item item) {
		buffer.writeString(item.getId());	
		buffer.writeString(item.getItemRef().getId());
		buffer.writeInt(item.getNumber());
		buffer.writeByte(item.getBindStatus());

		if (!item.isNonPropertyItem()) {
			buffer.writeByte((byte) 1); // 用于标识 isNonPropertyItem
			byte[] itemProperties = item.getProperty().toByteArray();
			buffer.writeInt(itemProperties.length);
			buffer.writeBytes(itemProperties);
			buffer.writeByte(item.getItemType());
			if (item.getItemType() == ItemType.Equip) {
				byte[] washProperties = EqiupmentComponentProvider.getEquipmentSmithComponent(item).getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary()
						.toByteArray();
				buffer.writeInt(washProperties.length);
				buffer.writeBytes(washProperties);
			}
			byte[] attachProperties = item.getAttachProperty().toByteArray();
			buffer.writeInt(attachProperties.length);
			buffer.writeBytes(attachProperties);
		} else
			buffer.writeByte((byte) 0);

		return buffer;
	}

}
