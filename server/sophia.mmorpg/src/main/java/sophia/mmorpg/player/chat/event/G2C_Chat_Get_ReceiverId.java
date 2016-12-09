package sophia.mmorpg.player.chat.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_Chat_Get_ReceiverId extends ActionEventBase {

	// 接受内容的玩家
	private Player receiver;
	
	private String s;

	@Override
	public void unpackBody(IoBuffer buffer) {
		s = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// 接受内容的玩家ID
		putString(buffer, receiver.getId());
		// 性别
		buffer.put(MGPropertyAccesser.getGender(receiver.getProperty()));
		return buffer;
	}

	public void setReceiver(Player player) {
		this.receiver = player;
	}

	public String getS() {
		return s;
	}

}
