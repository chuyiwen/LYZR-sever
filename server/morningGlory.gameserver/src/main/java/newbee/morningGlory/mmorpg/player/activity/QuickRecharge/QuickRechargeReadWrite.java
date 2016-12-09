package newbee.morningGlory.mmorpg.player.activity.QuickRecharge;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class QuickRechargeReadWrite implements ActivityReadWrite{
	private static Logger logger = Logger.getLogger(QuickRechargeReadWrite.class);
	private Player player;
	private int current_version = Default_Write_Version;

	public QuickRechargeReadWrite(Player player) {
		this.player = player;
	}

	public byte[] toBytes() {
		if (current_version == 10000) {
			return toBytesVer10000();
		}else {
			logger.error("写入版本没有对应写入方法");
			return null;
		}
	}

	public void fromBytes(ByteArrayReadWriteBuffer buffer) {
		int ver = buffer.readInt();
		if (ver == 10000) {
			fromBytesVer10000(buffer);
		}else {
			logger.error("读出版本没有对应读出方法");
		}
	}

	private byte[] toBytesVer10000() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGPlayerQuickRechargeComponent quickRechargeComponent = (MGPlayerQuickRechargeComponent) player.getTagged(MGPlayerQuickRechargeComponent.Tag);
		QuickRechargeMgr quickRechargeMgr = quickRechargeComponent.getQuickRechargeMgr();	
		buffer.writeInt(current_version);
		int size = quickRechargeMgr.getQuickRechargeSet().size();
		buffer.writeInt(size);
		for(String quickRechargeRefId : quickRechargeMgr.getQuickRechargeSet()){
			buffer.writeString(quickRechargeRefId);
		}
		
		return buffer.getData();
	}

	private void fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		MGPlayerQuickRechargeComponent quickRechargeComponent = (MGPlayerQuickRechargeComponent) player.getTagged(MGPlayerQuickRechargeComponent.Tag);
		QuickRechargeMgr quickRechargeMgr = quickRechargeComponent.getQuickRechargeMgr();	
		int size = buffer.readInt();
		for(int i= 0 ;i < size ; i++){
			String quickRechargeRefId = buffer.readString();
			quickRechargeMgr.add(quickRechargeRefId);
		}

	}
	
	
	
}
