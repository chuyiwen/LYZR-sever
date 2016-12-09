package newbee.morningGlory.mmorpg.player.activity.fund.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Fund_GetReward extends ActionEventBase {

	private byte type;
	private int day;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		this.type = arg0.get();
		this.day = arg0.getInt();
	}

	public byte getType() {
		return type;
	}

	public int getDay() {
		return day;
	}

}
