/**
 * 
 */
package sophia.mmorpg.player.itemBag.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Item_Info extends ActionEventBase {
	private String playerId;
	private String itemId;

	public String getPlayerId() {
		return playerId;
	}

	public String getItemId() {
		return itemId;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.playerId = getString(buffer);
		this.itemId = getString(buffer);
	}

	@Override
	public String getName() {
		return "查看物品";
	}

}
