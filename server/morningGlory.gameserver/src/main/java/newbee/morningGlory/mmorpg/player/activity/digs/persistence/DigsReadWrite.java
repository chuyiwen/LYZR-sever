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
package newbee.morningGlory.mmorpg.player.activity.digs.persistence;

import java.util.List;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagSlot;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public final class DigsReadWrite extends AbstractPersistenceObjectReadWrite<ItemBag> implements PersistenceObjectReadWrite<ItemBag> {
	private ItemBag digsHouse;

	public DigsReadWrite(ItemBag digsHouse) {
		this.digsHouse = digsHouse;
	}

	@Override
	public byte[] toBytes(ItemBag persistenceObject) {
		return toBytesVer10000(persistenceObject);
	}

	@Override
	public ItemBag fromBytes(byte[] persistenceBytes) {
		return fromBytesVer10000(persistenceBytes);
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
		List<ItemBagSlot> itemBagSlots = persistenceObject.getItemBagCollection();
		for (ItemBagSlot itemBagSlot : itemBagSlots) {
			if (!itemBagSlot.isEmpty()) {
				toBytes(buffer, itemBagSlot);
			}
		}

		byte[] data = buffer.getData();
		return data;
	}

	private ItemBag fromBytesVer10000(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int writeVersion = buffer.readInt();
		int itemBagCapacity = buffer.readInt();
		int itemSlotNumber = buffer.readInt(); // 已使用格数
		digsHouse.expendItemBagSlot(itemBagCapacity);
		for (int i = 0; i < itemSlotNumber; i++) {
			String id = buffer.readString();
			int index = buffer.readInt();
			String itemRefId = buffer.readString();
			int number = buffer.readInt();
			byte bindStatus = buffer.readByte();
			Item item = GameObjectFactory.getItem(itemRefId, id);
			item.setNumber(number);
			item.setBindStatus(bindStatus);

			digsHouse.putItemBySlot(item, index);
		}

		return digsHouse;
	}

	private String toJsonVer10000(ItemBag persistenceObject) {
		return null;
	}

	private ItemBag fromJsonStringVer10000(String persistenceJsonString) {
		return null;
	}

	private ByteArrayReadWriteBuffer toBytes(ByteArrayReadWriteBuffer buffer, ItemBagSlot itemBagSlot) {
		Item item = itemBagSlot.getItem();
		buffer.writeString(item.getId());
		buffer.writeInt(itemBagSlot.getIndex());
		buffer.writeString(item.getItemRef().getId());
		buffer.writeInt(item.getNumber());
		buffer.writeByte(item.getBindStatus());

		return buffer;
	}

}
