package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Activity_CanRecieve extends ActionEventBase {
	private byte type;
	private byte result;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(type);
		buffer.put(result);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setResult(byte result) {
		this.result = result;
	}

	public void setType(byte type) {
		this.type = type;
	}

}
