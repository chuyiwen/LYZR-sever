package newbee.morningGlory.mmorpg.player.activity.mining.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mining_RemainRrfreshTime extends ActionEventBase{
	private byte mineType;
	
	private int remainRefreshSeconds;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(mineType);
		
		buffer.putInt(remainRefreshSeconds);
		
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		
	}

	public void setMineType(byte mineType) {
		this.mineType = mineType;
	}

	public void setRemainRefreshSeconds(int remainRefreshSeconds) {
		this.remainRefreshSeconds = remainRefreshSeconds;
	}
	
}
