package newbee.morningGlory.mmorpg.operatActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_OA_CanReceiveEvent extends ActionEventBase {

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	

}
