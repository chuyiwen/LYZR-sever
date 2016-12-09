package sophia.mmorpg.player.mount.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.mount.Mount;

public class G2C_Mount_Feed extends ActionEventBase {
	private Mount crtMount;

	private int baoJi = 1;

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// 当前坐骑RefId
		putString(buffer, crtMount.getCrtRefId());
		// 当前的经验
		buffer.putLong(crtMount.getExp());
		// 暴击 (1:没有暴击 2:2倍 3:3倍)
		buffer.putInt(baoJi);
		return buffer;
	}

	public Mount getCrtMount() {
		return crtMount;
	}

	public void setCrtMount(Mount crtMount) {
		this.crtMount = crtMount;
	}

	public int getBaoJi() {
		return baoJi;
	}

	public void setBaoJi(int baoJi) {
		this.baoJi = baoJi;
	}

}
