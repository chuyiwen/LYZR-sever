package sophia.mmorpg.player.mount.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.mount.Mount;

public class G2C_Mount_List extends ActionEventBase {

	private Mount crtMount;
	private boolean isOnMount;

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// 当前坐骑RefId
		putString(buffer, crtMount.getCrtRefId());
		// 当前的经验
		buffer.putLong(crtMount.getExp());
		// 坐骑ID
		putString(buffer, crtMount.getId());
		// 坐骑状态 
		if (isOnMount) {
			buffer.put((byte) 1);
		} else {
			buffer.put((byte) 0);
		}
		return buffer;
	}

	public Mount getCrtMount() {
		return crtMount;
	}

	public void setCrtMount(Mount crtMount) {
		this.crtMount = crtMount;
	}

	public boolean isOnMount() {
		return isOnMount;
	}

	public void setOnMount(boolean isOnMount) {
		this.isOnMount = isOnMount;
	}

}