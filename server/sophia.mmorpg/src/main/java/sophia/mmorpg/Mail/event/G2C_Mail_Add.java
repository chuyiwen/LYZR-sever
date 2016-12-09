package sophia.mmorpg.Mail.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.Mail.Mail;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-13 下午4:27:43
 * @version 1.0
 */
public class G2C_Mail_Add extends ActionEventBase {
	private Mail mail;

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		mail.writeToBufferList(buffer);
		return buffer;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}
}
