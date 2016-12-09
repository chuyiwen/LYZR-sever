package newbee.morningGlory.mmorpg.player.activity.event;

import newbee.morningGlory.mmorpg.player.activity.mgr.SignMgr;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_SignIn_List extends ActionEventBase {
	private SignMgr signMgr;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		signMgr.writeSignListInfo(buffer);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public SignMgr getSignMgr() {
		return signMgr;
	}

	public void setSignMgr(SignMgr signMgr) {
		this.signMgr = signMgr;
	}

}
