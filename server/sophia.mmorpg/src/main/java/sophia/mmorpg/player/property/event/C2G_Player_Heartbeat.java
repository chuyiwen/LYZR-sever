package sophia.mmorpg.player.property.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Player_Heartbeat extends ActionEventBase {
	private long timestamp; // in seconds

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.setTimestamp(buffer.getLong());

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putLong(timestamp);
		return buffer;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
