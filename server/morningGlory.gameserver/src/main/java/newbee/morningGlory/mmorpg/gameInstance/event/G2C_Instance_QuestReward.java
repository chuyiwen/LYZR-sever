package newbee.morningGlory.mmorpg.gameInstance.event;

import java.util.List;

import newbee.morningGlory.mmorpg.player.gameInstance.quest.MGGameInstanceQuest;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Instance_QuestReward extends ActionEventBase {
	private boolean absent;
	private long time;
	private int count;
	
	private List<MGGameInstanceQuest> questList;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		
		buffer.putLong(time);
		if (absent){
			buffer.put((byte)1);
		}
		else{
			buffer.put((byte)0);
		}
		buffer.put((byte)count);
		for (MGGameInstanceQuest quest : questList){
			if (quest.getQuestCourse().wasCompleted()){
				putString(buffer, quest.getQuestRef().getId());
			}
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public List<MGGameInstanceQuest> getQuestList() {
		return questList;
	}

	public void setQuestList(List<MGGameInstanceQuest> questList) {
		this.questList = questList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isAbsent() {
		return absent;
	}

	public void setAbsent(boolean absent) {
		this.absent = absent;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
