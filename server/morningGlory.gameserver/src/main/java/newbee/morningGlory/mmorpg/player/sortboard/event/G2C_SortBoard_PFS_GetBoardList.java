package newbee.morningGlory.mmorpg.player.sortboard.event;

import java.util.List;

import newbee.morningGlory.mmorpg.sortboard.SortboardData;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_SortBoard_PFS_GetBoardList extends ActionEventBase {

	private SortboardData sortboardData;
	private int sortboardType;
	private int version;
	private byte profession;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(sortboardType);
		buffer.putInt(version);
		List<SortboardScoreData> scoreData = sortboardData.getScoreData();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		buffer.put(profession);
		buffer.put((byte) scoreData.size());
		for (SortboardScoreData data : scoreData) {
			putString(buffer, data.getPlayerId());
			putString(buffer, data.getName());
			Player player = playerManager.getPlayer(data.getPlayerId());
			String unionName = null;
			if (player != null) {
				unionName = MGPropertyAccesser.getUnionName(player.getProperty());
			}
			if (StringUtils.isEmpty(unionName)) {
				putString(buffer, "");
			} else {
				putString(buffer, unionName);
			}

			buffer.putLong(data.getScore());
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public void setSortboardData(SortboardData sortboardData) {
		this.sortboardData = sortboardData;
	}

	public void setSortboardType(int sortboardType) {
		this.sortboardType = sortboardType;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setProfession(byte profession) {
		this.profession = profession;
	}

}
