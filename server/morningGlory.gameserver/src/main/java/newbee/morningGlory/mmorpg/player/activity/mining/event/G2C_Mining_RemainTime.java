package newbee.morningGlory.mmorpg.player.activity.mining.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mining_RemainTime extends ActionEventBase {
	private long startRemainMills;
	private long endRemainMills;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putLong(startRemainMills);
		buffer.putLong(endRemainMills);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
	}


	public void setEndRemainMills(long endRemainMills) {
		this.endRemainMills = endRemainMills;
	}

	public void setStartRemainMills(long startRemainMills) {
		this.startRemainMills = startRemainMills;
	}
	
}
