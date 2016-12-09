package sophia.mmorpg.player.team.actionEvent.leader;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_NotifyLeaderTeamActionEvent extends ActionEventBase {

	private byte playerActionType;// 拒绝加入/队员离队

	public void setRefuseJoin() {
		this.playerActionType = 1;
	}

	public void setLeaveTeam() {
		this.playerActionType = 2;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(playerActionType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

}
