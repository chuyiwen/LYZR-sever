package sophia.mmorpg.player.team.actionEvent.join;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_PlayerTeam_JoinRequestEvent extends ActionEventBase {
	private String joinRequestTeamId;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		joinRequestTeamId = getString(buffer);
	}

	public String getJoinRequestTeamId() {
		return joinRequestTeamId;
	}

	public void setJoinRequestTeamId(String joinRequestTeamId) {
		this.joinRequestTeamId = joinRequestTeamId;
	}
	
}