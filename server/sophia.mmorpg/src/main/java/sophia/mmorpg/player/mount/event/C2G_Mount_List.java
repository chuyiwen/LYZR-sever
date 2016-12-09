package sophia.mmorpg.player.mount.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Mount_List extends ActionEventBase {

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	public String getName(){
		return "坐骑状态";
	}
}
