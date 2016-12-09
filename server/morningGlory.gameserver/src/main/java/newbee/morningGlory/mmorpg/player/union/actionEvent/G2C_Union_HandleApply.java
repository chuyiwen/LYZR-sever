package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Union_HandleApply extends ActionEventBase{
	private static Logger logger = Logger.getLogger(G2C_Union_ApplyJoinUnion.class);
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if(logger.isDebugEnabled()){
			logger.debug("处理申请返回");
		}
		buffer.put((byte)1);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		
	}

}
