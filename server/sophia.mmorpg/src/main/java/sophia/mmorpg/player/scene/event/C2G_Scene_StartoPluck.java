package sophia.mmorpg.player.scene.event;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Scene_StartoPluck extends ActionEventBase {
	private static Logger logger = Logger.getLogger(C2G_Scene_StartoPluck.class);
	private String charId;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		putString(arg0, charId);
		return arg0;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		charId = getString(buffer);
		logger.debug(charId);
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

}
