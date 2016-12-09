package sophia.mmorpg.player.team.actionEvent.info;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.team.PlayerTeam;

public class G2C_PlayerTeam_InfomationEvent extends ActionEventBase {
	
	private List<PlayerTeam> teams = new ArrayList<>();
	
	private PlayerTeam playerTeam = null;
	
	public void setTeams(List<PlayerTeam> teams) {
		this.teams = teams;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (playerTeam == null) {
			putString(buffer, "");
		} else {
			putString(buffer, playerTeam.getId());
		}
		buffer.putShort((short) teams.size());
		for (PlayerTeam playerTeam : teams) {
			putString(buffer, playerTeam.getTeamLeader().getName());
			putString(buffer, playerTeam.getId());
			buffer.put((byte)playerTeam.getTeamLevelLimit());
			buffer.put((byte)playerTeam.getTeamMemberNum());
			buffer.put((byte)playerTeam.getTeamAverageLevel());
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public void setPlayerTeam(PlayerTeam playerTeam) {
		this.playerTeam = playerTeam;
	}

}