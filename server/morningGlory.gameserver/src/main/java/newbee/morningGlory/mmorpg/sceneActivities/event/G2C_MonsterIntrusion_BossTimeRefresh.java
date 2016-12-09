package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_MonsterIntrusion_BossTimeRefresh extends ActionEventBase{

	private String monsterRefId;
	private String sceneRefId;
	private long refreshTime;
	private byte isDead;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, monsterRefId);
		putString(buffer, sceneRefId);
		buffer.putLong(refreshTime / 1000);
		buffer.put(isDead);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
//		this.sceneRefId = getString(buffer);
//		this.x = buffer.getInt();
//		this.y = buffer.getInt();
	}

	public String getMonsterRefId() {
		return monsterRefId;
	}

	public void setMonsterRefId(String monsterRefId) {
		this.monsterRefId = monsterRefId;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}
	
	public byte getIsDead() {
		return isDead;
	}

	public void setIsDead(byte isDead) {
		this.isDead = isDead;
	}

	public String getSceneRefId() {
		return sceneRefId;
	}

	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}

}
