package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_QST_GetDailyQuestList extends ActionEventBase {

	public C2G_QST_GetDailyQuestList() {
		this.actionEventId = DailyQuestActionEventDefines.C2G_QST_GetDailyQuestList;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

}
