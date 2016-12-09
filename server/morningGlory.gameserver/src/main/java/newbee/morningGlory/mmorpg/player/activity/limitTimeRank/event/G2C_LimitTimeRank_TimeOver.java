package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_LimitTimeRank_TimeOver extends ActionEventBase {
	private byte openState;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(openState);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public byte getOpenState() {
		return openState;
	}

	public void setOpenState(byte openState) {
		this.openState = openState;
	}

}
