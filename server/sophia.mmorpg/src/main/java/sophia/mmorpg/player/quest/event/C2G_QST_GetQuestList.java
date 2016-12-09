package sophia.mmorpg.player.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_QST_GetQuestList extends ActionEventBase {
	private byte message;
	
	public C2G_QST_GetQuestList() {
		this.actionEventId = QuestActionEventDefines.C2G_QST_GetQuestList;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public int getMessage() {
		return message;
	}

	public void setMessage(byte message) {
		this.message = message;
	}

	

}
