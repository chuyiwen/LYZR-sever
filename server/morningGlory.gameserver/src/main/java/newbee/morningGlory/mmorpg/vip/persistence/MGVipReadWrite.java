/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package newbee.morningGlory.mmorpg.vip.persistence;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.vip.MGVipEffectMgr;
import newbee.morningGlory.mmorpg.vip.MGVipLevelMgr;
import newbee.morningGlory.mmorpg.vip.MGVipRewardRecord;
import newbee.morningGlory.mmorpg.vip.MGVipType;
import newbee.morningGlory.mmorpg.vip.lottery.MGVipLotteryMgr;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public final class MGVipReadWrite extends AbstractPersistenceObjectReadWrite<MGVipLevelMgr> implements PersistenceObjectReadWrite<MGVipLevelMgr> {
	private MGVipLevelMgr vipMgr;
	private MGVipLotteryMgr lotteryMgr;
	private Player player;

	public MGVipReadWrite(MGVipLevelMgr vipMgr,MGVipLotteryMgr lotteryMgr, Player player) {
		this.vipMgr = vipMgr;
		this.player = player;
		this.lotteryMgr = lotteryMgr;
	}

	@Override
	public byte[] toBytes(MGVipLevelMgr persistenceObject) {
		return toBytesVer10000(persistenceObject);
	}

	@Override
	public MGVipLevelMgr fromBytes(byte[] persistenceBytes) {
		return fromBytesVer10000(persistenceBytes);
	}

	@Override
	public String toJsonString(MGVipLevelMgr persistenceObject) {
		return toJsonVer10000(persistenceObject);
	}

	@Override
	public MGVipLevelMgr fromJsonString(String persistenceJsonString) {
		// TODO Auto-generated method stub
		return fromJsonStringVer10000(persistenceJsonString);
	}

	private byte[] toBytesVer10000(MGVipLevelMgr persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version);
		byte vipType = persistenceObject.getVipType();
		buffer.writeByte(vipType);
		if (persistenceObject.isVip()) {
			buffer.writeString(persistenceObject.getVipLevelDataRef().getId());
			buffer.writeLong(persistenceObject.getVipStartTime());
			buffer.writeLong(persistenceObject.getRemainTime());
			buffer.writeLong(persistenceObject.getVipEndTime());
			MGVipRewardRecord record = persistenceObject.getVipRewardRecord();
			buffer.writeLong(record.getGetExpTime());
			buffer.writeLong(record.getGetGiftTime());
			Map<Integer,Integer> levelRewards = record.getVipLevelRewardTimeMap();
			byte count = 0;
			for(Entry<Integer,Integer> entry : levelRewards.entrySet()){
				count ++ ;
			}
			buffer.writeByte(count);
			for(Entry<Integer,Integer> entry : levelRewards.entrySet()){
				int level = entry.getKey();
				buffer.writeInt(level);
			}
		}
		buffer.writeLong(lotteryMgr.getLotteryRecord().getLastUpdateLotteryCountTime());
		buffer.writeInt(lotteryMgr.getLotteryRecord().getResidueCount());
		byte[] data = buffer.getData();
		return data;
	}

	private MGVipLevelMgr fromBytesVer10000(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int defaultVersion = buffer.readInt();
		byte vipType = buffer.readByte();
		vipMgr.setVip(vipType);
		player.setVipType(vipType);
		if(vipType != MGVipType.NO_VIP){
			String vipRefId = buffer.readString();
			long vipStartTime = buffer.readLong();
			long vipRemainTime = buffer.readLong();
			long vipEndTime = buffer.readLong();
			long getExpTime = buffer.readLong();
			long getGiftTime = buffer.readLong();
			
			vipMgr.setVipStartTime(vipStartTime);
			vipMgr.setStackRemainTime(vipRemainTime);
			vipMgr.setVipEndTime(vipEndTime);
			
			MGVipRewardRecord record = vipMgr.getVipRewardRecord();
			record.setGetExpTime(getExpTime);
			record.setGetGiftTime(getGiftTime);
			Map<Integer,Integer> levelRewards = record.getVipLevelRewardTimeMap();
			byte count = buffer.readByte();
			for(byte i=0;i<count;i++){
				int key = buffer.readInt();
				levelRewards.put(key, key);
			}
			MGVipEffectMgr effectMgr = new MGVipEffectMgr(player);
			effectMgr.restore(vipRefId,vipMgr);
		}
		long lastUpdateLotteryCountTime = buffer.readLong();
		int residueCount = buffer.readInt();
		lotteryMgr.getLotteryRecord().setLastUpdateLotteryCountTime(lastUpdateLotteryCountTime);
		lotteryMgr.getLotteryRecord().addResidueCount(residueCount);
		return vipMgr;
	}

	private String toJsonVer10000(MGVipLevelMgr persistenceObject) {
		return null;
	}

	private MGVipLevelMgr fromJsonStringVer10000(String persistenceJsonString) {
		return null;
	}

}
