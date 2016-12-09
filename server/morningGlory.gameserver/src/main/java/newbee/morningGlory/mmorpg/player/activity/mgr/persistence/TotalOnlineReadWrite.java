package newbee.morningGlory.mmorpg.player.activity.mgr.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.mgr.OnlineMgr;
import newbee.morningGlory.mmorpg.player.activity.ref.OnlineRef;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class TotalOnlineReadWrite implements ActivityReadWrite {
	private static Logger logger = Logger.getLogger(TotalOnlineReadWrite.class);
	private Player player;

	public TotalOnlineReadWrite(Player player) {
		this.player = player;
	}

	public byte[] toBytes() {
		return toBytesVer10004();
	}

	public void fromBytes(ByteArrayReadWriteBuffer buffer) {
		int ver = buffer.readInt();
		if (ver == 10003) {
			fromBytesVer10003(buffer);
		} else if (ver == 10004) {
			fromBytesVer10004(buffer);
		} else {
			logger.error("读出版本没有对应读出方法");
		}
	}
	
	private byte[] toBytesVer10004() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		
		OnlineMgr onlineMgr = playerActivityComponent.getOnlineMgr();
		String dayTimeString = onlineMgr.getTimeString();
		String crtCumulativeTimeRefId = onlineMgr.getCrtCumulativeTimeRefId();
		int lastOnlineTime = onlineMgr.getLastOnlineTime();
		Map<String, AwardData> onlineAwards = onlineMgr.getOnlineAwards();


		buffer.writeInt(10004);
		buffer.writeString(dayTimeString);
		buffer.writeString(crtCumulativeTimeRefId);
		buffer.writeInt(lastOnlineTime);

		buffer.writeInt(onlineAwards.size());
		for (Entry<String, AwardData> entry : onlineAwards.entrySet()) {
			buffer.writeString(entry.getKey());
			AwardData awardData = entry.getValue();
			buffer.writeByte(awardData.getType());
			buffer.writeByte(awardData.getState());
		}

		return buffer.getData();
	}

	public void fromBytesVer10003(ByteArrayReadWriteBuffer buffer) {
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		
		OnlineMgr onlineMgr = playerActivityComponent.getOnlineMgr();
		
		Map<String, AwardData> onlineAwards = new HashMap<String, AwardData>();

		int lastAccumulativeSeconds = buffer.readInt();
		String crtCumulativeTimeRefId = buffer.readString();
		boolean hasAward = buffer.readBoolean();
		String dayTimeString = buffer.readString();
		
		int lastOnlineTime = calculateLastOnlineTime(crtCumulativeTimeRefId, lastAccumulativeSeconds);

		int mapSize = buffer.readInt();
		for (int i = 0; i < mapSize; i++) {
			String refId = buffer.readString();
			byte type = buffer.readByte();
			byte awardState = buffer.readByte();
			AwardData awardData = new AwardData(type, awardState);
			onlineAwards.put(refId, awardData);
		}

		onlineMgr.setCrtCumulativeTimeRefId(crtCumulativeTimeRefId);
		onlineMgr.setTimeString(dayTimeString);
		onlineMgr.setOnlineAwards(onlineAwards);
		onlineMgr.setLastOnlineTime(lastOnlineTime);
	}
	
	private int calculateLastOnlineTime(String crtCumulativeTimeRefId, int lastAccumulativeSeconds) {
		OnlineRef onlineRef = (OnlineRef)GameRoot.getGameRefObjectManager().getManagedObject(crtCumulativeTimeRefId);
		
		int lastOnlineTime = 0;
		if (onlineRef != null) {
			OnlineRef preOnlineRef = onlineRef.getOnlineRefPreRef();

			while (preOnlineRef != null) {
				int crtNeedTime = MGPropertyAccesser.getOnlineSecond(preOnlineRef.getProperty());
				lastOnlineTime = lastOnlineTime + crtNeedTime;
				preOnlineRef = preOnlineRef.getOnlineRefPreRef();
			}
		}
		
		return lastOnlineTime;
	}
	
	public void fromBytesVer10004(ByteArrayReadWriteBuffer buffer) {
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		
		OnlineMgr onlineMgr = playerActivityComponent.getOnlineMgr();
		Map<String, AwardData> onlineAwards = new HashMap<String, AwardData>();

		String dayTimeString = buffer.readString();
		String crtCumulativeTimeRefId = buffer.readString();
		int lastOnlineTime = buffer.readInt();
		
		int mapSize = buffer.readInt();
		for (int i = 0; i < mapSize; i++) {
			String refId = buffer.readString();
			byte type = buffer.readByte();
			byte awardState = buffer.readByte();
			AwardData awardData = new AwardData(type, awardState);
			onlineAwards.put(refId, awardData);
		}

		onlineMgr.setTimeString(dayTimeString);
		onlineMgr.setCrtCumulativeTimeRefId(crtCumulativeTimeRefId);
		onlineMgr.setOnlineAwards(onlineAwards);
		onlineMgr.setLastOnlineTime(lastOnlineTime);
	}
}
