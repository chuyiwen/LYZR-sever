package sophia.mmorpg.player.chat.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Chat_System extends ActionEventBase {
	// 发送内容
	private String msg;
	// 系统公告的类型
	private byte type;

	@Override
	public void unpackBody(IoBuffer buffer) {
		msg = getString(buffer)  +","+ buffer.get();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// 发送内容
		putString(buffer, msg);
		// 系统公告的类型
		buffer.put(type);
		return buffer;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}
	
}
