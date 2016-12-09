package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Ladder_Athletics extends ActionEventBase{
	private byte result;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(result);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		
	}

	public void setResult(byte result) {
		this.result = result;
	}

}
