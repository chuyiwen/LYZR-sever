package sophia.mmorpg.player.team.actionEvent.activity;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_PlayerTeamBoss_Show extends ActionEventBase {
	

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

}