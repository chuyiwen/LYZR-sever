package sophia.mmorpg.player.friend.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_AddOnePlayer extends ActionEventBase{
	private String playerName;
	private byte groupType;
	
	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		groupType = buffer.get();
		playerName = getString(buffer);
	}

	public String getPlayerName() {
		return playerName;
	}

	public byte getGroupType() {
		return groupType;
	}
}
