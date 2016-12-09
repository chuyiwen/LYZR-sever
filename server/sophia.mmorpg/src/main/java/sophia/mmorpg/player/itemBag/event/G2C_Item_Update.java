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

import java.util.TreeSet;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.foundation.util.IoBufferUtil;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagPartialRenewalData;
import sophia.mmorpg.player.itemBag.ItemBagSlot;
import sophia.mmorpg.player.itemBag.ItemWriter;

public class G2C_Item_Update extends ActionEventBase {

	private ItemBagPartialRenewalData itemBagPartialRenewalData;
	private ItemBag itemBag;
	private Player player;
	private byte optype;
	private int isFightValueUpdate = 1; // 2 = 战斗力更新

	public G2C_Item_Update() {
		ziped = (byte) 1;
	}

	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.put(optype);
		ByteArrayReadWriteBuffer dataBuffer = new ByteArrayReadWriteBuffer();
		writeToClient(dataBuffer, itemBagPartialRenewalData);
		return paramIoBuffer.put(dataBuffer.getData());
	}

	private void writeToClient(ByteArrayReadWriteBuffer dataBuffer, ItemBagPartialRenewalData renewalData) {
		TreeSet<ItemBagSlot> partialRenewalIndexs = renewalData.getPartialRenewalIndexs();
		short size = (short) partialRenewalIndexs.size();
		dataBuffer.writeShort(size);
		for (int i = 0; i < size; i++) {
			ItemBagSlot itemBagSlot = partialRenewalIndexs.pollFirst();
			Item item = itemBagSlot.getItem();
			dataBuffer.writeString(item == null ? "" : item.getId());
			dataBuffer.writeShort(itemBagSlot.getIndex());
			String itemRefId = item == null ? "" : item.getItemRef().getId();
			dataBuffer.writeString(itemRefId);
			ItemWriter.write(player, dataBuffer, item, isFightValueUpdate);
		}

	}

	public static IoBuffer packRenewalItemData(IoBuffer buffer, ItemBagPartialRenewalData renewalData, Player player, byte optType) {
		buffer.put(optType);
		TreeSet<ItemBagSlot> partialRenewalIndexs = renewalData.getPartialRenewalIndexs();
		short size = (short) partialRenewalIndexs.size();
		buffer.putShort(size);
		for (int i = 0; i < size; i++) {
			ItemBagSlot itemBagSlot = partialRenewalIndexs.pollFirst();
			Item item = itemBagSlot.getItem();
			IoBufferUtil.putString(buffer, item == null ? "" : item.getId());
			buffer.putShort(itemBagSlot.getIndex());
			String itemRefId = item == null ? "" : item.getItemRef().getId();
			IoBufferUtil.putString(buffer, itemRefId);
			ItemWriter.write(player, buffer, item);
		}
		return buffer;

	}

	public ItemBagPartialRenewalData getItemBagPartialRenewalData() {
		return itemBagPartialRenewalData;
	}

	public void setItemBagPartialRenewalData(ItemBagPartialRenewalData itemBagPartialRenewalData) {
		this.itemBagPartialRenewalData = itemBagPartialRenewalData;
	}

	public byte getOptype() {
		return optype;
	}

	public void setOptype(byte optype) {
		this.optype = optype;
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

	public int getWhichData() {
		return isFightValueUpdate;
	}

	public void setFightValueUpdate(int isFightValueUpdate) {
		this.isFightValueUpdate = isFightValueUpdate;
	}

}
