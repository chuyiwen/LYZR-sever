package newbee.morningGlory.mmorpg.player.store.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Discount_BeginOrEndNotify extends ActionEventBase {
	private byte type;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(type);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setType(byte type) {
		this.type = type;
	}

}
