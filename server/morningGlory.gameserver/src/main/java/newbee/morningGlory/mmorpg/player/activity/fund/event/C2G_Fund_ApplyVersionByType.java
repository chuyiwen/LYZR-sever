package newbee.morningGlory.mmorpg.player.activity.fund.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Fund_ApplyVersionByType extends ActionEventBase {
	private byte type;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

		this.type = arg0.get();
		// TODO Auto-generated method stub

	}

	public byte getType() {
		return type;
	}

}
