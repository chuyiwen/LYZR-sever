package newbee.morningGlory.mmorpg.player.activity.mining.event;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mining_EnterEvent extends ActionEventBase {
	public static final byte Open = 0;
	public static final byte Close = 1;

	private byte enterType;
	private long leaveTime;// 剩余时间
	private byte count;// 已经采集次数
	private String nextTimeString;
	private Map<Byte, Byte> collectedCount;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(enterType);
		if(enterType == Open) {
			buffer.putLong(leaveTime);
			buffer.put(count);
			
			int size = collectedCount.size();
			
			buffer.put((byte)size);
			for (Entry<Byte, Byte> entry : collectedCount.entrySet()) {
				byte pluckType = entry.getKey();
				byte collectedCount = entry.getValue();
				
				buffer.put(pluckType);
				buffer.put(collectedCount);
			}
			
		} else {
			putString(buffer, nextTimeString);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setLeaveTime(long leaveTime) {
		this.leaveTime = leaveTime;
	}

	public void setCount(byte count) {
		this.count = count;
	}

	public void setEnterType(byte enterType) {
		this.enterType = enterType;
	}

	public void setNextTimeString(String nextTimeString) {
		this.nextTimeString = nextTimeString;
	}

	public void setCollectedCount(Map<Byte, Byte> collectedCount) {
		this.collectedCount = collectedCount;
	}
	
}
