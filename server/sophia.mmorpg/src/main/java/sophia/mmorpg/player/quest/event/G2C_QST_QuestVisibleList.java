package sophia.mmorpg.player.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_QST_QuestVisibleList extends ActionEventBase {
	private String nextQuestId;

	public G2C_QST_QuestVisibleList() {
		this.actionEventId = QuestActionEventDefines.G2C_QST_QuestVisibleList;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		buffer.get();
		nextQuestId = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put((byte) 1);
		putString(buffer, nextQuestId);
		return buffer;
	}

	@Override
	public int getPriority() {
		return Immediately_Priority;
	}

	public void setNextQuestID(String nextQuestId) {
		this.nextQuestId = nextQuestId;
	}
	
	public String getNextQuestID(){
		return this.nextQuestId;
	}

}
