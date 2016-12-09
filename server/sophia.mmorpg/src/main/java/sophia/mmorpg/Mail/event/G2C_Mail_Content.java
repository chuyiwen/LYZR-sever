package sophia.mmorpg.Mail.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.Mail.Mail;

public class G2C_Mail_Content extends ActionEventBase{
	private Mail mail;
	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		mail.writeToBuffer(buffer);
		return buffer;
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

}
