package sophia.mmorpg.player.team.actionEvent.broadcast;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

/**
 * 广播队长转让
 * 
 */
public class G2C_Broadcast_HandoverTeamActionEvent extends ActionEventBase {
	private Player newTeamLeader;
	private Player oldTeamLeader;

	public void setNewTeamLeader(Player newTeamLeader) {
		this.newTeamLeader = newTeamLeader;
	}

	public void setOldTeamLeader(Player oldTeamLeader) {
		this.oldTeamLeader = oldTeamLeader;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, this.oldTeamLeader.getId());
		putString(buffer, this.newTeamLeader.getId());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

}
