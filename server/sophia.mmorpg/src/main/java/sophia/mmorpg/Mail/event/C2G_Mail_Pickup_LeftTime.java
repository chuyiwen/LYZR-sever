package sophia.mmorpg.Mail.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Mail_Pickup_LeftTime extends ActionEventBase{
	private String mailId;

	@Override
	public void unpackBody(IoBuffer buffer) {
		mailId = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	public String getMailId() {
		return mailId;
	}
}
