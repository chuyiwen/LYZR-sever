package newbee.morningGlory.mmorpg.player.activity.mgr.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.mgr.SignMgr;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class SignReadWrite implements ActivityReadWrite{
	private static Logger logger = Logger.getLogger(SignReadWrite.class);
	private Player player;
	private int currentVersion = Default_Write_Version;
	
	public SignReadWrite(Player player){
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
		
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		SignMgr signMgr = playerActivityComponent.getSignMgr();		
		
		String signDateInfo = signMgr.getSignDateInfo();
		List<Byte> signDataList = signMgr.getSignDataList();
		Map<String, AwardData> signAwardMap = signMgr.getAccumulativeSignAwardMap();
		
		buffer.writeInt(currentVersion);
		buffer.writeString(signDateInfo);
		buffer.writeInt(signDataList.size());
		for (Byte day : signDataList) {
			buffer.writeByte(day);
		}
		
		buffer.writeInt(signAwardMap.size());
		for (Entry<String, AwardData> entry : signAwardMap.entrySet()) {
			buffer.writeString(entry.getKey());
			AwardData awardData = entry.getValue();
			buffer.writeByte(awardData.getType());
			buffer.writeByte(awardData.getState());
		}
		
		return buffer.getData();
	}
	
	private void fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		SignMgr signMgr = playerActivityComponent.getSignMgr();
		List<Byte> signDataList = new ArrayList<Byte>();
		Map<String, AwardData> signAwardMap = new HashMap<String, AwardData>();

		String signDateInfo = buffer.readString();
		int listSize = buffer.readInt();
		for (int i = 0; i < listSize; i++) {
			signDataList.add(buffer.readByte());
		}

		int mapSize = buffer.readInt();
		for (int i = 0; i < mapSize; i++) {
			String refId = buffer.readString();
			byte type = buffer.readByte();
			byte awardState = buffer.readByte();
			AwardData awardData = new AwardData(type, awardState);
			signAwardMap.put(refId, awardData);
		}

		signMgr.setSignDateInfo(signDateInfo);
		signMgr.setSignDataList(signDataList);
		signMgr.setAccumulativeSignAwardMap(signAwardMap);
	}
}
