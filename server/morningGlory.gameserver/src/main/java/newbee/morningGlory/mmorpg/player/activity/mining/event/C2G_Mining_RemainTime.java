package newbee.morningGlory.mmorpg.player.activity.mining.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Mining_RemainTime extends ActionEventBase{
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

}
