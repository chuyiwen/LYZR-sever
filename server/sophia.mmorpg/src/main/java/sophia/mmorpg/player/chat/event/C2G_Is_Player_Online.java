package sophia.mmorpg.player.chat.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Is_Player_Online extends ActionEventBase {
	private byte count;
	// playerId
	private List<String> playerIdList = new ArrayList<String>();

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		count = buffer.get();
		
		for (int i = 0; i < count; i++) {
			String playerId = getString(buffer);
			playerIdList.add(playerId);
		}
	}

	public byte getCount() {
		return count;
	}

	public List<String> getPlayerIdList() {
		return playerIdList;
	}

}
