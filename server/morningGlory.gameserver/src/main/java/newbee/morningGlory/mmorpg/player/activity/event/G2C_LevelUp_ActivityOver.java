package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_LevelUp_ActivityOver extends ActionEventBase {
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put((byte) 0);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

}
