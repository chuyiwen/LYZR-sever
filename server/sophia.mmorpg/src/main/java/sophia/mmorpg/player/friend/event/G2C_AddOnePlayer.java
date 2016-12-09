package sophia.mmorpg.player.friend.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_AddOnePlayer extends ActionEventBase {
	private Player player;
	private byte groupType;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		String playerName = player.getName();
		String playerId = player.getId();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		byte online = player.isOnline() ? (byte) 1 : (byte) 0;
		byte professionId = player.getProfession();
		
		buffer.put(groupType);
		putString(buffer, playerName);
		putString(buffer, playerId);
		buffer.put(gender);
		buffer.put(online);
		buffer.put(professionId);
		
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public byte getGroupType() {
		return groupType;
	}

	public void setGroupType(byte groupType) {
		this.groupType = groupType;
	}

}
