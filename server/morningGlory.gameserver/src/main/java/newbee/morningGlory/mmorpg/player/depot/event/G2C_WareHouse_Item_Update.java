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
package newbee.morningGlory.mmorpg.player.depot.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBagPartialRenewalData;
import sophia.mmorpg.player.itemBag.event.G2C_Item_Update;

public class G2C_WareHouse_Item_Update extends ActionEventBase {
	private byte optType;
	private Player player;
	private ItemBagPartialRenewalData partialRenewalData;

	public G2C_WareHouse_Item_Update() {
		this.actionEventId = PlayerDepotEventDefines.G2C_WareHouse_Item_Update;
	}

	public G2C_WareHouse_Item_Update(byte optType, Player player, ItemBagPartialRenewalData partialRenewalData) {
		super();
		this.optType = optType;
		this.player = player;
		this.partialRenewalData = partialRenewalData;
		this.actionEventId = PlayerDepotEventDefines.G2C_WareHouse_Item_Update;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return G2C_Item_Update.packRenewalItemData(buffer, partialRenewalData, player, optType);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public byte getOptType() {
		return optType;
	}

	public void setOptType(byte optType) {
		this.optType = optType;
	}

}
