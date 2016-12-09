package sophia.mmorpg.player.team.actionEvent.activity;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_PlayerTeamBoss_RequestTime extends ActionEventBase {
	private byte type;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		type = buffer.get();
	}

	public byte getType() {
		return type;
	}
}