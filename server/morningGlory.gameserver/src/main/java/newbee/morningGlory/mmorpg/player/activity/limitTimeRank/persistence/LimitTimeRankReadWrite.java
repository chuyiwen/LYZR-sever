package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeRankMgr;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.MGLimitTimeRankComponent;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class LimitTimeRankReadWrite implements ActivityReadWrite{
	private static Logger logger = Logger.getLogger(LimitTimeRankReadWrite.class);
	private Player player;
	private int currentVersion = Default_Write_Version;

	public LimitTimeRankReadWrite(Player player) {
		this.player = player;
	}

	public byte[] toBytes() {
		if (currentVersion == 10000) {
			return toBytesVer10000();
		} else {
			logger.error("写入版本没有对应写入方法");
			return null;
		}
	}

	public void fromBytes(ByteArrayReadWriteBuffer buffer) {
		int ver = buffer.readInt();
		if (ver == 10000) {
			fromBytesVer10000(buffer);
		} else {
			logger.error("读出版本没有对应读出方法");
		}
	}

	private byte[] toBytesVer10000() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGLimitTimeRankComponent limitTimeRankComponent = (MGLimitTimeRankComponent) player.getTagged(MGLimitTimeRankComponent.Tag);
		LimitTimeRankMgr rankMgr = limitTimeRankComponent.getRankMgr();
		Map<String, AwardData> rewardMaps = rankMgr.getRewardMaps();

		buffer.writeInt(Default_Write_Version);
		buffer.writeInt(rewardMaps.size());
		for (Entry<String, AwardData> entry : rewardMaps.entrySet()) {
			buffer.writeString(entry.getKey());
			buffer.writeByte(entry.getValue().getType());
			buffer.writeByte(entry.getValue().getState());
		}
		return buffer.getData();
	}

	public Object fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		MGLimitTimeRankComponent limitTimeRankComponent = (MGLimitTimeRankComponent) player.getTagged(MGLimitTimeRankComponent.Tag);
		LimitTimeRankMgr rankMgr = limitTimeRankComponent.getRankMgr();
		Map<String, AwardData> rewardMaps = new HashMap<String, AwardData>();

		int rewardMapsSize = buffer.readInt();
		for (int i = 0; i < rewardMapsSize; i++) {
			String refId = buffer.readString();
			AwardData awardData = new AwardData(buffer.readByte(), buffer.readByte());
			rewardMaps.put(refId, awardData);
		}
		rankMgr.setRewardMaps(rewardMaps);
		return null;
	}

}
