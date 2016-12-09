package newbee.morningGlory.mmorpg.player.wing.persistence;

import newbee.morningGlory.mmorpg.player.wing.MGPlayerWing;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public class WingReadWrite extends AbstractPersistenceObjectReadWrite<MGPlayerWing> implements PersistenceObjectReadWrite<MGPlayerWing> {
	private static Logger logger = Logger.getLogger(WingReadWrite.class);
	
	@Override
	public byte[] toBytes(MGPlayerWing persistenceObject) {
		return toBytesVer10001(persistenceObject);
	}

	@Override
	public MGPlayerWing fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		
		if (version == 10000) {
			return fromBytesVer10000(buffer);
		} else if (version == 10001) {
			return fromBytesVer10001(buffer);
		} else {
			logger.error("读取数据没有对应的版本 version = " + version);
		}
		return null;
	}

	@Override
	public String toJsonString(MGPlayerWing persistenceObject) {
		return null;
	}

	@Override
	public MGPlayerWing fromJsonString(String persistenceJsonString) {
		return null;
	}

	private byte[] toBytesVer10000(MGPlayerWing persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGPlayerWingRef ref = persistenceObject.getPlayerWingRef();
		buffer.writeInt(10000);
		if (ref != null) {
			String wingRefId = ref.getId();
			buffer.writeString(wingRefId);
		} else {
			buffer.writeString("null");
		}

		return buffer.getData();
	}
	
	private byte[] toBytesVer10001(MGPlayerWing persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGPlayerWingRef ref = persistenceObject.getPlayerWingRef();
		buffer.writeInt(10001);
		if (ref != null) {
			String wingRefId = ref.getId();
			long totalExp = persistenceObject.getExp();
			buffer.writeString(wingRefId);
			buffer.writeLong(totalExp);
		} else {
			buffer.writeString("null");
		}

		return buffer.getData();
	}
	
	private MGPlayerWing fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		String playerWingRefId = buffer.readString();
		if(!StringUtils.equals(playerWingRefId, "null")) {
			playerWingRefId = playerWingRefId + "_0";			
			MGPlayerWing playerWing = new MGPlayerWing();
			MGPlayerWingRef mgPlayerWingRef = (MGPlayerWingRef) GameRoot.getGameRefObjectManager().getManagedObject(playerWingRefId);
			playerWing.setPlayerWingRef(mgPlayerWingRef);
			return playerWing;
		} else {
			return null;
		}
	}
	
	private MGPlayerWing fromBytesVer10001(ByteArrayReadWriteBuffer buffer) {
		String playerWingRefId = buffer.readString();
		if(!StringUtils.equals(playerWingRefId, "null")) {
			long totalExp = buffer.readLong();
			MGPlayerWing playerWing = new MGPlayerWing();
			MGPlayerWingRef mgPlayerWingRef = (MGPlayerWingRef) GameRoot.getGameRefObjectManager().getManagedObject(playerWingRefId);
			playerWing.setPlayerWingRef(mgPlayerWingRef);
			playerWing.setExp(totalExp);
			return playerWing;
		} else {
			return null;
		}
	}
	

}
