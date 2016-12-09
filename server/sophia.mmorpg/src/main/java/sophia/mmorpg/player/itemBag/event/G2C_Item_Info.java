package sophia.mmorpg.player.itemBag.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBagSlot;
import sophia.mmorpg.player.itemBag.ItemWriter;

public class G2C_Item_Info extends ActionEventBase {

	private ItemBagSlot slot;
	private Player player;
	

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {

		if (slot != null) {
			if (!slot.isEmpty()) {
				Item item = slot.getItem();
				putString(buffer, item == null ? "" : item.getId());
				buffer.putShort(slot.getIndex());
				if (item == null) {
					return buffer;
				}
				ItemRef itemRef = item.getItemRef();
				String refId = itemRef.getId();
				putString(buffer, refId);
				ItemWriter.write(player, buffer, item);
			}

		}

		return buffer;
	}

	public void setSlot(ItemBagSlot slot) {
		this.slot = slot;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		
	}



}
