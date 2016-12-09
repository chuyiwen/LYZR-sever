package newbee.morningGlory.mmorpg.player.activity.event;

import newbee.morningGlory.mmorpg.player.activity.mgr.AdvancedMgr;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Advanced_List extends ActionEventBase {
	private AdvancedMgr advancedMgr;

	public G2C_Advanced_List(){
		ziped = (byte)1;
	}
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		advancedMgr.writeInfoToClient(buffer);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public AdvancedMgr getAdvancedMgr() {
		return advancedMgr;
	}

	public void setAdvancedMgr(AdvancedMgr advancedMgr) {
		this.advancedMgr = advancedMgr;
	}

}
