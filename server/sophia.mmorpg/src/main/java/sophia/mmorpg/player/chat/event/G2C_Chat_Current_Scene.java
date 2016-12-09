package sophia.mmorpg.player.chat.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_Chat_Current_Scene extends ActionEventBase {
	// 发送内容的玩家
	private Player player;
	// 发送内容
	private String msg;

	private long time;
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		// 本地客户端调试用
		msg = getString(buffer) + "," + getString(buffer) + "," + getString(buffer) + "," + getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// 发送者名字
		putString(buffer, player.getName());
		// 发送者id
		putString(buffer, player.getId());
		// 发送内容
		putString(buffer, msg);
		// 发送者的场景ID
		putString(buffer, player.getCrtScene() != null ? player.getCrtScene().getId() : "-1");
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

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public String getName() {
		return "当前";
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
