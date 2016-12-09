package newbee.morningGlory.mmorpg.player.wing.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Wing_RequestNowWing extends ActionEventBase {
	
	public C2G_Wing_RequestNowWing() {
		this.actionEventId = WingEventDefines.C2G_Wing_RequestNowWing;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

}
