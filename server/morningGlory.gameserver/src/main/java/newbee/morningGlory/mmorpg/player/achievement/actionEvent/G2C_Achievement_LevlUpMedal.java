package newbee.morningGlory.mmorpg.player.achievement.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Achievement_LevlUpMedal extends ActionEventBase {
	private String refId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, refId);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

}
