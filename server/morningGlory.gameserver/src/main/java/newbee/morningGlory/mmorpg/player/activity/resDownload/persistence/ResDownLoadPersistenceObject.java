package newbee.morningGlory.mmorpg.player.activity.resDownload.persistence;

import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadComponent;
import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadData;
import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadMgr;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class ResDownLoadPersistenceObject {
	private static ResDownLoadPersistenceObject instance = new ResDownLoadPersistenceObject();
	private static final int Default_version = 10000;
	public static ResDownLoadPersistenceObject getInstance() {
		return instance;
	}
	
	private ResDownLoadPersistenceObject() {
		
	}

	public byte[] ToBytes(Player player) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGResDownLoadComponent resDownLoadComponent  = (MGResDownLoadComponent)player.getTagged(MGResDownLoadComponent.Tag);
		MGResDownLoadMgr resDownLoadMgr = resDownLoadComponent.getResDownLoadMgr();
		List<MGResDownLoadData> list = resDownLoadMgr.getResDownloads();
		buffer.writeInt(Default_version);
		buffer.writeInt(list.size());
		for(MGResDownLoadData data : list){
			buffer.writeString(data.getRewardId());
			buffer.writeByte(data.getIsResDownloadReceive());
			buffer.writeString(data.getIdentityName());
			buffer.writeString(data.getRewardPlayerName());
		}
		return buffer.getData();
	}
	
	public MGResDownLoadMgr FromBytes(byte[] persistence,Player player) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistence);
		MGResDownLoadComponent resDownLoadComponent  = (MGResDownLoadComponent)player.getTagged(MGResDownLoadComponent.Tag);
		MGResDownLoadMgr resDownLoadMgr = resDownLoadComponent.getResDownLoadMgr();
		int version  = buffer.readInt();
		int size = buffer.readInt();
		for(int i= 0 ;i< size;i++){
			String rewardId = buffer.readString();
			byte isResDownloadReceive = buffer.readByte();
			String identityName = buffer.readString();
			String rewardPlayerName = buffer.readString();
			
			MGResDownLoadData data = new MGResDownLoadData();
			data.setIdentityName(identityName);
			data.setIsResDownloadReceive(isResDownloadReceive);
			data.setRewardId(rewardId);
			data.setRewardPlayerName(rewardPlayerName);
			resDownLoadMgr.addResDownLoadData(data);
			
		}
		return null;
		
	}
}
