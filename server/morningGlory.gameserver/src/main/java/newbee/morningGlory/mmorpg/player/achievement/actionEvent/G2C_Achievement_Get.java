package newbee.morningGlory.mmorpg.player.achievement.actionEvent;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Achievement_Get extends ActionEventBase {
	private static Logger logger = Logger.getLogger(G2C_Achievement_Get.class);
	private String refId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (logger.isDebugEnabled()) {
			logger.debug("获得的成就的refId"+refId);
		}
		putString(buffer, refId);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

}
