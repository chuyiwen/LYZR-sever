package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_MonsterIntrusion_LeaveMap extends ActionEventBase{
//
//	private String sceneRefId;
//	private int x;
//	private int y;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
//		this.sceneRefId = getString(buffer);
//		this.x = buffer.getInt();
//		this.y = buffer.getInt();
	}

	
}
