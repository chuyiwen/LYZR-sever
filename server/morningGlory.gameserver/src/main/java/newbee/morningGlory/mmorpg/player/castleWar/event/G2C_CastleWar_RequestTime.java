package newbee.morningGlory.mmorpg.player.castleWar.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_CastleWar_RequestTime extends ActionEventBase {
	private long timeToStart;
	private long timeToEnd;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putLong(timeToStart);
		buffer.putLong(timeToEnd);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public long getTimeToStart() {
		return timeToStart;
	}

	public void setTimeToStart(long timeToStart) {
		this.timeToStart = timeToStart;
	}

	public long getTimeToEnd() {
		return timeToEnd;
	}

	public void setTimeToEnd(long timeToEnd) {
		this.timeToEnd = timeToEnd;
	}

}