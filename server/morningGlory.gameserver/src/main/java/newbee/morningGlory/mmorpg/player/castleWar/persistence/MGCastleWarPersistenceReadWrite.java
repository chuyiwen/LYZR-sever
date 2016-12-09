package newbee.morningGlory.mmorpg.player.castleWar.persistence;

import newbee.morningGlory.mmorpg.player.castleWar.MGCastleWarComponent;
import newbee.morningGlory.mmorpg.player.dailyQuest.persistence.DailyQuestPersistenceReadWrite;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;

public class MGCastleWarPersistenceReadWrite {

	private static final Logger logger = Logger.getLogger(DailyQuestPersistenceReadWrite.class.getName());

	private static int Default_Write_Version = 10000;

	private int current_version = Default_Write_Version;

	public MGCastleWarPersistenceReadWrite() {
	}

	public byte[] toBytes(MGCastleWarComponent persistenceObject) {
		if (current_version == Default_Write_Version) {
			return toBytesVer10000(persistenceObject);
		} else {
			logger.error("写入版本没有对应写入方法");
			return null;
		}
	}

	private byte[] toBytesVer10000(MGCastleWarComponent persistenceObject) {
		int instanceTime = persistenceObject.getInstanceTime();
		boolean isGetGift = persistenceObject.getDailyGift();
		long lastRefreshTime = persistenceObject.getLastRefreshTime();
		ByteArrayReadWriteBuffer br = new ByteArrayReadWriteBuffer();
		// 我们先写个版本号，读的时候，好用对应版本的方法。
		br.writeInt(Default_Write_Version);
		br.writeInt(instanceTime);
		br.writeBoolean(isGetGift);
		br.writeLong(lastRefreshTime);
		return br.getData();
	}

	public void fromBytes(byte[] persistenceBytes, MGCastleWarComponent castleWarComponent) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int Vers = buffer.readInt();
		if (Vers == Default_Write_Version) {
			fromBytesVers10000(buffer, castleWarComponent);
		} else {
			int instanceTime = Vers;
			boolean isGetGift = buffer.readBoolean();
			castleWarComponent.setDailyGift(isGetGift);
			castleWarComponent.setInstanceTime(instanceTime);
		}
	}

	private void fromBytesVers10000(ByteArrayReadWriteBuffer buffer, MGCastleWarComponent castleWarComponent) {
		int instanceTime = buffer.readInt();
		boolean isGetGift = buffer.readBoolean();
		long lastRefreshTime = buffer.readLong();
		castleWarComponent.setDailyGift(isGetGift);
		castleWarComponent.setInstanceTime(instanceTime);
		castleWarComponent.setLastRefreshTime(lastRefreshTime);
	}

}
