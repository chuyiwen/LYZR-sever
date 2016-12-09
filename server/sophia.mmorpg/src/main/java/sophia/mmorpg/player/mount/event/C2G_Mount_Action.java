package sophia.mmorpg.player.mount.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Mount_Action extends ActionEventBase {

	// 动作（0:上马,1:下马）
	private int actionType;

	@Override
	public void unpackBody(IoBuffer buffer) {
		actionType = buffer.getInt();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(actionType);
		return buffer;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public String getName(){
		return "动作";
	}
}
