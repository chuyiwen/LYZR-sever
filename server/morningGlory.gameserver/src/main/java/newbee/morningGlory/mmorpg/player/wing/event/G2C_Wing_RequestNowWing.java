package newbee.morningGlory.mmorpg.player.wing.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Wing_RequestNowWing extends ActionEventBase {
	private String wingRefId;
	private long crtExp;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, wingRefId);
		buffer.putLong(crtExp);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public String getWingRefId() {
		return wingRefId;
	}

	public void setWingRefId(String wingRefId) {
		this.wingRefId = wingRefId;
	}

	public long getCrtExp() {
		return crtExp;
	}

	public void setCrtExp(long crtExp) {
		this.crtExp = crtExp;
	}

}
