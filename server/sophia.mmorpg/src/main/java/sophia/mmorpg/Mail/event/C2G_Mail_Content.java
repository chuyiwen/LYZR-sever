package sophia.mmorpg.Mail.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Mail_Content extends ActionEventBase{
	private String mailId;
	@Override
	public void unpackBody(IoBuffer buffer) {
		mailId = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}
}
