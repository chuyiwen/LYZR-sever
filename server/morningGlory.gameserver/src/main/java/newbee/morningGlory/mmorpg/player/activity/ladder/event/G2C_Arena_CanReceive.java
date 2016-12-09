package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Arena_CanReceive extends ActionEventBase{
	private byte canReceive;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(canReceive);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setCanReceive(byte canReceive) {
		this.canReceive = canReceive;
	}

}
