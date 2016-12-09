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
package newbee.morningGlory.mmorpg.sprite.buff.persistence;

import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffMgr;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public final class BuffReadWrite extends AbstractPersistenceObjectReadWrite<MGFightSpriteBuffMgr> implements PersistenceObjectReadWrite<MGFightSpriteBuffMgr> {
	private MGFightSpriteBuffMgr fightSpriteBuffMgr;
	private Player player;

	public BuffReadWrite(MGFightSpriteBuffMgr fightSpriteBuffMgr, Player player) {
		this.fightSpriteBuffMgr = fightSpriteBuffMgr;
		this.player = player;
	}

	@Override
	public byte[] toBytes(MGFightSpriteBuffMgr persistenceObject) {
		return toBytesVer10001(persistenceObject);
	}

	@Override
	public MGFightSpriteBuffMgr fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		if (version == Default_Write_Version) {
			return fromBytesVer10000(buffer);
		} else if (version == Default_Write_Version + 1) {
			return fromBytesVer10001(buffer);
		} else if (version == Default_Write_Version + 2) {
			return fromBytesVer10002(buffer);
		}

		return null;
	}

	@Override
	public String toJsonString(MGFightSpriteBuffMgr persistenceObject) {
		return toJsonVer10000(persistenceObject);
	}

	@Override
	public MGFightSpriteBuffMgr fromJsonString(String persistenceJsonString) {
		return fromJsonStringVer10000(persistenceJsonString);
	}

	private byte[] toBytesVer10001(MGFightSpriteBuffMgr persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version + 2);
		int count = 0;
		for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
			if (buff.getFightSpriteBuffRef().isNeedSaveBuff() && buff.getAttachFightSprite() == player) {
				count++;
			}
		}
		buffer.writeInt(count);
		for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
			if (buff.getFightSpriteBuffRef().isNeedSaveBuff() && buff.getAttachFightSprite() == player) {
				toBytes(buffer, buff);
			}
		}
		byte[] data = buffer.getData();
		return data;
	}

	private MGFightSpriteBuffMgr fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			String buffRefId = buffer.readString();
			long lastAffectTime = buffer.readLong();
			long expireTime = buffer.readLong();
			long createTime = buffer.readLong();
			int length = buffer.readInt();
			byte[] properties = buffer.readBytes(length);
			MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
			MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef, player, player);
			buff.setLastAffectTime(lastAffectTime);
			buff.setExpiration(expireTime);
			buff.setCreateTime(createTime);
			buff.getSpecialProperty().loadDictionary(properties);
			fightSpriteBuffMgr.getFightSpriteBuffList().add(buff);

		}
		return fightSpriteBuffMgr;
	}

	private MGFightSpriteBuffMgr fromBytesVer10001(ByteArrayReadWriteBuffer buffer) {
		fightSpriteBuffMgr.getFightSpriteBuffList().clear();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			String buffRefId = buffer.readString();
			long lastAffectTime = buffer.readLong();
			byte durationType = buffer.readByte();
			long duration = buffer.readLong();
			long createTime = buffer.readLong();
			int length = buffer.readInt();
			byte[] properties = buffer.readBytes(length);
			MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
			MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef, player, player);
			buff.setLastAffectTime(lastAffectTime);
			buff.setDuration(duration);
			buff.setCreateTime(createTime);
			buff.getSpecialProperty().loadDictionary(properties);
			fightSpriteBuffMgr.getFightSpriteBuffList().add(buff);
			if (buff.getFightSpriteBuffRef().isChangeFightValueBuff() && buff.getFightSpriteBuffRef().getAttachClosure() != null) {
				buff.attach();
			}
		}
		return fightSpriteBuffMgr;
	}
	private MGFightSpriteBuffMgr fromBytesVer10002(ByteArrayReadWriteBuffer buffer) {
		fightSpriteBuffMgr.getFightSpriteBuffList().clear();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			String buffRefId = buffer.readString();
			long lastAffectTime = buffer.readLong();
			long duration = buffer.readLong();
			long absoluteDuration = buffer.readLong();
			long createTime = buffer.readLong();
			int length = buffer.readInt();
			byte[] properties = buffer.readBytes(length);
			MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
			MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef, player, player);
			buff.setLastAffectTime(lastAffectTime);
			buff.setDuration(duration);
			buff.setAbsoluteDuration(absoluteDuration);
			buff.setCreateTime(createTime);
			buff.getSpecialProperty().loadDictionary(properties);
			fightSpriteBuffMgr.getFightSpriteBuffList().add(buff);
			if (buff.getFightSpriteBuffRef().getAttachClosure() != null) {
				buff.setNotify(false);
				buff.attach();
				buff.setNotify(true);
			}
		}
		return fightSpriteBuffMgr;
	}
	private String toJsonVer10000(MGFightSpriteBuffMgr persistenceObject) {
		return null;
	}

	private MGFightSpriteBuffMgr fromJsonStringVer10000(String persistenceJsonString) {
		return null;
	}

	private ByteArrayReadWriteBuffer toBytes(ByteArrayReadWriteBuffer buffer, MGFightSpriteBuff buff) {

		String buffRefId = buff.getFightSpriteBuffRef().getId();
		long lastAffectTime = buff.getLastAffectTime();
		long duration = buff.getDuration();
		long absoluteDuration = buff.getAbsoluteDuration();
		long createTime = buff.getCreateTime();
		buffer.writeString(buffRefId);
		buffer.writeLong(lastAffectTime);
		buffer.writeLong(duration);
		buffer.writeLong(absoluteDuration);
		buffer.writeLong(createTime);
		byte[] properties = buff.getSpecialProperty().toByteArray();
		buffer.writeInt(properties.length);
		buffer.writeBytes(properties);
		return buffer;
	}

}
