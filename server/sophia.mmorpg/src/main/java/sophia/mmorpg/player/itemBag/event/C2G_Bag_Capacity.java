/**
 * 
 */
package sophia.mmorpg.player.itemBag.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;


public class C2G_Bag_Capacity extends ActionEventBase {

	
	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		
		return null;
	}


	@Override
	public void unpackBody(IoBuffer arg0) {
		

	}
	
	@Override
	public String getName() {
		
		return "物品容量";
	}

}
