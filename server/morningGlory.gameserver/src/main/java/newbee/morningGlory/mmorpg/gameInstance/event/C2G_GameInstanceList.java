package newbee.morningGlory.mmorpg.gameInstance.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_GameInstanceList extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	public String getName() {
		return "副本列表";
	}

}
