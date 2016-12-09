package sophia.mmorpg.Mail.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mail_Pickup_LeftTime extends ActionEventBase{
	private String mailId;
	private long leftTime;
	@Override
	public void unpackBody(IoBuffer buffer) {
		
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, mailId);
		buffer.putLong(leftTime);
		return buffer;
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	public long getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(long leftTime) {
		this.leftTime = leftTime;
	}
}
