package newbee.morningGlory.mmorpg.player.union.actionEvent;

import newbee.morningGlory.mmorpg.union.MGUnion;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Union_ApplyList extends ActionEventBase {
	private static Logger logger = Logger.getLogger(G2C_Union_ApplyList.class);
	private MGUnion union;

	public G2C_Union_ApplyList() {
		ziped = (byte) 1;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (logger.isDebugEnabled()) {
			logger.info("申请列表返回");
		}

		union.getUnionApplyMgr().writeApplyList(buffer);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setUnion(MGUnion union) {
		this.union = union;
	}

}
