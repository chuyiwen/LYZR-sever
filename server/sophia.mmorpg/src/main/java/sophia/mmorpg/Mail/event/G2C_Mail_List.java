package sophia.mmorpg.Mail.event;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.Mail.Mail;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-11 下午5:45:01
 * @version 1.0
 */
public class G2C_Mail_List extends ActionEventBase {
	private List<Mail> mails;

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (mails==null||mails.size()==0) {
			buffer.putInt(0);
		}else{
			buffer.putInt(mails.size());
			for (Mail mail : mails) {
				mail.writeToBufferList(buffer);
			}
		}
		return buffer;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}
}
