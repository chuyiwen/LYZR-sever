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
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.event.G2C_Item_List;

public class G2C_WareHouse_Item_List extends ActionEventBase {
	private Player player;
	private ItemBag depot;

	public G2C_WareHouse_Item_List() {
		this.actionEventId = PlayerDepotEventDefines.G2C_WareHouse_Item_List;
		this.ziped = 1;
	}

	public G2C_WareHouse_Item_List(Player player, ItemBag depot) {
		super();
		this.player = player;
		this.depot = depot;
		this.actionEventId = PlayerDepotEventDefines.G2C_WareHouse_Item_List;
		this.ziped = 1;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return G2C_Item_List.packBody(buffer, player, depot);
	}

	public ItemBag getDepot() {
		return depot;
	}

	public void setDepot(ItemBag depot) {
		this.depot = depot;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
