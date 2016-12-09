package sophia.mmorpg.player.team.actionEvent.create;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_PlayerTeam_CreateEvent extends ActionEventBase {
	private int levelChoice;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		levelChoice = buffer.get();
	}

	public int getLevelChoice() {
		return levelChoice;
	}

	public void setLevelChoice(int levelChoice) {
		this.levelChoice = levelChoice;
	}
	
}