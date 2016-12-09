package newbee.morningGlory.mmorpg.player.activity.event;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_OT_ShowDailyOnLineTimer extends ActionEventBase {
	private int time;
	private List<String> rewardList;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(time);
		buffer.put((byte) rewardList.size());
		for (String refId : rewardList) {
			putString(buffer, refId);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public List<String> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<String> rewardList) {
		this.rewardList = rewardList;
	}

}
