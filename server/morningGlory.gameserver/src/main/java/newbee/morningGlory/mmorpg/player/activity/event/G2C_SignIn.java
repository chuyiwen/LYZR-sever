package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_SignIn extends ActionEventBase {
	private byte day;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(day);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public byte getDay() {
		return day;
	}

	public void setDay(byte day) {
		this.day = day;
	}

}
