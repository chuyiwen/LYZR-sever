package sophia.mmorpg.player.team.actionEvent.info;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.team.actionEvent.PlayerTeamStateType;

public class G2C_PlayerInfoActionEvent extends ActionEventBase {

	private Player invitedPlayer;// 被邀请者
	private Player invitePlayer;// 邀请者

	public void setInvitedPlayer(Player invitedPlayer) {
		this.invitedPlayer = invitedPlayer;
	}

	public void setInvitePlayer(Player invitePlayer) {
		this.invitePlayer = invitePlayer;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (this.invitedPlayer.getPlayerTeamComponent().hasTeam()) {
			if (this.invitedPlayer.getPlayerTeamComponent().getTeam().equals(this.invitePlayer.getPlayerTeamComponent().getTeam())) {
				buffer.put(PlayerTeamStateType.TEAM_STATE_TYPE_YOUR_TEAM);
			} else {
				buffer.put(PlayerTeamStateType.TEAM_STATE_TYPE_OTHER_TEAM);
			}
		} else {
			buffer.put(PlayerTeamStateType.TEAM_STATE_TYPE_NO_TEAM);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

}
