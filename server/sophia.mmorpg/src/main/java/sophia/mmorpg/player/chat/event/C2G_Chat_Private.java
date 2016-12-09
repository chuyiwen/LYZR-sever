package sophia.mmorpg.player.chat.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Chat_Private extends ActionEventBase {
	private String receiverId;
	private String msg;


	@Override
	public void unpackBody(IoBuffer buffer) {
		this.receiverId = getString(buffer);
		this.msg = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, receiverId);
		putString(buffer, msg);
		return buffer;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
	
	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getName(){
		return "私聊";
	}

}
