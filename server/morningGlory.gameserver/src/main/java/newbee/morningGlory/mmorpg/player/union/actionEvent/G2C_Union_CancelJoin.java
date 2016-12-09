package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Union_CancelJoin extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put((byte)1);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

}
