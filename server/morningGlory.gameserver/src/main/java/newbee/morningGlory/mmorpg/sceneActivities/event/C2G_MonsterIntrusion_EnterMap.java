package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_MonsterIntrusion_EnterMap extends ActionEventBase {

	private String sceneRefId;
	private int x;
	private int y;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.sceneRefId = getString(buffer);
		this.x = buffer.getInt();
		this.y = buffer.getInt();
	}

	public String getSceneRefId() {
		return sceneRefId;
	}

	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
