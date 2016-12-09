package newbee.morningGlory.mmorpg.player.gameInstance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MGPlayerGameInstanceRecordMgr {

	// gameInstanceRefId,record
	private Map<String, MGPlayerGameInstanceRecord> gameInstanceRecords = new ConcurrentHashMap<String, MGPlayerGameInstanceRecord>();// 玩家所有的副本进程记录

	/**
	 * 获取我的副本进度记录
	 * 
	 * @param p
	 * @return
	 */
	public MGPlayerGameInstanceRecord getRecord(String gameInstanceRefId) {
		MGPlayerGameInstanceRecord r = this.gameInstanceRecords.get(gameInstanceRefId);
		return r;
	}

	public void addRecord(String gameInstanceRefId, MGPlayerGameInstanceRecord record) {
		gameInstanceRecords.put(gameInstanceRefId, record);
	}

	public void clearRecord() {
		gameInstanceRecords.clear();
	}

	/**
	 * 每天凌晨进行一次重置清理,当玩家不下线的时候(如果玩家下线,在数据入库时要清理)
	 */
	public void clear() {
		if (gameInstanceRecords != null && gameInstanceRecords.size() > 0) {
			MGPlayerGameInstanceRecord t;
			for (String key : gameInstanceRecords.keySet()) {
				t = gameInstanceRecords.get(key);
				if (t.getCountRecord().isCanRemove())
					gameInstanceRecords.remove(key);
			}
		}
	}

	public void pack(List<CountRecord> records) {
		if (records == null || records.size() == 0) {
			return;
		}
		for (CountRecord countRecord : records) {
			MGPlayerGameInstanceRecord playerRecord = new MGPlayerGameInstanceRecord(countRecord.getRefId());
			playerRecord.setCountRecord(countRecord);
			this.gameInstanceRecords.put(countRecord.getRefId(), playerRecord);
		}
	}

	public Map<String, MGPlayerGameInstanceRecord> getGameInstanceRecords() {
		return gameInstanceRecords;
	}

	public void setGameInstanceRecords(Map<String, MGPlayerGameInstanceRecord> gameInstanceRecords) {
		this.gameInstanceRecords = gameInstanceRecords;
	}
	
	public void resetAllDayRecord() {
		Collection<MGPlayerGameInstanceRecord> records = gameInstanceRecords.values();
		for (MGPlayerGameInstanceRecord gameInstanceRecord : records) {
			gameInstanceRecord.resetDayRecord();
		}
	}
}
