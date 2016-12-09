package newbee.morningGlory.mmorpg.player.castleWar.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_CastleWar_JoinWar extends ActionEventBase {
	private byte result;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(result);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}
}