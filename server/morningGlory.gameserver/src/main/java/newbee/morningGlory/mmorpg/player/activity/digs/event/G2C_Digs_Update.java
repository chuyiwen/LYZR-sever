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

import java.util.TreeSet;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagPartialRenewalData;
import sophia.mmorpg.player.itemBag.ItemBagSlot;

public class G2C_Digs_Update extends ActionEventBase {
	private short capacity;
	private short count;
	private ItemBag digsHouse;
	private Player player;
	private ItemBagPartialRenewalData itemBagPartialRenewalData;
	private byte optype;
	
	public G2C_Digs_Update(){
		ziped= (byte)1;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		capacity = (short) digsHouse.getItemSlotNumber();
		buffer.putShort(capacity);
		buffer.put(optype);
		ByteArrayReadWriteBuffer dataBuffer = new ByteArrayReadWriteBuffer();
		writeToClient(dataBuffer, itemBagPartialRenewalData);		
		itemBagPartialRenewalData.reset();
		return buffer.put(dataBuffer.getData());
	}

	private void writeToClient(ByteArrayReadWriteBuffer buffer, ItemBagPartialRenewalData renewalData) {
		TreeSet<ItemBagSlot> partialRenewalIndexs = renewalData.getPartialRenewalIndexs();
		short size = (short) partialRenewalIndexs.size();
		buffer.writeShort(size);
		for (int i = 0; i < size; i++) {
				
				ItemBagSlot slot = partialRenewalIndexs.pollFirst();
				Item item = slot.getItem();	
				
				String id = item == null ? "" : item.getId();
				String refId = item == null ? "" : item.getItemRef().getId();
				int number = item == null ? 0 : item.getNumber();
				byte bindStatus = item == null ? 0 : item.getBindStatus();

				
				buffer.writeString(id);
				buffer.writeShort(slot.getIndex());
				buffer.writeString(refId);
				buffer.writeShort((short)number);
				buffer.writeByte(bindStatus);
							
			}
		

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

	public byte getOptype() {
		return optype;
	}

	public void setOptype(byte optype) {
		this.optype = optype;
	}

	public ItemBagPartialRenewalData getItemBagPartialRenewalData() {
		return itemBagPartialRenewalData;
	}

	public void setItemBagPartialRenewalData(ItemBagPartialRenewalData itemBagPartialRenewalData) {
		this.itemBagPartialRenewalData = itemBagPartialRenewalData;
	}

}
