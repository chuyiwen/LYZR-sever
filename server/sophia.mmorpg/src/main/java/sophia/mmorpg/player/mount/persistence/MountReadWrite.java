package sophia.mmorpg.player.mount.persistence;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public class MountReadWrite extends AbstractPersistenceObjectReadWrite<Mount> implements PersistenceObjectReadWrite<Mount> {

	@Override
	public byte[] toBytes(Mount persistenceObject) {
		return toBytesVer10000(persistenceObject);
	}

	@Override
	public Mount fromBytes(byte[] persistenceBytes) {
		if (persistenceBytes == null || persistenceBytes.length == 0)
			return null;
		return fromBytesVer10000(persistenceBytes);
	}

	@Override
	public String toJsonString(Mount persistenceObject) {
		return toJsonVer10000(persistenceObject);
	}

	@Override
	public Mount fromJsonString(String persistenceJsonString) {
		return fromJsonStringVer10000(persistenceJsonString);
	}

	private byte[] toBytesVer10000(Mount persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeInt(Default_Write_Version);
		buffer.writeLong(persistenceObject.getExp()); // 坐骑经验
		buffer.writeString(persistenceObject.getCrtRefId()); // 坐骑RefId
		buffer.writeByte(persistenceObject.getMountState());// 坐骑状态（上马／下马）
		byte[] data = buffer.getData();
		return data;
	}

	private Mount fromBytesVer10000(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		Mount mount = null;
		if (buffer.hasRemaining()) {
			int writeVersion = buffer.readInt();
			long exp = buffer.readLong();// 坐骑经验
			String mountRefId = buffer.readString(); // 坐骑RefId
			byte mountState = buffer.readByte();// 坐骑状态（上马／下马）
			mount = GameObjectFactory.getMount(mountRefId);
			mount.setExp(exp);
			mount.setMountState(mountState);
		}
		return mount;
	}

	private String toJsonVer10000(Mount persistenceObject) {
		return null;
	}

	private Mount fromJsonStringVer10000(String persistenceJsonString) {
		return null;
	}
}
