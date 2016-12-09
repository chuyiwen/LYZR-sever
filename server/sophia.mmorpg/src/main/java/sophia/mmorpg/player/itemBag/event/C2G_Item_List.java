package sophia.mmorpg.player.itemBag.event;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Item_List extends ActionEventBase {
	private static final Logger logger = Logger.getLogger(C2G_Item_List.class);
	
	public C2G_Item_List()
	{
		this.actionEventId = ItemBagEventDefines.C2G_Item_List;
	}
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter unpackBody");
		
		}
	}
	@Override
	public String getName(){
		return "物品列表";
	}

	
	
	
}
