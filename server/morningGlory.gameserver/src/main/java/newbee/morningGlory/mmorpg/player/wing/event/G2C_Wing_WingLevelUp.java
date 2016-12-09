package newbee.morningGlory.mmorpg.player.wing.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Wing_WingLevelUp extends ActionEventBase {
	private String crtWingRefId;

	private byte critMultipleType;

	private long exp;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, crtWingRefId);
		buffer.put(critMultipleType);
		buffer.putLong(exp);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public String getCrtWingRefId() {
		return crtWingRefId;
	}

	public void setCrtWingRefId(String crtWingRefId) {
		this.crtWingRefId = crtWingRefId;
	}

	public byte getCritMultipleType() {
		return critMultipleType;
	}

	public void setCritMultipleType(byte critMultipleType) {
		this.critMultipleType = critMultipleType;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

}
