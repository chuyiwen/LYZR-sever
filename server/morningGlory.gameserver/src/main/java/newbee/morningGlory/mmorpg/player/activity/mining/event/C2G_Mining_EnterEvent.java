package newbee.morningGlory.mmorpg.player.activity.mining.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Mining_EnterEvent extends ActionEventBase{
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
		
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}
}
