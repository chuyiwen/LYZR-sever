package newbee.morningGlory.mmorpg.player.activity.mgr.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.mgr.LevelUpMgr;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class LevelupReadWrite implements ActivityReadWrite{
	private static Logger logger = Logger.getLogger(LevelupReadWrite.class);
	private Player player;
	private int current_version = Default_Write_Version;

	public LevelupReadWrite(Player player) {
		this.player = player;
	}

	public byte[] toBytes() {
		if (current_version == 10000) {
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
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		LevelUpMgr levelUpMgr = playerActivityComponent.getLevelUpMgr();

		String crtLevelUpRewardRefId = levelUpMgr.getCrtLevelUpRewardRefId();
		Map<String, AwardData> levelUpMaps = levelUpMgr.getLevelUpMaps();

		buffer.writeInt(Default_Write_Version);
		buffer.writeString(crtLevelUpRewardRefId);
		buffer.writeInt(levelUpMaps.size());
		for (Entry<String, AwardData> entry : levelUpMaps.entrySet()) {
			buffer.writeString(entry.getKey());
			buffer.writeByte(entry.getValue().getType());
			buffer.writeByte(entry.getValue().getState());
			if (logger.isDebugEnabled()) {
				logger.debug(entry.getKey() + "," + entry.getValue().getType() + "," + entry.getValue().getState());
			}
		}
		return buffer.getData();
	}

	public void fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		LevelUpMgr levelUpMgr = playerActivityComponent.getLevelUpMgr();
		Map<String, AwardData> levelUpMaps = new HashMap<String, AwardData>();

		String crtLevelUpRewardRefId = buffer.readString();
		int levelUpMapsSize = buffer.readInt();
		for (int i = 0; i < levelUpMapsSize; i++) {
			String refId = buffer.readString();
			byte type = buffer.readByte();
			byte state = buffer.readByte();
			AwardData awardData = new AwardData(type, state);
			levelUpMaps.put(refId, awardData);
			if (logger.isDebugEnabled()) {
				logger.debug("read levelUp info");
				logger.debug("refId:" + refId + "type:" + type + ", state:" + state);
			}
		}
		levelUpMgr.setCrtLevelUpRewardRefId(crtLevelUpRewardRefId);
		levelUpMgr.setLevelUpMaps(levelUpMaps);
	}

}
