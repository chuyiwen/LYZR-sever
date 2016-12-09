package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import java.util.List;

import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_QST_DailyQuestVisibleList extends ActionEventBase {
	private List<MGDailyQuest> questList;
	
	public List<MGDailyQuest> getQuestList() {
		return questList;
	}

	public void setQuestList(List<MGDailyQuest> questList) {
		this.questList = questList;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (questList.size()>0){
			buffer.putInt(questList.size());
			for (MGDailyQuest quest : questList){
				putString(buffer, quest.getDailyQuestRef().getId());
				buffer.putInt(quest.getNowTime());
				buffer.put((byte)quest.getStartLevel());
			}
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
	}

}
