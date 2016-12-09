package sophia.mmorpg.player.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_QST_QuestInstanceTrans extends ActionEventBase {
	private String questId;
	private String gameInstanceId;

	public C2G_QST_QuestInstanceTrans() {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		setQuestId(getString(buffer));
		setGameInstanceId(getString(buffer));
	}

	public void setQuestId(String questId) {
		this.questId = questId;
	}

	public String getQuestId() {
		return questId;
	}

	public String getGameInstanceId() {
		return gameInstanceId;
	}

	public void setGameInstanceId(String gameInstanceId) {
		this.gameInstanceId = gameInstanceId;
	}
}
