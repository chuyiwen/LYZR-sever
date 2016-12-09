package sophia.mmorpg.player.team.actionEvent.info;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 获取玩家的信息（已组队/已经是你的的队员/邀请组队）
 * 
 * @author Administrator
 * 
 */
public class C2G_PlayerInfoActionEvent extends ActionEventBase {
	private String playerId;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.playerId = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, this.getPlayerId());
		return buffer;
	}

	@Override
	public String getName() {
		return "查看玩家组队信息";
	}

}
