package newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Talisman_GetQuestReward extends ActionEventBase {
	private byte type;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		type = buffer.get();
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

}