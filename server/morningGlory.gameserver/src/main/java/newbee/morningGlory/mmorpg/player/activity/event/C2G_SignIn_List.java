package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_SignIn_List extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}



}
