package newbee.morningGlory.mmorpg.player.castleWar.event;

import newbee.morningGlory.MorningGloryContext;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_CastleWar_OpenServer extends ActionEventBase {
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		long serverOpenTime = MorningGloryContext.getServerOpenTime();
		buffer.putLong(serverOpenTime/1000);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}
}
