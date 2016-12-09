package sophia.mmorpg.player.friend.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_GetPlayerList extends ActionEventBase {
	private byte groupType;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		groupType = buffer.get();
	}

	public byte getGroupType() {
		return groupType;
	}

	public void setGroupType(byte groupType) {
		this.groupType = groupType;
	}

}
