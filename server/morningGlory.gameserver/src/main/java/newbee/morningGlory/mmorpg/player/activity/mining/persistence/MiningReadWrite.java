package newbee.morningGlory.mmorpg.player.activity.mining.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import newbee.morningGlory.mmorpg.player.activity.mining.MGPlayerMiningComponent;
import newbee.morningGlory.mmorpg.player.activity.mining.MGPlayerMiningManager;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class MiningReadWrite implements ActivityReadWrite {
	private static Logger logger = Logger.getLogger(MiningReadWrite.class);
	private Player player;
	
	public MiningReadWrite(Player player){
		this.player = player;
	}
	
	@Override
	public byte[] toBytes() {
		return toBytesVer10002();
	}

	@Override
	public void fromBytes(ByteArrayReadWriteBuffer buffer) {
		int version = buffer.readInt();
		if(version == 10000){
			fromBytesVer10000(buffer);
		} else if (version == 10001) {
			fromBytesVer10001(buffer);
		} else if (version == 10002) {
			fromBytesVer10002(buffer);
		} else {
 			logger.error("mining restore data error! version = " + version);
		}
		
	}
	
	private byte[] toBytesVer10002() {
		MGPlayerMiningComponent playerMiningComponent = (MGPlayerMiningComponent)player.getTagged(MGPlayerMiningComponent.Tag);
		MGPlayerMiningManager manager = playerMiningComponent.getPlayerMiningManager();
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		long lastMiningMills = manager.getLastMiningMills();
		Map<Byte, Byte> collectedCountMapping = manager.getCollectedCount();
		
		buffer.writeInt(10002);
		buffer.writeLong(lastMiningMills);
		
		buffer.writeInt(collectedCountMapping.size());
		for (Entry<Byte, Byte> entry : collectedCountMapping.entrySet()) {
			byte pluckType = entry.getKey();
			byte collectedCount = entry.getValue();
			
			buffer.writeByte(pluckType);
			buffer.writeByte(collectedCount);
		}
		
		return buffer.getData();
	}
	
	private void fromBytesVer10000(ByteArrayReadWriteBuffer buffer){
		MGPlayerMiningComponent playerMiningComponent = (MGPlayerMiningComponent)player.getTagged(MGPlayerMiningComponent.Tag);
		MGPlayerMiningManager manager = playerMiningComponent.getPlayerMiningManager();
	
		byte count = buffer.readByte();
		String lastMiningTime = buffer.readString();

		//manager.setCount(buffer.readByte());
	}
	
	private void fromBytesVer10001(ByteArrayReadWriteBuffer buffer){
		MGPlayerMiningComponent playerMiningComponent = (MGPlayerMiningComponent)player.getTagged(MGPlayerMiningComponent.Tag);
		MGPlayerMiningManager manager = playerMiningComponent.getPlayerMiningManager();
	
		byte count = buffer.readByte();
		long lastMiningMills = buffer.readLong();
		
		manager.setLastMiningMills(lastMiningMills);
		
	}
	
	private void fromBytesVer10002(ByteArrayReadWriteBuffer buffer) {
		MGPlayerMiningComponent playerMiningComponent = (MGPlayerMiningComponent)player.getTagged(MGPlayerMiningComponent.Tag);
		MGPlayerMiningManager manager = playerMiningComponent.getPlayerMiningManager();
		
		Map<Byte, Byte> collectedCountMapping = new HashMap<Byte, Byte>();
	
		long lastMiningMills = buffer.readLong();
		
		int size = buffer.readInt();
		
		for (int i = 0; i < size; i++) {
			byte pluckType = buffer.readByte();
			byte collectedCount = buffer.readByte();
			
			collectedCountMapping.put(pluckType, collectedCount);
		}
		
		manager.setLastMiningMills(lastMiningMills);
		manager.setCollectedCount(collectedCountMapping);
	}

}
