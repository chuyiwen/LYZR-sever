package newbee.morningGlory.mmorpg.player.activity.mining.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mining_Open extends ActionEventBase {
	public static final byte Open = 0;
	public static final byte Close = 1;
	
	private byte openState;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(openState);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setOpenState(byte openState) {
		this.openState = openState;
	}

}
