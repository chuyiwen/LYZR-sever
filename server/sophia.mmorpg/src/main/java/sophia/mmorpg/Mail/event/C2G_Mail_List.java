package sophia.mmorpg.Mail.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-11 下午5:36:50
 * @version 1.0
 */
public class C2G_Mail_List extends ActionEventBase {

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}
}
