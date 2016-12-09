package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_QST_DailyQuestSubmit extends ActionEventBase {
	private String submitQuestId;
	private int rewardLevel;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		setSubmitQuestId(getString(buffer));
		setRewardLevel(buffer.get());
	}

	public String getSubmitQuestId() {
		return submitQuestId;
	}

	public void setSubmitQuestId(String submitQuestId) {
		this.submitQuestId = submitQuestId;
	}

	public int getRewardLevel() {
		return rewardLevel;
	}

	public void setRewardLevel(int rewardLevel) {
		this.rewardLevel = rewardLevel;
	}

}
