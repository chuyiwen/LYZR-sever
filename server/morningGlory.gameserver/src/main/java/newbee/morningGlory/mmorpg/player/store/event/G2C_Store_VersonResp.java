package newbee.morningGlory.mmorpg.player.store.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Store_VersonResp extends ActionEventBase {
	private int verson;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(verson);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public int getVerson() {
		return verson;
	}

	public void setVerson(int verson) {
		this.verson = verson;
	}

}