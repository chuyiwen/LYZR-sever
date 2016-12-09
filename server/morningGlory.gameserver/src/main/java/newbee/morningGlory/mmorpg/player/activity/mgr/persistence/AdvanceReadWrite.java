package newbee.morningGlory.mmorpg.player.activity.mgr.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.mgr.AdvancedMgr;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class AdvanceReadWrite implements ActivityReadWrite{
	private static Logger logger = Logger.getLogger(AdvanceReadWrite.class);
	private Player player;
	private int current_version = Default_Write_Version;

	public AdvanceReadWrite(Player player) {
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
		AdvancedMgr advancedMgr = playerActivityComponent.getAdvanceMgr();

		Map<String, AwardData> rideAwardMaps = advancedMgr.getRideAwardMaps();
		Map<String, AwardData> wingAwardMaps = advancedMgr.getWingAwardMaps();
		Map<String, AwardData> talisAwardMaps = advancedMgr.getTalisAwardMaps();
		String crtRideRewardRefId = advancedMgr.getCrtRideRewardRefId();
		String crtWingRewardRefId = advancedMgr.getCrtWingRewardRefId();

		buffer.writeInt(Default_Write_Version);
		buffer.writeString(crtRideRewardRefId);
		buffer.writeString(crtWingRewardRefId);

		buffer.writeInt(rideAwardMaps.size());
		for (Entry<String, AwardData> entry : rideAwardMaps.entrySet()) {
			buffer.writeString(entry.getKey());
			buffer.writeByte(entry.getValue().getType());
			buffer.writeByte(entry.getValue().getState());
		}

		buffer.writeInt(wingAwardMaps.size());
		for (Entry<String, AwardData> entry : wingAwardMaps.entrySet()) {
			buffer.writeString(entry.getKey());
			buffer.writeByte(entry.getValue().getType());
			buffer.writeByte(entry.getValue().getState());
		}

		buffer.writeInt(talisAwardMaps.size());
		for (Entry<String, AwardData> entry : talisAwardMaps.entrySet()) {
			buffer.writeString(entry.getKey());
			buffer.writeByte(entry.getValue().getType());
			buffer.writeByte(entry.getValue().getState());
		}
		return buffer.getData();
	}

	public void fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		Map<String, AwardData> rideAwardMaps = new HashMap<String, AwardData>();
		Map<String, AwardData> wingAwardMaps = new HashMap<String, AwardData>();
		Map<String, AwardData> talisAwardMaps = new HashMap<String, AwardData>();
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		AdvancedMgr advanceMgr = playerActivityComponent.getAdvanceMgr();

		advanceMgr.setCrtRideRewardRefId(buffer.readString());
		advanceMgr.setCrtWingRewardRefId(buffer.readString());

		int rideAwardMapsSize = buffer.readInt();
		for (int i = 0; i < rideAwardMapsSize; i++) {
			String refId = buffer.readString();
			AwardData awardData = new AwardData(buffer.readByte(), buffer.readByte());
			rideAwardMaps.put(refId, awardData);
		}

		int wingAwardMapsSize = buffer.readInt();
		for (int i = 0; i < wingAwardMapsSize; i++) {
			String refId = buffer.readString();
			AwardData awardData = new AwardData(buffer.readByte(), buffer.readByte());
			wingAwardMaps.put(refId, awardData);
		}

		int talisAwardMapsSize = buffer.readInt();
		for (int i = 0; i < talisAwardMapsSize; i++) {
			String refId = buffer.readString();
			AwardData awardData = new AwardData(buffer.readByte(), buffer.readByte());
			talisAwardMaps.put(refId, awardData);
		}

		advanceMgr.setRideAwardMaps(rideAwardMaps);
		advanceMgr.setWingAwardMaps(wingAwardMaps);
		advanceMgr.setTalisAwardMaps(talisAwardMaps);
	}

}
