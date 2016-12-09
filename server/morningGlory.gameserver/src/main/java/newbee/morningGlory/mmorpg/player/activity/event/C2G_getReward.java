package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_getReward extends ActionEventBase {
	private byte type;
	private String giftRefId;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		type = buffer.get();
		giftRefId = getString(buffer);
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getGiftRefId() {
		return giftRefId;
	}

	public void setGiftRefId(String giftRefId) {
		this.giftRefId = giftRefId;
	}

}
