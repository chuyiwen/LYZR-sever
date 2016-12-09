package sophia.mmorpg.player.team.actionEvent.activity;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_PlayerTeamBoss_PreStart extends ActionEventBase {
	
	private byte type;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(type);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	public void setType(byte type) {
		this.type = type;
	}
}