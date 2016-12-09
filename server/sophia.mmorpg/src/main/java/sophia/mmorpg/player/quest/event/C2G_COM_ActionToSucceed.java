package sophia.mmorpg.player.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_COM_ActionToSucceed extends ActionEventBase {
	
	private byte eventType; // 1：打开仓库

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(eventType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		eventType = buffer.get();
	}

	public byte getEventType() {
		return eventType;
	}

	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}

}
