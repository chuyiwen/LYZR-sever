package sophia.mmorpg.player.friend.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_DeleteOnePlayer extends ActionEventBase {
	private String playerId;
	private byte groupType;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(groupType);
		putString(buffer, playerId);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public byte getGroupType() {
		return groupType;
	}

	public void setGroupType(byte groupType) {
		this.groupType = groupType;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

}
