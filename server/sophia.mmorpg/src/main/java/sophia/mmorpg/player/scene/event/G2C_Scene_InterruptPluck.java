package sophia.mmorpg.player.scene.event;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Scene_InterruptPluck extends ActionEventBase{
	private static Logger logger = Logger.getLogger(G2C_Scene_InterruptPluck.class);
	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		logger.debug("被打断通知");
		return null;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		
	}

}
