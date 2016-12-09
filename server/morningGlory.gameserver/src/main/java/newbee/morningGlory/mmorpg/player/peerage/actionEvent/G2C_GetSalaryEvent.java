package newbee.morningGlory.mmorpg.player.peerage.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_GetSalaryEvent extends ActionEventBase {
	private byte success;// 0 领取失败  	1 领取成功

	@Override
	public IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		buffer.put(success);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "领取俸禄返回";
	}

	public byte getSuccess() {
		return success;
	}

	public void setSuccess(byte success) {
		this.success = success;
	}

}
