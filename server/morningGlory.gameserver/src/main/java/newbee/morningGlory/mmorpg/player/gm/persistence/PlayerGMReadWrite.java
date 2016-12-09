package newbee.morningGlory.mmorpg.player.gm.persistence;

import newbee.morningGlory.mmorpg.player.gm.MGPlayerGMMgr;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;
import sophia.mmorpg.player.state.PlayerStateMgr;

public class PlayerGMReadWrite extends AbstractPersistenceObjectReadWrite<MGPlayerGMMgr> implements PersistenceObjectReadWrite<MGPlayerGMMgr> {
	
	private MGPlayerGMMgr playerGMMgr;

	public PlayerGMReadWrite(MGPlayerGMMgr playerGMMgr) {
		this.playerGMMgr = playerGMMgr;
	}
	
	@Override
	public byte[] toBytes(MGPlayerGMMgr persistenceObject) {
		return toBytesVer10000(persistenceObject);
	}

	@Override
	public MGPlayerGMMgr fromBytes(byte[] persistenceBytes) {
		return fromBytesVer10000(persistenceBytes);
	}

	@Override
	public String toJsonString(MGPlayerGMMgr persistenceObject) {
		return toJsonVer10000(persistenceObject);
	}

	@Override
	public MGPlayerGMMgr fromJsonString(String persistenceJsonString) {
		return fromJsonStringVer10000(persistenceJsonString);
	}

	private byte[] toBytesVer10000(MGPlayerGMMgr persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeInt(Default_Write_Version);
		PlayerStateMgr playerStateMgr = persistenceObject.getPlayer().getPlayerStateMgr();
		buffer.writeByte(playerStateMgr.getStates());
		byte[] data = buffer.getData();
		return data;
	}

	private MGPlayerGMMgr fromBytesVer10000(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		if (buffer.hasRemaining()) {
			buffer.readInt();
			byte states = buffer.readByte();// 玩家状态(封号/禁言)由GM控制
			playerGMMgr.getPlayer().getPlayerStateMgr().setStates(states);
		} 
		
		return playerGMMgr;
	}

	private String toJsonVer10000(MGPlayerGMMgr persistenceObject) {
		return null;
	}

	private MGPlayerGMMgr fromJsonStringVer10000(String persistenceJsonString) {
		return null;
	}

}
