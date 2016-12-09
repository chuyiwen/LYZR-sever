package newbee.morningGlory.mmorpg.player.activity.fund.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Fund_GetReward extends ActionEventBase {

	private byte type;
	private byte result;// //成功失败 0失败 1成功

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		arg0.put(type);
		arg0.put(result);
		return arg0;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setResult(byte result) {
		this.result = result;
	}
	
	
	

}