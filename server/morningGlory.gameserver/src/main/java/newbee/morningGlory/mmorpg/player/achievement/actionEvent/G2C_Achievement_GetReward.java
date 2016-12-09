package newbee.morningGlory.mmorpg.player.achievement.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Achievement_GetReward extends ActionEventBase {
	private String refId;
	private byte success;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, refId);
		buffer.put(success);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public byte getSuccess() {
		return success;
	}

	public void setSuccess(byte success) {
		this.success = success;
	}

}
