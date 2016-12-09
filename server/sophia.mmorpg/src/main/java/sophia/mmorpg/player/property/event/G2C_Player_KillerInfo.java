package sophia.mmorpg.player.property.event;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Player_KillerInfo extends ActionEventBase {
	private static final Logger logger = Logger.getLogger(G2C_Player_KillerInfo.class);
	
	private String killerCharId;
	private String killerName;
	private byte killerType;
	private long deadTime;
	private int killerLevel;
	private byte killerOccupa;
	private int killerFightPower;
	private Map<String, Integer> lootItemRefMap;

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, killerCharId);
		putString(buffer, killerName);
		buffer.put(killerType);
		buffer.putInt(killerLevel);
		buffer.put(killerOccupa);
		buffer.putInt(killerFightPower);
		buffer.putLong(deadTime);
		int size = lootItemRefMap.size();
		if (logger.isDebugEnabled()) {
			logger.debug("lootItemRefMap size=" + size);
		}

		buffer.put((byte)size);
		Set<Entry<String,Integer>> entrySet = lootItemRefMap.entrySet();
		Iterator<Entry<String, Integer>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next();
			putString(buffer, entry.getKey());
			buffer.putInt(entry.getValue());
		}
		
		return buffer;
	}

	public String getKillerName() {
		return killerName;
	}

	public void setKillerName(String killerName) {
		this.killerName = killerName;
	}

	public byte getKillerType() {
		return killerType;
	}

	public void setKillerType(byte killerType) {
		this.killerType = killerType;
	}

	public long getDeadTime() {
		return deadTime;
	}

	public void setDeadTime(long deadTime) {
		this.deadTime = deadTime;
	}

	public String getKillerCharId() {
		return killerCharId;
	}

	public void setKillerCharId(String killerCharId) {
		this.killerCharId = killerCharId;
	}

	public Map<String, Integer> getLootItemRefMap() {
		return lootItemRefMap;
	}

	public void setLootItemRefMap(Map<String, Integer> lootItemRefMap) {
		this.lootItemRefMap = lootItemRefMap;
	}

	public int getKillerLevel() {
		return killerLevel;
	}

	public void setKillerLevel(int killerLevel) {
		this.killerLevel = killerLevel;
	}

	public byte getKillerOccupa() {
		return killerOccupa;
	}

	public void setKillerOccupa(byte killerOccupa) {
		this.killerOccupa = killerOccupa;
	}

	public int getKillerFightPower() {
		return killerFightPower;
	}

	public void setKillerFightPower(int killerFightPower) {
		this.killerFightPower = killerFightPower;
	}
}
