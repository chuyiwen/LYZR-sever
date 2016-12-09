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
package sophia.mmorpg.player.itemBag.event;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagSlot;
import sophia.mmorpg.player.itemBag.ItemWriter;

public class G2C_Item_List extends ActionEventBase {
	private short capacity;
	private short count;
	private ItemBag itemBag;
	private Player player;

	public G2C_Item_List() {
		this.actionEventId = ItemBagEventDefines.G2C_Item_List;
		ziped = (byte) 1;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return packBody(buffer, player, itemBag);
	}

	public static IoBuffer packBody(IoBuffer buffer, Player player, ItemBag itemBag) {
		ByteArrayReadWriteBuffer dataBuffer = new ByteArrayReadWriteBuffer();
		short capacity = (short) itemBag.getItemBagCapacity();
		List<ItemBagSlot> itemBagSlots = itemBag.getItemBagCollection();
		dataBuffer.writeShort(capacity);
		short count = (short) itemBag.getItemSlotNumber();
		dataBuffer.writeShort(count);
		for (ItemBagSlot itemBagSlot : itemBagSlots) {
			if (!itemBagSlot.isEmpty()) {
				Item item = itemBagSlot.getItem();
				dataBuffer.writeString(item == null ? "" : item.getId());
				dataBuffer.writeShort(itemBagSlot.getIndex());
				String itemRefId = item == null ? "" : item.getItemRef().getId();
				dataBuffer.writeString(itemRefId);
				if (item == null) {
					continue;
				}
				ItemWriter.write(player, dataBuffer, item);
			}

		}
		itemBag.getNoResetItemBagPartialRenewalData().reset();
		return buffer.put(dataBuffer.getData());
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	public String getName() {
		return "返回物品列表";
	}

	public short getCapacity() {
		return capacity;
	}

	public void setCapacity(short capacity) {
		this.capacity = capacity;
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public ItemBag getItemBag() {

		return itemBag;
	}

	public void setItemBag(ItemBag itemBag) {
		this.itemBag = itemBag;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
