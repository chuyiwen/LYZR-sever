package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_SignIn extends ActionEventBase {
	private byte singType;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		singType = buffer.get();
	}

	public byte getSingType() {
		return singType;
	}

	public void setSingType(byte singType) {
		this.singType = singType;
	}

}
