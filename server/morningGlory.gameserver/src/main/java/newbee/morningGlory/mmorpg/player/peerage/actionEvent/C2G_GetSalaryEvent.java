package newbee.morningGlory.mmorpg.player.peerage.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_GetSalaryEvent extends ActionEventBase {
	@Override
	public IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "领取俸禄";
	}

}
