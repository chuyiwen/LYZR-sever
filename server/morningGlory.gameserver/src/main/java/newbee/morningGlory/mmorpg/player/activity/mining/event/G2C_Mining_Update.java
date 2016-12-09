package newbee.morningGlory.mmorpg.player.activity.mining.event;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mining_Update extends ActionEventBase{
	private byte totalCollectedCount;
	private Map<Byte, Byte> collectedCount;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(totalCollectedCount);
		int size = collectedCount.size();
		
		buffer.put((byte)size);
		for (Entry<Byte, Byte> entry : collectedCount.entrySet()) {
			byte pluckType = entry.getKey();
			byte collectedCount = entry.getValue();
			
			buffer.put(pluckType);
			buffer.put(collectedCount);
		}
		
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		
	}

	public void setCollectedCount(Map<Byte, Byte> collectedCount) {
		this.collectedCount = collectedCount;
	}

	public void setTotalCollectedCount(byte totalCollectedCount) {
		this.totalCollectedCount = totalCollectedCount;
	}
	
}
