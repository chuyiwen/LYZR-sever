package sophia.mmorpg.player.team.actionEvent.broadcast;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

/**
 * 广播队友死亡
 * 
 * @author Administrator
 * 
 */
public class G2C_Broadcast_TeamMemberDeadActionEvent extends ActionEventBase {
	private Player deadTeamMember;

	public void setDeadTeamMember(Player deadTeamMember) {
		this.deadTeamMember = deadTeamMember;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, this.deadTeamMember.getId());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

}
