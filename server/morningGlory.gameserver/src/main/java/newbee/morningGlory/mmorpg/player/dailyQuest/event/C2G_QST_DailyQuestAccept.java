package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_QST_DailyQuestAccept extends ActionEventBase {
	private String acceptQuest;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		setAcceptQuest(getString(buffer));
	}

	public String getAcceptQuest() {
		return acceptQuest;
	}

	public void setAcceptQuest(String acceptQuest) {
		this.acceptQuest = acceptQuest;
	}

}
