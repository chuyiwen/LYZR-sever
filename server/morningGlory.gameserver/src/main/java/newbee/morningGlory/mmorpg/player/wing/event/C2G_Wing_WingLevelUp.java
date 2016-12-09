package newbee.morningGlory.mmorpg.player.wing.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Wing_WingLevelUp extends ActionEventBase {
	private String itemRefId;

	private byte itemNum;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		itemRefId = getString(buffer);
		itemNum = buffer.get();
	}

	public String getItemRefId() {
		return itemRefId;
	}

	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public byte getItemNum() {
		return itemNum;
	}

	public void setItemNum(byte itemNum) {
		this.itemNum = itemNum;
	}

}
