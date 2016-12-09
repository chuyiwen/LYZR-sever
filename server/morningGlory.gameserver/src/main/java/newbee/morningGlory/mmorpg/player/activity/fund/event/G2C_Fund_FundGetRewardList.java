package newbee.morningGlory.mmorpg.player.activity.fund.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Fund_FundGetRewardList extends ActionEventBase {

	private byte type;
	private byte getRecord[];
	private int count;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		arg0.put(type);
		arg0.putInt(count);
		for (int i = 0; i < count; i++) {
			if (getRecord[i] == 0 && i < count - 1)
				arg0.put((byte)2);
			else
				arg0.put(getRecord[i]);
		}
		return arg0;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setGetRecord(byte[] getRecord) {
		this.getRecord = getRecord;
	}

	public void setCount(int count) {
		this.count = count;
		// TODO Auto-generated method stub

	}

}
