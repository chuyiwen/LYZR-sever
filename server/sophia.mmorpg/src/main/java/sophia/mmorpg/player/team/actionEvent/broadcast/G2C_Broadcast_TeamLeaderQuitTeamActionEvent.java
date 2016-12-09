package sophia.mmorpg.player.team.actionEvent.broadcast;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

public class G2C_Broadcast_TeamLeaderQuitTeamActionEvent extends ActionEventBase {
	private Player newTeamLeader;
	private Player quitTeamLeader;

	public void setNewTeamLeader(Player newTeamLeader) {
		this.newTeamLeader = newTeamLeader;
	}

	public void setQuitTeamLeader(Player quitTeamLeader) {
		this.quitTeamLeader = quitTeamLeader;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, this.quitTeamLeader.getId());
		putString(buffer, this.newTeamLeader.getId());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

}
