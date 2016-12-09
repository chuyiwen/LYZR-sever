package sophia.mmorpg.player.chat.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_System_Prompt extends ActionEventBase {
	// 发送内容
	private String msg;

	// 消息的位置
	private byte position;

	// 特效的类型
	private byte specialEffectsType;

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, msg);
		buffer.put(position);
		buffer.put(specialEffectsType);
		return buffer;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public void setSpecialEffectsType(byte specialEffectsType) {
		this.specialEffectsType = specialEffectsType;
	}

}
