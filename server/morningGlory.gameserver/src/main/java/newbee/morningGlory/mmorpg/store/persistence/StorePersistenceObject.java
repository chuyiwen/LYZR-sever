package newbee.morningGlory.mmorpg.store.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import sophia.foundation.util.ByteArrayReadWriteBuffer;

public class StorePersistenceObject {

	private static StorePersistenceObject instance = new StorePersistenceObject();

	public static StorePersistenceObject getInstance() {
		return instance;
	}

	private StorePersistenceObject() {
	}

	public byte[] allLimitDataToBytes(Map<String, Short> allLimit) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeInt(allLimit.size());
		for (Entry<String, Short> item : allLimit.entrySet()) {
			buffer.writeString(item.getKey());
			buffer.writeShort(item.getValue());
		}
		return buffer.getData();
	}
	
	public Map<String, Short> allLimitDataFromBytes(byte[] allLimit) {
		Map<String, Short> limit = new HashMap<String, Short>();
		ByteArrayReadWriteBuffer allLimitBuffer = new ByteArrayReadWriteBuffer(allLimit);
		int count = allLimitBuffer.readInt();
		for (int i = 0; i < count; i++) {
			String itemRefId = allLimitBuffer.readString();
			short itemCount = allLimitBuffer.readShort();
			limit.put(itemRefId, itemCount);
		}
		return limit;
	}

	public byte[] personalLimitDataToBytes(Map<String, ConcurrentHashMap<String, Short>> personalLimit) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeShort((short) personalLimit.size());
		for (Entry<String, ConcurrentHashMap<String, Short>> item : personalLimit.entrySet()) {
			buffer.writeString(item.getKey());
			buffer.writeShort((short) item.getValue().size());
			for (Entry<String, Short> person : item.getValue().entrySet()) {
				buffer.writeString(person.getKey());
				buffer.writeShort(person.getValue());
			}
		}
		return buffer.getData();
	}
	
	public Map<String, ConcurrentHashMap<String, Short>> personalLimitDataFromBytes(byte[] personalLimit) {
		Map<String, ConcurrentHashMap<String, Short>> limit = new HashMap<String, ConcurrentHashMap<String, Short>>();
		ByteArrayReadWriteBuffer personalLimitBuffer = new ByteArrayReadWriteBuffer(personalLimit);
		int count = personalLimitBuffer.readShort();
		for (int i = 0; i < count; i++) {
			String itemRefId = personalLimitBuffer.readString();
			short size = personalLimitBuffer.readShort();
			ConcurrentHashMap<String, Short> personNum = new ConcurrentHashMap<>();
			for (short j = 0; j < size; j++) {
				String playerId = personalLimitBuffer.readString();
				short num = personalLimitBuffer.readShort();
				personNum.put(playerId, num);
			}
			limit.put(itemRefId, personNum);
		}
		return limit;
	}
	
}
