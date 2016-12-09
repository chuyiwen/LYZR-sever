package sophia.mmorpg.player.team.actionEvent.join;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_JoinRequestReplyActionEvent extends ActionEventBase {
	private byte playerActionType;// 1：对方在30秒内没有响应/2：拒绝加入/3：通过加入
	
	public void setPlayerActionType(byte playerActionType) {
		this.playerActionType = playerActionType;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(playerActionType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}
	
}