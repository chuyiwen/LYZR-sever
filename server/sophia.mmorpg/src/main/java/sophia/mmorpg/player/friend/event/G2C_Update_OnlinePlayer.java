package sophia.mmorpg.player.friend.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Update_OnlinePlayer extends ActionEventBase{
	private String playerId;
	private boolean online;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, playerId);
		
		byte onlineType = online ? (byte)1 : (byte)0;
		buffer.put(onlineType);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
