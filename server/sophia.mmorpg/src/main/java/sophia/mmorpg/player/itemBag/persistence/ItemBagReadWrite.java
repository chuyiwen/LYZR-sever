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
package sophia.mmorpg.player.itemBag.persistence;

import java.util.List;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagSlot;
import sophia.mmorpg.player.itemBag.ItemType;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public final class ItemBagReadWrite extends AbstractPersistenceObjectReadWrite<ItemBag> implements PersistenceObjectReadWrite<ItemBag> {
	private ItemBag itemBag;

	public ItemBagReadWrite(ItemBag itemBag) {
		this.itemBag = itemBag;
	}

	@Override
	public byte[] toBytes(ItemBag persistenceObject) {

		return toBytesVer10001(persistenceObject);
	}

	@Override
	public ItemBag fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		if (version == 10000) {
			return fromBytesVer10000(persistenceBytes);
		} else if (version == 10001) {
			return fromBytesVer10001(persistenceBytes);
		}
		return fromBytesVer10001(persistenceBytes);
	}

	@Override
	public String toJsonString(ItemBag persistenceObject) {
		return toJsonVer10000(persistenceObject);
	}

	@Override
	public ItemBag fromJsonString(String persistenceJsonString) {
		// TODO Auto-generated method stub
		return fromJsonStringVer10000(persistenceJsonString);
	}

	// 之后的数据持久化代码有改动，使用Version
	private byte[] toBytesVer10000(ItemBag persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version);
		buffer.writeInt(persistenceObject.getItemBagCapacity()); // 背包容量
		buffer.writeInt(persistenceObject.getItemSlotNumber()); // 已使用容量
		buffer.writeInt(persistenceObject.getIsFirstPutItemToBagValue()); // 第一次是否已没了
		List<ItemBagSlot> itemBagSlots = persistenceObject.getItemBagCollection();
		for (ItemBagSlot itemBagSlot : itemBagSlots) {
			if (!itemBagSlot.isEmpty()) {
				buffer.writeInt(itemBagSlot.getIndex());
				ItemPersistenceObject.toBytes(Default_Write_Version, buffer, itemBagSlot.getItem());
			}
		}

		byte[] data = buffer.getData();
		return data;
	}

	private ItemBag fromBytesVer10000(byte[] persistenceBytes) {
		if (persistenceBytes == null) {
			return itemBag;
		}
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		buffer.readInt();
		int itemBagCapacity = buffer.readInt();
		int itemSlotNumber = buffer.readInt(); // 已使用格数
		int isFirstPutItemToBag = buffer.readInt();
		itemBag.setIsFirstPutItemToBag(isFirstPutItemToBag);
		itemBag.expendItemBagSlot(itemBagCapacity);
		for (int i = 0; i < itemSlotNumber; i++) {
			String id = buffer.readString();
			int index = buffer.readInt();
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

			itemBag.putItemBySlot(item, index);
		}

		return itemBag;
	}

	private byte[] toBytesVer10001(ItemBag persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version + 1);
		buffer.writeInt(persistenceObject.getItemBagCapacity()); // 背包容量
		buffer.writeInt(persistenceObject.getItemSlotNumber()); // 已使用容量
		buffer.writeInt(persistenceObject.getIsFirstPutItemToBagValue()); // 第一次是否已没了
		List<ItemBagSlot> itemBagSlots = persistenceObject.getItemBagCollection();
		for (ItemBagSlot itemBagSlot : itemBagSlots) {
			if (!itemBagSlot.isEmpty()) {
				buffer.writeInt(itemBagSlot.getIndex());
				ItemPersistenceObject.toBytes(Default_Write_Version + 1, buffer, itemBagSlot.getItem());
			}
		}

		byte[] data = buffer.getData();
		return data;
	}

	private ItemBag fromBytesVer10001(byte[] persistenceBytes) {
		if (persistenceBytes == null) {
			return itemBag;
		}
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		int itemBagCapacity = buffer.readInt();
		int itemSlotNumber = buffer.readInt(); // 已使用格数
		int isFirstPutItemToBag = buffer.readInt();
		itemBag.setIsFirstPutItemToBag(isFirstPutItemToBag);
		itemBag.expendItemBagSlot(itemBagCapacity);
		for (int i = 0; i < itemSlotNumber; i++) {
			int index = buffer.readInt();
			Item item = ItemPersistenceObject.fromBytes(version, buffer);
			itemBag.putItemBySlot(item, index);
		}

		return itemBag;
	}

	private String toJsonVer10000(ItemBag persistenceObject) {
		return null;
	}

	private ItemBag fromJsonStringVer10000(String persistenceJsonString) {
		return null;
	}

	

}
