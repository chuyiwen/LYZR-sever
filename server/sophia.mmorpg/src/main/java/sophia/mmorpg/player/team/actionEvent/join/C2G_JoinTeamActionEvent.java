package sophia.mmorpg.player.team.actionEvent.join;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_JoinTeamActionEvent extends ActionEventBase {
	private String invitePlayerId;
	private byte replyJoinTeamType;

	public String getInvitePlayerId() {
		return invitePlayerId;
	}

	public byte getReplyJoinTeamType() {
		return replyJoinTeamType;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.invitePlayerId = getString(buffer);
		this.replyJoinTeamType = buffer.get();
	}

}
