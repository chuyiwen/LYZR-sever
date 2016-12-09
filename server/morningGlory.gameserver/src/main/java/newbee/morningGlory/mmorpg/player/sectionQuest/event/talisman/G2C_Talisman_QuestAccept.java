package newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Talisman_QuestAccept extends ActionEventBase {
	private byte type;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(type);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

}
