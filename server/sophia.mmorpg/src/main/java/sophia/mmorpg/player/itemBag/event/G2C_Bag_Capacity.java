/**
 * 
 */
package sophia.mmorpg.player.itemBag.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.itemBag.ItemBag;


public class G2C_Bag_Capacity extends ActionEventBase {

	private ItemBag itemBag;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		
		short capacity = itemBag.getItemBagMaxCapacity();
		short count = (short)itemBag.getItemBagCapacity();
		buffer.putShort(capacity);
		buffer.putShort(count);
		
		return buffer;
	}

	
	@Override
	public void unpackBody(IoBuffer buffer) {
		

	}


	public ItemBag getItemBag() {
		return itemBag;
	}


	public void setItemBag(ItemBag itemBag) {
		this.itemBag = itemBag;
	}
	
	

}
