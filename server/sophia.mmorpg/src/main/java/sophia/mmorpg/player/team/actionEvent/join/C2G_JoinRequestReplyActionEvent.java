package sophia.mmorpg.player.team.actionEvent.join;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_JoinRequestReplyActionEvent extends ActionEventBase {
	private String requestPlayerId;
	private byte replyJoinTeamType;

	public String getRequestPlayerId() {
		return requestPlayerId;
	}

	public byte getReplyJoinTeamType() {
		return replyJoinTeamType;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.requestPlayerId = getString(buffer);
		this.replyJoinTeamType = buffer.get();
	}
	
}