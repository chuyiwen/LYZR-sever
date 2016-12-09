package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_Union_Chat extends ActionEventBase {
	// 公会聊天的类型：系统和个人
	private byte type;
	// 发送内容的玩家
	private Player player;
	// 发送内容
	private String msg;
	
	private long time;
	@Override
	public void unpackBody(IoBuffer buffer) {
		msg = getString(buffer)  +","+ getString(buffer) +","+ getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(type);
		
		// 发送者名字
		putString(buffer, player.getName());
		// 发送者id
		putString(buffer, player.getId());
		// 发送内容
		putString(buffer, msg);
		// 性别
		buffer.put(MGPropertyAccesser.getGender(player.getProperty()));
		// VIP类型
		buffer.put(player.getVipType());
		
		buffer.putLong(time);
		return buffer;
	}
	
	public void setSender(Player player) {
		this.player = player;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
