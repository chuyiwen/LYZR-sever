package sophia.mmorpg.player.chat.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_Chat_Private extends ActionEventBase {
	// 发送内容的玩家
	private Player player;
	// 接受内容的玩家
	private Player receiver;
	// 发送内容
	private String msg;
	
	private long time;
	@Override
	public void unpackBody(IoBuffer buffer) {
		msg = getString(buffer)  +","+ getString(buffer) +","+ getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// 发送者名字
		putString(buffer, player.getName());
		// 发送者id
		putString(buffer, player.getId());
		
		// 发送者职业
		buffer.put(player.getProfession());
		
		// 接收者名字
		putString(buffer, receiver.getName());
		// 接收者id
		putString(buffer, receiver.getId());
		// 接受者职业
		buffer.put(receiver.getProfession());
		
		// 发送内容
		putString(buffer, msg);
		// 性别
		buffer.put(MGPropertyAccesser.getGender(player.getProperty()));
		
		buffer.put(MGPropertyAccesser.getGender(receiver.getProperty()));
		// VIP类型
		buffer.put(player.getVipType());
		
		buffer.put(receiver.getVipType());
		
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Player getReceiver() {
		return receiver;
	}

	public void setReceiver(Player receiver) {
		this.receiver = receiver;
	}
	
}
