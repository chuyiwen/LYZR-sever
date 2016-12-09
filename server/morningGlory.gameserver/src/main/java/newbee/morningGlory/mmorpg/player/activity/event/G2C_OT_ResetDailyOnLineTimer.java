package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_OT_ResetDailyOnLineTimer extends ActionEventBase {
	private byte reset;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put((byte) 1);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public byte getReset() {
		return reset;
	}

	public void setReset(byte reset) {
		this.reset = reset;
	}

}
