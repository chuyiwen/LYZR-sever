package sophia.mmorpg.monster.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Monster_ClearError extends ActionEventBase {
	
	private String monsterId;

	@Override
	public void unpackBody(IoBuffer buffer) {
		monsterId = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}

}
