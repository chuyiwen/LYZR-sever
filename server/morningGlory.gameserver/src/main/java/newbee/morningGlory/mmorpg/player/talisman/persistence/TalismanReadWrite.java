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
package newbee.morningGlory.mmorpg.player.talisman.persistence;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.character.CharacterCreate;
import newbee.morningGlory.mmorpg.player.talisman.MGCittaRef;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerCitta;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerTalismanComponent;
import newbee.morningGlory.mmorpg.player.talisman.MGTalisman;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanContains;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanEffectMgr;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanRef;
import newbee.morningGlory.mmorpg.player.talisman.level.MGTalismanDataConfig;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public final class TalismanReadWrite extends AbstractPersistenceObjectReadWrite<MGPlayerCitta> implements PersistenceObjectReadWrite<MGPlayerCitta> {
	private static final Logger logger = Logger.getLogger(TalismanReadWrite.class);
	private MGPlayerCitta playerCitta;
	private Player player;

	public TalismanReadWrite(MGPlayerCitta playerCitta, Player player) {
		this.playerCitta = playerCitta;
		this.player = player;
	}

	@Override
	public byte[] toBytes(MGPlayerCitta persistenceObject) {
		return toBytesVer10005(persistenceObject);
	}

	@Override
	public MGPlayerCitta fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		switch (version) {
		case Default_Write_Version + 3:
			return fromBytesVer10003(persistenceBytes);
		case Default_Write_Version + 4:
			return fromBytesVer10004(persistenceBytes);
		case Default_Write_Version + 5:
			return fromBytesVer10005(persistenceBytes);
		}
		return fromBytesVer10005(persistenceBytes);
	}

	@Override
	public String toJsonString(MGPlayerCitta persistenceObject) {
		return toJsonVer10000(persistenceObject);
	}

	@Override
	public MGPlayerCitta fromJsonString(String persistenceJsonString) {
		return fromJsonStringVer10000(persistenceJsonString);
	}
	
	private byte[] toBytesVer10005(MGPlayerCitta persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version + 5);
		if (persistenceObject.getTalismanList().size() != MGPlayerCitta.TalismanNumber) {
			logger.error("保存法宝数据出错!!!!,player:" + player.getName() + ",identity:" + player.getIdentity().getId() + ",数量:" + playerCitta.getTalismanList().size());

		}
		if (persistenceObject.getTalismanList().size() == 0) {
			CharacterCreate.configTalismanComponentTo(player);
		}
		int i = 0;
		MGCittaRef ref = persistenceObject.getCittaRef();
		String refId = ref == null ? "" : ref.getId();
		int level = ref == null ? 0 : persistenceObject.getLevel();
		buffer.writeString(refId);
		buffer.writeInt(level);
		buffer.writeLong(persistenceObject.getLastRewardTime());
		buffer.writeInt(persistenceObject.getGoldReward());
		buffer.writeInt(persistenceObject.getExpReward());
		buffer.writeInt(persistenceObject.getStoneReward());
		buffer.writeInt(persistenceObject.getBaoXiangs().size());
		for (ItemPair itemPair : persistenceObject.getBaoXiangs()) {
			buffer.writeString(itemPair.getItemRefId());
			buffer.writeInt(itemPair.getNumber());
		}
		buffer.writeInt(MGPlayerCitta.TalismanNumber);
		for (MGTalismanContains talismanContain : persistenceObject.getTalismanList()) {
			if (i > MGPlayerCitta.TalismanNumber - 1) {
				break;
			}
			MGTalisman talisman = talismanContain.getTalisman();
			buffer.writeInt(talismanContain.getIndex());
			buffer.writeString(talisman.getTalismanRef().getId());
			buffer.writeByte(talisman.getState());
			buffer.writeLong(talisman.getLastHandleTime());
			i++;
		}
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		Map<Byte, Long> map = talismanComponent.getStatistics().getStatistics();
		buffer.writeInt(map.size());
		for (Entry<Byte, Long> entry : map.entrySet()) {
			buffer.writeByte(entry.getKey());
			buffer.writeLong(entry.getValue());
		}
		byte[] data = buffer.getData();
		return data;
	}
	
	private MGPlayerCitta fromBytesVer10005(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		MGTalismanDataConfig talismanDataConfig = (MGTalismanDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGTalismanDataConfig.MGTalisman_Id);
		MGTalismanEffectMgr effectMgr = ((MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag)).getTalismanEffectMgr();
		int version = buffer.readInt();
		String cittaRefId = buffer.readString();
		MGCittaRef cittaRef = (MGCittaRef) GameRoot.getGameRefObjectManager().getManagedObject(cittaRefId);
		playerCitta.setCittaRef(cittaRef);
		int level = buffer.readInt();
		long lastRewardTime = buffer.readLong();
		int goldReward = buffer.readInt();
		int expReward = buffer.readInt();
		int stoneReward = buffer.readInt();
		int baoXiangSize = buffer.readInt();
		playerCitta.setLastRewardTime(lastRewardTime);
		for (int i = 0; i < baoXiangSize; i++) {
			String itemRefId = buffer.readString();
			int number = buffer.readInt();
			ItemPair itemPair = new ItemPair(itemRefId, number, false);
			playerCitta.getBaoXiangs().add(itemPair);
		}	
		playerCitta.setGoldReward(goldReward);
		playerCitta.setExpReward(expReward);
		playerCitta.setStoneReward(stoneReward);
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			int index = buffer.readInt();
			String talismanRefId = buffer.readString();
			String key = talismanRefId + "_" + level;
			MGTalismanRef talismanRef = talismanDataConfig.getTalismanLevelDataMap().get(key);
			byte state = buffer.readByte();
			long lastHandleTime = buffer.readLong();
			MGTalisman talisman = new MGTalisman(talismanRef);
			talisman.setState(state);
			talisman.setLastHandleTime(lastHandleTime);
			MGTalismanContains talismanContains = new MGTalismanContains(index, talisman);
			playerCitta.addTalisman(talismanContains);

		}
		if (playerCitta.getTalismanList().size() != MGPlayerCitta.TalismanNumber) {
			logger.error("load 法宝数据出错!!!!,player:" + player.getName() + ",identity:" + player.getIdentity().getId() + ",法宝数量:" + playerCitta.getTalismanList().size());
		}
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);

		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			byte key = buffer.readByte();
			long value = buffer.readLong();
			talismanComponent.getStatistics().addTalismanStatistics(key, value);
		}
		effectMgr.restore();
		return playerCitta;
	}
	
	private byte[] toBytesVer10004(MGPlayerCitta persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version + 4);
		if (persistenceObject.getTalismanList().size() != MGPlayerCitta.TalismanNumber) {
			logger.error("保存法宝数据出错!!!!,player:" + player.getName() + ",identity:" + player.getIdentity().getId() + ",数量:" + playerCitta.getTalismanList().size());

		}
		if (persistenceObject.getTalismanList().size() == 0) {
			CharacterCreate.configTalismanComponentTo(player);
		}
		int i = 0;
		MGCittaRef ref = persistenceObject.getCittaRef();
		String refId = ref == null ? "" : ref.getId();
		int level = ref == null ? 0 : persistenceObject.getLevel();
		buffer.writeString(refId);
		buffer.writeInt(level);
		buffer.writeInt(persistenceObject.getGoldReward());
		buffer.writeInt(persistenceObject.getExpReward());
		buffer.writeInt(persistenceObject.getStoneReward());
		buffer.writeInt(persistenceObject.getBaoXiangs().size());
		for (ItemPair itemPair : persistenceObject.getBaoXiangs()) {
			buffer.writeString(itemPair.getItemRefId());
			buffer.writeInt(itemPair.getNumber());
		}
		buffer.writeInt(MGPlayerCitta.TalismanNumber);
		for (MGTalismanContains talismanContain : persistenceObject.getTalismanList()) {
			if (i > MGPlayerCitta.TalismanNumber - 1) {
				break;
			}
			MGTalisman talisman = talismanContain.getTalisman();
			buffer.writeInt(talismanContain.getIndex());
			buffer.writeString(talisman.getTalismanRef().getId());
			buffer.writeByte(talisman.getState());
			buffer.writeLong(talisman.getLastHandleTime());
			i++;
		}
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		Map<Byte, Long> map = talismanComponent.getStatistics().getStatistics();
		buffer.writeInt(map.size());
		for (Entry<Byte, Long> entry : map.entrySet()) {
			buffer.writeByte(entry.getKey());
			buffer.writeLong(entry.getValue());
		}
		byte[] data = buffer.getData();
		return data;
	}
	private MGPlayerCitta fromBytesVer10004(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		MGTalismanDataConfig talismanDataConfig = (MGTalismanDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGTalismanDataConfig.MGTalisman_Id);
		MGTalismanEffectMgr effectMgr = ((MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag)).getTalismanEffectMgr();
		int version = buffer.readInt();
		String cittaRefId = buffer.readString();
		MGCittaRef cittaRef = (MGCittaRef) GameRoot.getGameRefObjectManager().getManagedObject(cittaRefId);
		playerCitta.setCittaRef(cittaRef);
		int level = buffer.readInt();
		int goldReward = buffer.readInt();
		int expReward = buffer.readInt();
		int stoneReward = buffer.readInt();
		int baoXiangSize = buffer.readInt();
		for (int i = 0; i < baoXiangSize; i++) {
			String itemRefId = buffer.readString();
			int number = buffer.readInt();
			ItemPair itemPair = new ItemPair(itemRefId, number, false);
			playerCitta.getBaoXiangs().add(itemPair);
		}
		playerCitta.setGoldReward(goldReward);
		playerCitta.setExpReward(expReward);
		playerCitta.setStoneReward(stoneReward);
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			int index = buffer.readInt();
			String talismanRefId = buffer.readString();
			String key = talismanRefId + "_" + level;
			MGTalismanRef talismanRef = talismanDataConfig.getTalismanLevelDataMap().get(key);
			byte state = buffer.readByte();
			long lastHandleTime = buffer.readLong();
			MGTalisman talisman = new MGTalisman(talismanRef);
			talisman.setState(state);
			talisman.setLastHandleTime(lastHandleTime);
			MGTalismanContains talismanContains = new MGTalismanContains(index, talisman);
			playerCitta.addTalisman(talismanContains);

		}
		if (playerCitta.getTalismanList().size() != MGPlayerCitta.TalismanNumber) {
			logger.error("load 法宝数据出错!!!!,player:" + player.getName() + ",identity:" + player.getIdentity().getId() + ",法宝数量:" + playerCitta.getTalismanList().size());
		}
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);

		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			byte key = buffer.readByte();
			long value = buffer.readLong();
			talismanComponent.getStatistics().addTalismanStatistics(key, value);
		}
		effectMgr.restore();
		return playerCitta;
	}

	private MGPlayerCitta fromBytesVer10003(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		MGTalismanDataConfig talismanDataConfig = (MGTalismanDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGTalismanDataConfig.MGTalisman_Id);
		MGTalismanEffectMgr effectMgr = ((MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag)).getTalismanEffectMgr();
		int version = buffer.readInt();
		String cittaRefId = buffer.readString();
		MGCittaRef cittaRef = (MGCittaRef) GameRoot.getGameRefObjectManager().getManagedObject(cittaRefId);
		playerCitta.setCittaRef(cittaRef);
		int level = buffer.readInt();
		int goldReward = buffer.readInt();
		int expReward = buffer.readInt();
		int stoneReward = buffer.readInt();
		int baoXiangSize = buffer.readInt();
		for (int i = 0; i < baoXiangSize; i++) {
			String itemRefId = buffer.readString();
			int number = buffer.readInt();
			ItemPair itemPair = new ItemPair(itemRefId, number, false);
			playerCitta.getBaoXiangs().add(itemPair);
		}
		playerCitta.setGoldReward(goldReward);
		playerCitta.setExpReward(expReward);
		playerCitta.setStoneReward(stoneReward);
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			int index = buffer.readInt();
			String talismanRefId = buffer.readString();
			String key = talismanRefId + "_" + level;
			MGTalismanRef talismanRef = talismanDataConfig.getTalismanLevelDataMap().get(key);
			byte state = buffer.readByte();
			MGTalisman talisman = new MGTalisman(talismanRef);
			talisman.setState(state);

			MGTalismanContains talismanContains = new MGTalismanContains(index, talisman);
			playerCitta.addTalisman(talismanContains);

		}
		if (playerCitta.getTalismanList().size() != MGPlayerCitta.TalismanNumber) {
			logger.error("load 法宝数据出错!!!!,player:" + player.getName() + ",identity:" + player.getIdentity().getId() + ",法宝数量:" + playerCitta.getTalismanList().size());
		}
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);

		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			byte key = buffer.readByte();
			long value = buffer.readLong();
			talismanComponent.getStatistics().addTalismanStatistics(key, value);
		}
		effectMgr.restore();
		return playerCitta;
	}


	private String toJsonVer10000(MGPlayerCitta persistenceObject) {
		return null;
	}

	private MGPlayerCitta fromJsonStringVer10000(String persistenceJsonString) {
		return null;
	}

}
