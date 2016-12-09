package newbee.morningGlory.mmorpg.player.sortboard.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_SortBoard_GetTopPlayerData extends ActionEventBase{
	private List<String> topPlayerData = new ArrayList<>();
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put((byte) topPlayerData.size());
		for (String entry : topPlayerData) {
			putString(buffer, entry);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public List<String> getTopPlayerData() {
		return topPlayerData;
	}

	public void setTopPlayerData(List<String> topPlayerData) {
		this.topPlayerData = topPlayerData;
	}

}