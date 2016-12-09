package newbee.morningGlory.mmorpg.player.activity.fund.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Fund_ReturnVersion extends ActionEventBase {

	private byte type;
	private int version;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		arg0.put(type);
		arg0.putInt(version);
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
