package sophia.mmorpg.player.mount.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mount_IsOnMount extends ActionEventBase {
	private boolean isOnMount;

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (isOnMount) {
			buffer.put((byte) 1);
		} else {
			buffer.put((byte) 0);
		}
		return buffer;
	}

	public boolean isOnMount() {
		return isOnMount;
	}

	public void setOnMount(boolean isOnMount) {
		this.isOnMount = isOnMount;
	}

}
