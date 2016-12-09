package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_QST_DailyStartLevel extends ActionEventBase {
	
	private MGDailyQuest dailyQuest;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, dailyQuest.getDailyQuestRef().getId());
		buffer.put(dailyQuest.getStartLevel());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public MGDailyQuest getDailyQuest() {
		return dailyQuest;
	}

	public void setDailyQuest(MGDailyQuest dailyQuest) {
		this.dailyQuest = dailyQuest;
	}

}
