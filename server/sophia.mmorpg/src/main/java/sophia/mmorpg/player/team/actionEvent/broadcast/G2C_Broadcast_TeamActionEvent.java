package sophia.mmorpg.player.team.actionEvent.broadcast;

import java.util.Collection;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * 广播队友增加或者减少
 * 
 * @author Administrator
 * 
 */
public class G2C_Broadcast_TeamActionEvent extends ActionEventBase {
	private Collection<Player> teamMembers;

	private byte actionType;

	private Player teamLeader;

	public void setActionType(byte actionType) {
		this.actionType = actionType;
	}

	public void setTeamMembers(Collection<Player> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public void setTeamLeader(Player teamLeader) {
		this.teamLeader = teamLeader;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(actionType);
		putString(buffer, teamLeader.getId());
		buffer.put(teamLeader.getProfession());
		buffer.put(MGPropertyAccesser.getGender(teamLeader.getProperty()));
		putString(buffer, teamLeader.getName());
		buffer.putInt(teamLeader.getHP());
		buffer.putInt(teamLeader.getHPMax());
		buffer.putInt(teamMembers.size());
		for (Player teamMember : teamMembers) {
			putString(buffer, teamMember.getId());
			buffer.put(teamMember.getProfession());
			buffer.put(MGPropertyAccesser.getGender(teamMember.getProperty()));
			putString(buffer, teamMember.getName());
			buffer.putInt(teamMember.getHP());
			buffer.putInt(teamMember.getHPMax());
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}
}
