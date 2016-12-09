package newbee.morningGlory.mmorpg.gameInstance.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_GameInstanceEnter extends ActionEventBase {

	private String gameInstanceId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, gameInstanceId);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		gameInstanceId = getString(buffer);
	}

	public String getGameInstanceId() {
		return gameInstanceId;
	}

	public void setGameInstanceId(String gameInstanceId) {
		this.gameInstanceId = gameInstanceId;
	}

	@Override
	public String getName() {
		return "进入副本";
	}
}
