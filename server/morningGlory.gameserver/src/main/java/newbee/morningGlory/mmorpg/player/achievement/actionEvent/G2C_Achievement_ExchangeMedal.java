package newbee.morningGlory.mmorpg.player.achievement.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Achievement_ExchangeMedal extends ActionEventBase{
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(0);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
