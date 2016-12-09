package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import newbee.morningGlory.mmorpg.ladder.MGLadderSystemMessageFacade;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Arena_UpadateNotice extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		MGLadderSystemMessageFacade.writeLadderSystemInfo(buffer);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}
}
