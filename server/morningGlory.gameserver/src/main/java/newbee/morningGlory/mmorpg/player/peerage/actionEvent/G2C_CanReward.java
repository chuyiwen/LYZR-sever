package newbee.morningGlory.mmorpg.player.peerage.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_CanReward extends ActionEventBase {
	private byte canGet;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		buffer.put(canGet);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public byte getCanGet() {
		return canGet;
	}

	public void setCanGet(byte canGet) {
		this.canGet = canGet;
	}

}
