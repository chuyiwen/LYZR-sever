package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Union_ApplyJoinUnion extends ActionEventBase {
	private byte UIrefreshType;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(UIrefreshType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setUIrefreshType(byte uIrefreshType) {
		UIrefreshType = uIrefreshType;
	}

}
