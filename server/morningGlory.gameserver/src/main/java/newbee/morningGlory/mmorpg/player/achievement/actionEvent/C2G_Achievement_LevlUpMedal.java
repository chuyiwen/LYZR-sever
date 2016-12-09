package newbee.morningGlory.mmorpg.player.achievement.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Achievement_LevlUpMedal extends ActionEventBase {
	private byte position;
	private String refId;
	private String id;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		position = buffer.get();
		refId = getString(buffer);
		id = getString(buffer);
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getId() {
		return id;
	}

}
