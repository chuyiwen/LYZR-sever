package newbee.morningGlory.mmorpg.sortboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeActivityMgr;
import newbee.morningGlory.mmorpg.sortboard.persistence.SortboardDAO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.utils.DateTimeUtil;

public class SortboardMgr {
	private static Logger logger = Logger.getLogger(SortboardMgr.class);
	public static final int SubListLimitSize = 50;
	public static final int SortboardLengh = 1000;
	public static final int PlayerTypeStart = 100;
	public static final int[] TypeStarts = new int[] { PlayerTypeStart };

	public Map<Integer, String> topThree = new HashMap<>();
	// 排行榜分类
	private List<Sortboard> sbs = new ArrayList<Sortboard>();
	// 排行榜数据版本信息
	private Map<SortboardType, Integer> versionList = new HashMap<>();
	// 当前排行榜数据
	private Map<Integer, SortboardData> sbd = new HashMap<>();
	// 用于缓存排行榜数据，当PlayerManager清除玩家缓存时，会产生新的排行榜缓存数据
	private Map<Integer, SortboardData> sbdCache = new HashMap<>();
	// 战力榜数据
	private Map<Integer, SortboardData> fightPowerBoard = new HashMap<>();
	// 战力榜缓存数据
	private Map<Integer, SortboardData> fightPowerBoardCache = new HashMap<>();

	private List<SFTimer> timers = new ArrayList<>();

	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();

	private static SortboardMgr instance = new SortboardMgr();

	public static SortboardMgr getInstance() {
		return instance;
	}

	private SortboardMgr() {
	}

	private Sortboard createSortboard(SortboardType sortboardType) {
		Sortboard sortboard = null;
		try {
			sortboard = sortboardType.getClazz().newInstance();
		} catch (Exception e) {
			logger.error(e);
			return sortboard;
		}

		sortboard.type = sortboardType;
		sortboard.size = SortboardLengh;
		sortboard.name = sortboardType.getName();
		sortboard.init();

		return sortboard;
	}

	private SortboardData createSortboardData(SortboardType sortboardType) {
		SortboardData boardData = new SortboardData();
		boardData.setScoreData(new ArrayList<SortboardScoreData>());
		boardData.setType(sortboardType);
		return boardData;
	}

	public void initialize() {
		SortboardType[] values = SortboardType.values();
		for (SortboardType sortboardType : values) {
			Sortboard sortboard = createSortboard(sortboardType);
			sbs.add(sbs.size(), sortboard);
			SortboardData sortboardData = createSortboardData(sortboardType);
			putCacheSortboardData(sortboardData);
			versionList.put(sortboardType, 1000);
		}

		startTimer();
		
		try {
			load();
		} catch (Exception e) {
			logger.error("load sortBoard Data Error!!!" + e);
			e.printStackTrace();
		}

	}
	
