package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_MultiTimesExp_State extends ActionEventBase{
	public static final byte Open = 0;
	public static final byte Close = 1;
	
	private byte state;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(state);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
}
