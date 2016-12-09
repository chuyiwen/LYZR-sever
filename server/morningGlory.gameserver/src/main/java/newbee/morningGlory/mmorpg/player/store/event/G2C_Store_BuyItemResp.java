package newbee.morningGlory.mmorpg.player.store.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Store_BuyItemResp extends ActionEventBase {
	private byte temp;


	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(temp);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public byte getTemp() {
		return temp;
	}

	public void setTemp(byte temp) {
		this.temp = temp;
	}

}