package newbee.morningGlory.mmorpg.operatActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 
 * Copyright (c) 2012 by 游爱.
 * 
 * @author 梁广 Create on 2013-10-14 下午7:35:41
 * 
 * @version 1.0
 */
public class G2C_OA_ClosedActivityEvent extends ActionEventBase {

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putShort(type);
		return buffer;
	}

	private short type;

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

}
