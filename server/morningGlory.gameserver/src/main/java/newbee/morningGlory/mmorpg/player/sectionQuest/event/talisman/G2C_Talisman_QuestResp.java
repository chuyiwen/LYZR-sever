package newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Talisman_QuestResp extends ActionEventBase {
	private byte type;
	private byte level;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(type);
		buffer.put(level);
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

	public int getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

}