package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_OT_ShowOnLineTimer extends ActionEventBase {
	private String refId;
	private int remainTime;
	private byte state;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, refId);
		buffer.putInt(remainTime);
		buffer.put(state);
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

	public int getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(int remainTime) {
		this.remainTime = remainTime;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

}
