package newbee.morningGlory.mmorpg.player.store.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Store_ItemListReq extends ActionEventBase {
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

}