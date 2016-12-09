package newbee.morningGlory.mmorpg.player.activity.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Activity_CanRecieve extends ActionEventBase {
	private byte activityType;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(activityType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		activityType = buffer.get();
	}

	public byte getActivityType() {
		return activityType;
	}

}
