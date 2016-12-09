package sophia.mmorpg.player.team.actionEvent.info;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.team.PlayerTeam;

public class G2C_TeamMenber_Detail extends ActionEventBase {
	private PlayerTeam playerTeam;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		if (playerTeam == null) {
			return buffer;
		}
		CopyOnWriteArrayList<String> members = playerTeam.getMembers();
		int size = members.size();
		buffer.put((byte) (size));
		Player teamLeader = playerTeam.getTeamLeader();
		buffer.put(getPlayerState(teamLeader));
		putString(buffer, teamLeader.getId());
		buffer.putInt(teamLeader.getHPMax());
		buffer.putInt(teamLeader.getHP());
		putString(buffer, teamLeader.getSceneRefId());
		buffer.putInt(teamLeader.getCrtPosition().getX());
		buffer.putInt(teamLeader.getCrtPosition().getY());
		for (String playerId : members) {
			Player player = playerManager.getPlayer(playerId);
			if(player!=null && !player.getId().equals(teamLeader.getId())){
				buffer.put(getPlayerState(player));
				putString(buffer, player.getId());
				buffer.putInt(player.getHPMax());
				buffer.putInt(player.getHP());
				putString(buffer, player.getSceneRefId());
				buffer.putInt(player.getCrtPosition().getX());
				buffer.putInt(player.getCrtPosition().getY());
			}
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}
	
	public byte getPlayerState(Player player) {
		if (player.isDead()) {
			return 3;
		}
		if (player.isOnline()) {
			return 1;
		} else {
			return 2;
		}
	}

	public PlayerTeam getPlayerTeam() {
		return playerTeam;
	}

	public void setPlayerTeam(PlayerTeam playerTeam) {
		this.playerTeam = playerTeam;
	}

}