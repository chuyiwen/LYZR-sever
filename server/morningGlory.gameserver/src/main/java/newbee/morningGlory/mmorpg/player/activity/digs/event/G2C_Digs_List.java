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
package newbee.morningGlory.mmorpg.player.activity.digs.event;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagSlot;

public class G2C_Digs_List extends ActionEventBase {
	private short capacity;
	private short count;
	private ItemBag digsHouse;
	private Player player;

	public G2C_Digs_List(){
		ziped = (byte)1;
	}
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {

		capacity = (short) digsHouse.getItemSlotNumber();
		List<ItemBagSlot> itemBagSlots = digsHouse.getItemBagCollection();
		buffer.putShort(capacity);
		count = (short) digsHouse.getItemSlotNumber();
		buffer.putShort(count);
		for (ItemBagSlot slot : itemBagSlots) {
			if (!slot.isEmpty()) {
				Item item = slot.getItem();
				ItemRef itemRef = item == null ? null : item.getItemRef();
				String refId = itemRef == null ? "" : itemRef.getId();
				int number = item == null ? 0 : item.getNumber();
				byte bindStatus = item == null ? 0 : item.getBindStatus();

				putString(buffer, item == null ? "" : item.getId());
				buffer.putShort(slot.getIndex());
				putString(buffer, refId);
				buffer.putShort((short)number);
				buffer.put(bindStatus);

			}

		}
		digsHouse.getNoResetItemBagPartialRenewalData().reset();
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

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

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public ItemBag getDigsHouse() {
		return digsHouse;
	}

	public void setDigsHouse(ItemBag digsHouse) {
		this.digsHouse = digsHouse;
	}

}
