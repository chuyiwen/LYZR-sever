package sophia.mmorpg.player.team.actionEvent.broadcast;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

/**
 * 广播队长踢出某个队友
 * 
 * @author Administrator
 * 
 */
public class G2C_Broadcast_TeamLeaderKickedOutActionEvent extends ActionEventBase {
	private Player kickedOutTeamLeader;

	public void setKickedOutTeamLeader(Player kickedOutTeamLeader) {
		this.kickedOutTeamLeader = kickedOutTeamLeader;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, this.kickedOutTeamLeader.getId());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

}
