package newbee.morningGlory.mmorpg.player.activity.digs.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Digs_Type extends ActionEventBase {
	
	private byte type;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		
		return null;
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
