package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import newbee.morningGlory.mmorpg.player.activity.ladder.CombatRecord;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Arena_UpadateFightRecord extends ActionEventBase {
	private static Logger logger = Logger.getLogger(G2C_Arena_UpadateFightRecord.class);
	private CombatRecord record;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(record.getIsAction());
		putString(buffer, record.getName());
		buffer.put(record.getResult());
		buffer.putInt(record.getRankChange());
		
		logger.debug(record.getIsAction());
		logger.debug(record.getName());
		logger.debug(record.getResult());
		logger.debug(record.getRankChange());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setRecord(CombatRecord record) {
		this.record = record;
	}

}
