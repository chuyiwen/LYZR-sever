package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_PayonPalace_EnterMap extends ActionEventBase{
	private String sceneRefId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.sceneRefId = getString(buffer);
	}
	
	public String getSceneRefId() {
		return sceneRefId;
	}

	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}
}
