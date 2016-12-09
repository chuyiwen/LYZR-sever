package sophia.mmorpg.player.team.actionEvent.join;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

public class G2C_PlayerTeam_JoinRequestEvent extends ActionEventBase {
	
	private Player requestPlayer;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, this.requestPlayer.getId());
		putString(buffer, this.requestPlayer.getName());
		buffer.putInt(this.requestPlayer.getLevel());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public Player getRequestPlayer() {
		return requestPlayer;
	}

	public void setRequestPlayer(Player requestPlayer) {
		this.requestPlayer = requestPlayer;
	}

}