	private void startTimer() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		SFTimer timer = timerCreater.hourCalendarChime(new SortboardTimer());
		timers.add(timer);
		timer = timerCreater.minuteCalendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				Collection<Player> playerList = playerManager.getPlayerList();
				int curOfflineCount = playerList.size() - PlayerManager.getOnlineTotalCount();
				if (curOfflineCount >= PlayerManager.getMaxCachePlayerCount()) {
					logger.info("SortboardMgr clean player cache list");
					// 备份需要
					ArrayList<Player> backupPlayerList = new ArrayList<>(playerList);
					// 排行榜排序
					try {
						newCacheSortBoardData();
					} catch (Exception e) {
						logger.error("newCacheSortBoardData error" + e);
					}
					// 清除缓存多余的玩家
					for (Player player : backupPlayerList) {
						playerManager.clearPlayer(player);
					}
				}
			}

			@Override
			public void handleServiceShutdown() {
			}
		});
	}

	public void stopTimer() {
		for (SFTimer timer : timers) {
			timer.cancel();
			if (logger.isDebugEnabled()) {
				logger.debug("SortboardMgr was terminated.");
			}
		}
	}

	private void load() throws Exception {
		ConcurrentHashMap<Integer, SortboardData> selectData = SortboardDAO.getInstance().selectData();
		Collection<SortboardData> sortboardDataList = selectData.values();
		for (SortboardData sortboardData : sortboardDataList) {
			putCacheSortboardData(sortboardData);
			if (logger.isDebugEnabled()) {
				logger.debug("SortBoardType: " + sortboardData.getType() + " Recover From DataBase");
			}
		}

		newSortBoardData();
	}

	public int getVersionByBoardType(SortboardType type) {
		if (versionList.get(type) == null) {
			logger.error("Do not has Type:" + type.getSortboardType() + " Sortboard Version.");
			return -1;
		}

		return versionList.get(type);
	}

	public Sortboard getSortboard(SortboardType type) {
		for (int i = 0; i < sbs.size(); i++) {
			Sortboard sb = (Sortboard) sbs.get(i);
			if (type == sb.getType()) {
				return sb;
			}
		}

		return null;
	}

	public Map<Integer, SortboardData> getFightPowerSubBoard() {
		return fightPowerBoard;
	}

	public void putSortboardData(SortboardData data) {
		sbd.put(data.getType().getSortboardType(), data);
	}

	public SortboardData getSortboardData(SortboardType type) {
		return sbd.get(type.getSortboardType());
	}

	public void putCacheSortboardData(SortboardData data) {
		sbdCache.put(data.getType().getSortboardType(), data);
	}

	public SortboardData getCacheSortboardData(SortboardType type) {
		return sbdCache.get(type.getSortboardType());
	}

	public void putFightPowerSortboardData(int profession, SortboardData data) {
		fightPowerBoard.put(profession, data);
	}

	public SortboardData getFightPowerSortboardData(int profession) {
		return fightPowerBoard.get(profession);
	}

	public void putCacheFightPowerSortboardData(int profession, SortboardData data) {
		fightPowerBoardCache.put(profession, data);
	}

	public SortboardData getCacheFightPowerSortboardData(int profession) {
		return fightPowerBoardCache.get(profession);
	}

	public void putTopThreeProfessionPlayer(int profession, String playerId) {
		topThree.put(profession, playerId);
	}

	public Map<Integer, String> getTopThreeProfessionPlayer() {
		return topThree;
	}

	public synchronized void newSortBoardData() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("SortBoard run, time=" + DateTimeUtil.getDateDetailString(Calendar.getInstance().getTimeInMillis()));
		}

		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> playerList = playerManager.getPlayerList();
		EnumSet<SortboardType> currEnumSet = EnumSet.allOf(SortboardType.class);
		for (SortboardType sortboardType : currEnumSet) {
			newSortBoardDataBy(getSortboard(sortboardType), playerList);
		}
	}

	public synchronized void newSortBoardDataByType(SortboardType sortboardType) throws Exception {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> playerList = playerManager.getPlayerList();
		newSortBoardDataBy(getSortboard(sortboardType), playerList);
	}

	public synchronized void newSortBoardDataBy(Sortboard board, Collection<Player> playerList) throws Exception {
		SortboardType sortboardType = board.getType();
		int integer = versionList.get(sortboardType);
		versionList.put(sortboardType, integer + 1);

		newCacheSortBoardDataBy(board, playerList);
		SortboardData cacheSortboardData = getCacheSortboardData(sortboardType);
		SortboardData sortboardData = cacheSortboardData.clone();
		resort100(board, sortboardData.getScoreData());
		putSortboardData(sortboardData);

		if (sortboardType == SortboardType.PlayerFightPower) {

			SortboardData cacheFightPowerSortboardData = getCacheFightPowerSortboardData(PlayerConfig.WARRIOR);
			SortboardData fightPowerSortboardData = cacheFightPowerSortboardData.clone();
			putFightPowerSortboardData(PlayerConfig.WARRIOR, fightPowerSortboardData);

			cacheFightPowerSortboardData = getCacheFightPowerSortboardData(PlayerConfig.ENCHANTER);
			fightPowerSortboardData = cacheFightPowerSortboardData.clone();
			putFightPowerSortboardData(PlayerConfig.ENCHANTER, fightPowerSortboardData);

			cacheFightPowerSortboardData = getCacheFightPowerSortboardData(PlayerConfig.WARLOCK);
			fightPowerSortboardData = cacheFightPowerSortboardData.clone();
			putFightPowerSortboardData(PlayerConfig.WARLOCK, fightPowerSortboardData);

			List<SortboardScoreData> scoreDataList = sortboardData.getScoreData();
			newTopThreeProfessionPlayer(scoreDataList);
		}
	}

	public synchronized void newCacheSortBoardData() throws Exception {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> playerList = playerManager.getPlayerList();
		EnumSet<SortboardType> currEnumSet = EnumSet.allOf(SortboardType.class);
		for (SortboardType sortboardType : currEnumSet) {
			newCacheSortBoardDataBy(getSortboard(sortboardType), playerList);
		}
	}

	private synchronized void newCacheSortBoardDataBy(Sortboard board, Collection<Player> playerList) throws Exception {
		SortboardType sortboardType = board.getType();
		SortboardData sortboardData = getCacheSortboardData(board.getType());
		List<SortboardScoreData> scoreDataList = sortboardData.getScoreData();
		for (Player player : playerList) {
			updatePlayerScore(player, board, scoreDataList);
		}

		if (sortboardType == SortboardType.PlayerFightPower) {
			newCacheFightPowerSortboardData();
		}

		Collections.sort(scoreDataList);
		if (scoreDataList.size() > SortboardLengh) {
			scoreDataList = new ArrayList<>(scoreDataList.subList(0, SortboardLengh - 1));
		}

		sortboardData.setScoreData(scoreDataList);

		int selectCount = SortboardDAO.getInstance().selectCount(sortboardType);
		if (selectCount == 0) {
			insertDataTo(sortboardData, LimitTimeActivityMgr.Sortboard_Type);
		} else {
			updataDataTo(sortboardData, LimitTimeActivityMgr.Sortboard_Type);
		}
	}

	private synchronized void newCacheFightPowerSortboardData() {
		SortboardData sortboardData = getCacheSortboardData(SortboardType.PlayerFightPower);
		List<SortboardScoreData> scoreDataList = sortboardData.getScoreData();
		newCacheFightPowerSortboardDataBy(PlayerConfig.WARRIOR, scoreDataList);
		newCacheFightPowerSortboardDataBy(PlayerConfig.ENCHANTER, scoreDataList);
		newCacheFightPowerSortboardDataBy(PlayerConfig.WARLOCK, scoreDataList);
	}

	private synchronized void newCacheFightPowerSortboardDataBy(int profession, List<SortboardScoreData> scoreDataList) {
		SortboardData fightPowerBoardData = new SortboardData();
		List<SortboardScoreData> list = new ArrayList<>();
		int count = SubListLimitSize;
		for (SortboardScoreData data : scoreDataList) {
			if (data.getProfession() == profession) {
				list.add(data);
			}
			if (--count <= 0) {
				break;
			}
		}

		fightPowerBoardData.setScoreData(list);
		fightPowerBoardCache.put(profession, fightPowerBoardData);
	}

	private synchronized void newTopThreeProfessionPlayer(List<SortboardScoreData> scoreDataList) {
		Map<Integer, String> topThree = new HashMap<>();
		for (int i = 0; i < scoreDataList.size(); i++) {
			SortboardScoreData scoreData = scoreDataList.get(i);
			int profession = scoreData.getProfession();
			if (!topThree.containsKey(profession)) {
				topThree.put(profession, scoreData.getPlayerId());
			}
			if (topThree.size() == 3) {
				break;
			}
		}

		this.topThree = topThree;
	}

	/**
	 * 更新（新增）玩家在排行榜上的分数
	 * 
	 * @param player
	 * @param board
	 * @param scoreDataList
	 */
	private void updatePlayerScore(Player player, Sortboard board, List<SortboardScoreData> scoreDataList) {
		int score = board.getScore(player);
		if (score <= 0) {
			return;
		}

		for (SortboardScoreData scoreData : scoreDataList) {
			if (StringUtils.equals(scoreData.getPlayerId(), player.getId())) {
				scoreData.setScore(score);
				return;
			}
		}

		scoreDataList.add(board.getSortboard(player));
	}

	/**
	 * 重复校验排行榜前100名数据
	 * 
	 * @param scoreDataList
	 * @param board
	 */
	private void resort100(Sortboard board, List<SortboardScoreData> scoreDataList) {
		for (int i = 0; i < 100 && i < scoreDataList.size(); i++) {
			SortboardScoreData sortboardScoreData = scoreDataList.get(i);
			String playerId = sortboardScoreData.getPlayerId();
			Player player = playerManager.getPlayer(playerId);
			if (player != null) {
				sortboardScoreData.setScore(board.getScore(player));
			}
		}

		Collections.sort(scoreDataList);
	}

	public synchronized void insertDataTo(SortboardData sortboardData, byte data_type) {
		try {
			SortboardDAO.getInstance().insertData(sortboardData, data_type);
		} catch (Exception e) {
			logger.error("insertDataTo error, data_type=" + data_type);
			logger.error("insertDataTo error, " + DebugUtil.printStack(e));
		}
	}

	public synchronized void updataDataTo(SortboardData sortboardData, byte data_type) {
		try {
			SortboardDAO.getInstance().updateData(sortboardData, data_type);
		} catch (Exception e) {
			logger.error("updataDataTo error, data_type=" + data_type);
			logger.error("updataDataTo error, " + DebugUtil.printStack(e));
		}
	}

}
