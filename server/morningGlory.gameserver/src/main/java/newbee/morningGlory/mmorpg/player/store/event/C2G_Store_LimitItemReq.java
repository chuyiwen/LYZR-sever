package newbee.morningGlory.mmorpg.player.store.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Store_LimitItemReq extends ActionEventBase {
	private String storeType;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		putString(buffer,storeType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		storeType = getString(buffer);
	}

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}


}