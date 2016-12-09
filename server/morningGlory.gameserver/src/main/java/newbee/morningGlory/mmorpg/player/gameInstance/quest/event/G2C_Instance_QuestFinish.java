package newbee.morningGlory.mmorpg.player.gameInstance.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Instance_QuestFinish extends ActionEventBase {
	private String questId;
	private int rewardType;
	
	public int getRewardType() {
		return rewardType;
	}

	public void setRewardType(int rewardType) {
		this.rewardType = rewardType;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, questId);
		buffer.put((byte)rewardType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public String getQuestId() {
		return questId;
	}

	public void setQuestId(String questId) {
		this.questId = questId;
	}

}