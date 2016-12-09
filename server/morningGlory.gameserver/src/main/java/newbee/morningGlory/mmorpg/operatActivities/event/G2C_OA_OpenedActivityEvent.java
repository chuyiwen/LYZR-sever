package newbee.morningGlory.mmorpg.operatActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_OA_OpenedActivityEvent extends ActionEventBase {

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putShort(type);
		return buffer;
	}

	private short type;

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

}
