package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Arena_UpdateChallengeCDTime extends ActionEventBase {
	private int cdtime;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(cdtime);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setCdtime(int cdtime) {
		this.cdtime = cdtime;
	}

}
