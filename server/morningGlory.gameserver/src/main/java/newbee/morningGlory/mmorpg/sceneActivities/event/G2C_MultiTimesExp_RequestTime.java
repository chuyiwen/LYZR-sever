package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_MultiTimesExp_RequestTime extends ActionEventBase{
	private long timeToStar;      //距离活动开始的时间（活动未开始大于0  开始后等于0）
	private long  timeToEnd;      // 距离活动结束的剩余时间（活动开始大于0  活动结束等于0）
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putLong(timeToStar);
		buffer.putLong(timeToEnd);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	public long getTimeToStar() {
		return timeToStar;
	}

	public void setTimeToStar(long timeToStar) {
		this.timeToStar = timeToStar;
	}

	public long getTimeToEnd() {
		return timeToEnd;
	}

	public void setTimeToEnd(long timeToEnd) {
		this.timeToEnd = timeToEnd;
	}
}
