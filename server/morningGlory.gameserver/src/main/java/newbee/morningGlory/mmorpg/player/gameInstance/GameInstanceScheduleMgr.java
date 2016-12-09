package newbee.morningGlory.mmorpg.player.gameInstance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import newbee.morningGlory.mmorpg.gameInstance.persistence.GameInstanceDao;
import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;

/**
 * 副本完成管理(玩家的记录)
 * 
 */
public final class GameInstanceScheduleMgr {
	
	private static final Logger logger = Logger.getLogger(GameInstanceScheduleMgr.class);
	
	// 玩家ID，recordMgr
	private ConcurrentHashMap<String, MGPlayerGameInstanceRecordMgr> schedules = new ConcurrentHashMap<String, MGPlayerGameInstanceRecordMgr>();// 所有玩家的副本进程记录

	/**
	 * 获取我的副本进度记录
	 * 
	 * @param player
	 * @return
	 */
	public MGPlayerGameInstanceRecordMgr getMyRecord(Player player) {
		String playerId = player.getId();
		MGPlayerGameInstanceRecordMgr r = this.schedules.get(playerId);
		if (r == null) {
			List<CountRecord> records = GameInstanceDao.getInstance().getRecordsByPlayerId(playerId);// 查询数据库
			r = new MGPlayerGameInstanceRecordMgr();
			r.pack(records);
			this.schedules.putIfAbsent(playerId, r);
		}

		return this.schedules.get(playerId);
	}

	private MGPlayerGameInstanceRecord getGameInstanceRecord(Player player, String gameInstanceRefId) {
		MGPlayerGameInstanceRecordMgr mgr = getMyRecord(player);
		MGPlayerGameInstanceRecord gameInstanceRecord = mgr.getRecord(gameInstanceRefId);
		if (gameInstanceRecord == null) {
			gameInstanceRecord = new MGPlayerGameInstanceRecord(gameInstanceRefId);
			CountRecord countRecord = new CountRecord(gameInstanceRefId);
			gameInstanceRecord.setCountRecord(countRecord);
			insertRecord(countRecord, player);// 插入数据库
			mgr.addRecord(gameInstanceRefId, gameInstanceRecord);
		}
		return gameInstanceRecord;
	}

	/**
	 * 增加一次记录
	 * 
	 * @param refId
	 */
	public void addInstanceRecord(Player player, String gameInstanceRefId) {
		MGPlayerGameInstanceRecord gameInstanceRecord = getGameInstanceRecord(player, gameInstanceRefId);
		gameInstanceRecord.getCountRecord().addEnterRecord(player);// 增加一次记录
		// GameInstanceDao.getInstance().updateRecord(
		// gameInstanceRecord.getCountRecord(), player.getId());// 更新数据库

	}

	/**
	 * 返回当周进入副本次数
	 * 
	 * @param refId
	 * @return
	 */
	public int getInstanceRecordInWeek(Player player, String gameInstanceRefId) {
		MGPlayerGameInstanceRecord gameInstanceRecord = getGameInstanceRecord(player, gameInstanceRefId);
		CountRecord timeRecord = gameInstanceRecord.getCountRecord();
		if (timeRecord == null)
			return 0;
		return timeRecord.getTimesInWeek(true);
	}

	/**
	 * 返回当天进入副本次数
	 * 
	 * @param refId
	 * @return
	 */
	public int getInstanceRecordInDay(Player player, String gameInstanceRefId) {
		MGPlayerGameInstanceRecord gameInstanceRecord = getGameInstanceRecord(player, gameInstanceRefId);
		CountRecord timeRecord = gameInstanceRecord.getCountRecord();
		if (timeRecord == null)
			return 0;
		return timeRecord.getTimesInDay(true);
	}

	public void destroy() {
		schedules.clear();
	}

	public void clearGameInstanceResource(String playerId) {
		MGPlayerGameInstanceRecordMgr r = this.schedules.get(playerId);
		if (r != null) {
			r.clearRecord();
			this.schedules.remove(playerId);
		}
	}

	public Map<String, MGPlayerGameInstanceRecordMgr> getSchedules() {
		return schedules;
	}

	public void setSchedules(ConcurrentHashMap<String, MGPlayerGameInstanceRecordMgr> schedules) {
		this.schedules = schedules;
	}

	public void saveGameInstanceData() throws Exception {
		if (schedules.isEmpty()) {
			return;
		}

		for (String playerId : schedules.keySet()) {
			MGPlayerGameInstanceRecordMgr mgr = schedules.get(playerId);
			if (mgr == null) {
				continue;
			}

			Map<String, MGPlayerGameInstanceRecord> gameInstanceRecordsMap = mgr.getGameInstanceRecords();
			if (gameInstanceRecordsMap != null && gameInstanceRecordsMap.size() > 0) {
				for (String gameInstanceRefId : gameInstanceRecordsMap.keySet()) {
					MGPlayerGameInstanceRecord playerGameInstanceRecord = gameInstanceRecordsMap.get(gameInstanceRefId);
					updateRecord(playerGameInstanceRecord, playerId);
				}
			}

			// 检查这个人是否在线，不在线则清理数据
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			if (player == null) {
				clearGameInstanceResource(playerId);
			}
		}
	}
	
	private void insertRecord(CountRecord countRecord, Player player) {
		try {
			GameInstanceDao.getInstance().insertRecord(countRecord, player);// 插入数据库
		} catch (Exception e) {
			logger.error("insertRecord error, player=" + player + ", countRecord=" + countRecord);
			logger.error("insertRecord error, " + DebugUtil.printStack(e));
		}
	}

	private void updateRecord(MGPlayerGameInstanceRecord playerGameInstanceRecord , String playerId) {
		if (playerGameInstanceRecord != null) {
			CountRecord record = playerGameInstanceRecord.getCountRecord();
			try {
				GameInstanceDao.getInstance().updateRecord(record, playerId);
			} catch (Exception e) {
				logger.error("updateRecord error, playerId=" + playerId);
				logger.error("updateRecord error, " + DebugUtil.printStack(e));
			}
		}
	}

}
