package sophia.mmorpg.player.team.actionEvent.create;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_PlayerTeam_CreateEvent extends ActionEventBase {
	private byte createSuccee = 0;
	private String teamId = "";

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(createSuccee);
		putString(buffer, teamId);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public byte getCreateSuccee() {
		return createSuccee;
	}

	public void setCreateSuccee(byte createSuccee) {
		this.createSuccee = createSuccee;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

}