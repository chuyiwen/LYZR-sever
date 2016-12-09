package sophia.mmorpg.player.friend.event;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.friend.FriendMember;
import sophia.mmorpg.friend.FriendSystemManager;

public class G2C_GetPlayerList extends ActionEventBase {
	private byte groupType;
	private Map<String, FriendMember> playerList;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(groupType);
		if (playerList != null && playerList.size() > 0) {
			buffer.put((byte) playerList.size());
			for (Entry<String, FriendMember> entry : playerList.entrySet()) {
				FriendMember friendMember = entry.getValue();
				String playerId = friendMember.getPlayerId();
				String playerName = friendMember.getPlayerName();
				byte professionId = friendMember.getProfessionId();
				byte gender = friendMember.getGender();
				boolean online = FriendSystemManager.isOnline(playerId);
				
				putString(buffer, playerName);
				putString(buffer, playerId);
				buffer.put(gender);
				if (online) {
					buffer.put((byte) 1);
				} else {
					buffer.put((byte) 0);
				}
				
				buffer.put(professionId);
			}
		} else {
			buffer.put((byte) 0);
		}
		
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

	public Map<String, FriendMember> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(Map<String, FriendMember> playerList) {
		this.playerList = playerList;
	}

}
