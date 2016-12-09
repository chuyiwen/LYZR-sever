package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_MonsterIntrusion_EnterMap extends ActionEventBase{

	private long continuTime;
	private double ExpMultiple;
	private String monsterRefId;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putLong(continuTime);
		buffer.putDouble(ExpMultiple);
		putString(buffer, monsterRefId);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		
	}

	public long getContinuTime() {
		return continuTime;
	}

	public void setContinuTime(long continuTime) {
		this.continuTime = continuTime;
	}

	public double getExpMultiple() {
		return ExpMultiple;
	}

	public void setExpMultiple(double expMultiple) {
		ExpMultiple = expMultiple;
	}

	public String getMonsterRefId() {
		return monsterRefId;
	}

	public void setMonsterRefId(String monsterRefId) {
		this.monsterRefId = monsterRefId;
	}
	
}
