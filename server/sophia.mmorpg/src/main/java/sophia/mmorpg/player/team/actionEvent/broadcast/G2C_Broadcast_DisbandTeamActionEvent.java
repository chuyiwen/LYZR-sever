package sophia.mmorpg.player.team.actionEvent.broadcast;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 广播队长解散队伍
 * 
 * 
 */
public class G2C_Broadcast_DisbandTeamActionEvent extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}
}
