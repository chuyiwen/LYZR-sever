package sophia.mmorpg.player.chat.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Chat_Get_ReceiverId extends ActionEventBase {

	// 玩家姓名
	private String receiverName;
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		this.receiverName = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, receiverName);
		return buffer;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	
	@Override
	public String getName() {
		return "获取玩家ID";
	}
	
}
