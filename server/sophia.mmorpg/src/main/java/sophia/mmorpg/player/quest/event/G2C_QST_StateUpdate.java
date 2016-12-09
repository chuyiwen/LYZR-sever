package sophia.mmorpg.player.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_QST_StateUpdate extends ActionEventBase{
	private String questId;
	private int state;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, questId);
		buffer.put((byte)state);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		questId = getString(buffer);
		state = buffer.get();
	}

	public String getQuestId() {
		return questId;
	}

	public void setQuestId(String questId) {
		this.questId = questId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
