package newbee.morningGlory.mmorpg.player.activity.limitTimeRank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.RankBeginEndTimeRef;
import newbee.morningGlory.mmorpg.sceneActivities.chime.Chime;
import newbee.morningGlory.mmorpg.sceneActivities.chime.DateChime;
import newbee.morningGlory.mmorpg.sortboard.SortboardData;
import newbee.morningGlory.mmorpg.sortboard.SortboardMgr;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import newbee.morningGlory.mmorpg.sortboard.SortboardType;
import newbee.morningGlory.mmorpg.sortboard.persistence.SortboardDAO;

import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SpecialEffectsType;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.chat.sysytem.SystemPromptPosition;

public class LimitTimeActivityMgr {
	private static Logger logger = Logger.getLogger(LimitTimeActivityMgr.class);

	// <refId, rankType>
	private static Map<String, Byte> limitTimeDataMaps = new HashMap<String, Byte>();

	// < rankType, refId> 1-->战力 2-->等级 5-->翅膀 6-->坐骑
	private static Map<Byte, String> beginEndTimeMaps = new HashMap<Byte, String>();

	// < rankType, sortboardData> rankType == sortBoardType.value()
	private static ConcurrentHashMap<Integer, SortboardData> copySBD = new ConcurrentHashMap<Integer, SortboardData>();

	public static Map<Byte, Integer> version = new HashMap<Byte, Integer>();

	public static List<LimitRankType> limitRankTypes = new ArrayList<LimitRankType>();

	public static LimitTimeActivityMgr instance = new LimitTimeActivityMgr();

	private List<SFTimer> timers = new ArrayList<SFTimer>();

	public static final byte Begin = 0;
	public static final byte End = 1;
	public static final byte Sortboard_Type = 1;
	public static final byte LimitRank_Type = 2;

	private LimitTimeActivityMgr() {
	}

	static {
		limitRankTypes.add(LimitRankType.PlayerFightPower);
		limitRankTypes.add(LimitRankType.PlayerLvl);
		limitRankTypes.add(LimitRankType.PlayerWingLvl);
		limitRankTypes.add(LimitRankType.MountLvl);
	}

	public static LimitTimeActivityMgr getInstance() {
		return instance;
	}

	private void startTimer(final LimitRankType limitRankType) {
		if (logger.isDebugEnabled()) {
			logger.debug("start limitRank hour timer! limitRankType:" + limitRankType);
		}

		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		SFTimer timer = timerCreater.hourCalendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				logger.debug("one hour over!");
				handle_TimeCountDown(limitRankType);
			}

			@Override
			public void handleServiceShutdown() {
			}

		});

		SFTimer marqueeTimer = timerCreater.minuteCalendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				handle_MarqueeCountDown(limitRankType);
			}

			@Override
			public void handleServiceShutdown() {
			}
		});

		timers.add(timer);
		timers.add(marqueeTimer);

	}
	
	public void stopTimer() {
		for (SFTimer timer : timers) {
			timer.cancel();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(" limitTimeRankTimer was terminated.");
		}
	}

	private void startOverActivtiyTimer(final LimitRankType limitRankType) {
		String endTime = getBeginOrEndTime(limitRankType.value(), End);
		// yyyyMMddHHmmss
		int day = Integer.parseInt(endTime.substring(6, 8));
		int hour = Integer.parseInt(endTime.substring(8, 10));
		if (logger.isDebugEnabled()) {
			logger.debug("day = " + day + ",hour =" + hour);
		}

		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		SFTimer overTimer = timerCreater.monthCalendarChime(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				SortboardType sortboardType = getSortboardType(limitRankType.value());
				SortboardData sortboardData = SortboardMgr.getInstance().getSortboardData(sortboardType);
				putSortboardData(sortboardType, sortboardData);
				setVersionNumber(limitRankType.value());
				if (logger.isDebugEnabled()) {
					logger.debug("限时冲榜时间到!!! limitRankType = " + limitRankType.value());
					logger.debug("最新版本号:" + version.get(limitRankType.value()));
				}

				// insert db
				if (selectOverTimeData(limitRankType) == null) {
					insertOverTimeData(limitRankType);
				}
			}

			@Override
			public void handleServiceShutdown() {

			}
		}, day, hour);
		timers.add(overTimer);
	}

	private void handle_MarqueeCountDown(LimitRankType limitRankType) {
		String rankTimeRefId = getBeginEndTimeMaps().get(limitRankType.value());
		RankBeginEndTimeRef rankTimeRef = (RankBeginEndTimeRef) GameRoot.getGameRefObjectManager().getManagedObject(rankTimeRefId);
		if (null == rankTimeRef) {
			logger.error("error rankTimeRefId = " + rankTimeRefId + " error limitRankType = " + LimitRank_Type);
		}

		if (isTimeOver(limitRankType)) {
			return;
		}
		Chime chime = null;
		if (rankTimeRef.getDateChimeList().size() > 0) {
			chime = rankTimeRef.getDateChimeList().get(0);
		}
		
		if (chime == null) {
			if (logger.isDebugEnabled()) {
				logger.error("empty chimeList: limitRankType = " + limitRankType);
			}
			
			return;
		}

		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.add(Calendar.HOUR, 1);
		boolean isPreOver = chime.checkEnd(crtCalendar);

		// 活动结束前一小时 走马灯提示
		if (isPreOver) {
			String limitRankName = LimitRankType.getLimitRankName(limitRankType.value());
			String content = String.format("%1$s活动还有1小时就结束啦，要拿大奖的朋友不要落后啦!", limitRankName);

			SystemPromptFacade.sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
			
			rankTimeRef.getDateChimeList().remove(0);
		}
	}

	private void handle_TimeCountDown(LimitRankType limitRankType) {
		if (!isTimeOver(limitRankType)) {
			if (logger.isDebugEnabled()) {
				logger.debug("one hour is over! reload limitRank data from sortboard! limitRankType = " + limitRankType.value());
			}

			SortboardType sortboardType = getSortboardType(limitRankType.value());
			SortboardData sortboardData = SortboardMgr.getInstance().getSortboardData(sortboardType);
			putSortboardData(sortboardType, sortboardData);
			setVersionNumber(limitRankType.value());
		}
	}

	public void init() {
		reInitTimeData();
		
		configSortBoardDataAndStartTimer();
	}
	
	private void reInitTimeData() {
		for(Entry<Byte, String> entry : beginEndTimeMaps.entrySet()) {
			String refId = entry.getValue();
			RankBeginEndTimeRef ref = (RankBeginEndTimeRef)GameRoot.getGameRefObjectManager().getManagedObject(refId);
			
			if (ref == null) {
				logger.error("error refId = " + entry.getValue());
				continue;
			}
			
			String beginTime = ref.getRankBeginTime();
			String endTime = ref.getRankEndTime();
			
			long openTimeMills = MorningGloryContext.getServerOpenTime();
			beginTime = timerParser(beginTime, openTimeMills);
			endTime = timerParser(endTime, openTimeMills);
			
			ref.setRankBeginTime(beginTime);
			ref.setRankEndTime(endTime);
			
			List<Chime> dateChimeList = parseDuringTime(beginTime, endTime);
			ref.setDateChimeList(dateChimeList);

		}
	}
	
	private String timerParser(String timeString, long mills) {
		// 48:00:00
		String[] timeArray = timeString.split(":");
		int hour = 0;
		int minute = 0;
		int second = 0;
		try {
			hour = Integer.parseInt(timeArray[0]);
			minute = Integer.parseInt(timeArray[1]);
			second = Integer.parseInt(timeArray[2]);

		} catch (Exception e) {
			logger.error("time parse error! timeString = " + timeString);
		}
		
		
		 long totalMills = mills + ( hour * 3600 + minute * 60 + second ) * 1000;
		 
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		 Calendar calendar = Calendar.getInstance();
//		 calendar.setTimeInMillis(totalMills);
		 calendar.setTimeInMillis(mills);
		 
		 calendar.add(Calendar.HOUR_OF_DAY,hour);
		 calendar.add(Calendar.MINUTE, minute);
		 calendar.add(Calendar.SECOND, second);
		 
		 String result = formatter.format(calendar.getTimeInMillis());
		 
		 if (logger.isDebugEnabled()) {
			 logger.debug("timeString result = " + result);
		 }
		 
		 return result;
		 
	}
	
	private List<Chime> parseDuringTime(String beginTime, String endTime) {
		List<Chime> dateChimeList = new ArrayList<Chime>();

		DateChime dateChime = readDateChime(beginTime, endTime);

		if (null != dateChime) {
			dateChimeList.add(dateChime);
		}

		return dateChimeList;
	}

	private DateChime readDateChime(String beginTime, String endTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date beginDate = null;
		Date endDate = null;
		try {
			beginDate = format.parse(beginTime);
			endDate = format.parse(endTime);

			return new DateChime(beginDate.getTime(), endDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void configSortBoardDataAndStartTimer() {
		for (LimitRankType limitRankType : limitRankTypes) {

			if (logger.isDebugEnabled()) {
				logger.debug("config limitRankType sortboardData! limitRankType = " + limitRankType.value());
			}

			SortboardData sortboardData = null;
			SortboardType sortboardType = getSortboardType(limitRankType.value());

			
			sortboardData = SortboardMgr.getInstance().getSortboardData(sortboardType);
			

			SortboardData overTimeSortboardData = selectOverTimeData(limitRankType);
			if (isTimeOver(limitRankType) && overTimeSortboardData != null) {
				sortboardData = overTimeSortboardData;
			} 

			if (null == sortboardData) {
				continue;
			}
			
			if (isTimeOver(limitRankType) && overTimeSortboardData == null) {
				SortboardMgr.getInstance().insertDataTo(sortboardData, LimitRank_Type);
			} 

			putSortboardData(sortboardType, sortboardData);
			setVersionNumber(limitRankType.value());
			startTimer(limitRankType);
			startOverActivtiyTimer(limitRankType);
		}
	}

	public void insertOverTimeData(LimitRankType limitRankType) {
		SortboardType sortboardType = getSortboardType(limitRankType.value());
		SortboardData sortboardData = copySBD.get(sortboardType.getSortboardType());
		if (sortboardData == null) {
			return;
		}
		
		SortboardMgr.getInstance().insertDataTo(sortboardData, LimitRank_Type);

		if (logger.isDebugEnabled()) {
			logger.debug("insert data into database! sortboardType = " + sortboardType.getSortboardType());
		}
	}

	public SortboardData selectOverTimeData(LimitRankType limitRankType) {
		SortboardType sortboardType = getSortboardType(limitRankType.value());
		return SortboardDAO.getInstance().selectLimitRankData(sortboardType.getSortboardType() + SortboardDAO.type_Difference);
	}

	private void setVersionNumber(byte type) {
		int ver = 0;
		if (version.get(type) != null) {
			ver = version.get(type);
		}
		version.put(type, ver + 1);
	}

	public static synchronized void putSortboardData(SortboardType type, SortboardData data) {
		copySBD.put(type.getSortboardType(), data.clone());
	}

	public static boolean isTimeOver(LimitRankType limitRankType) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String endTime = getBeginOrEndTime(limitRankType.value(), End);
		long now = System.currentTimeMillis();
		long end = 0;
		try {
			end = sdf.parse(endTime).getTime();
		} catch (ParseException e) {
			logger.error("time format error" + e);
			return false;
		}
		return now >= end;
	}

	public static String getBeginOrEndTime(byte limitRankType, byte timeType) {
		String rankTimeTypeRefId = beginEndTimeMaps.get(limitRankType);
		RankBeginEndTimeRef ref = getRankBeginEndTimeRef(rankTimeTypeRefId);
		if (ref == null) {
			logger.error("error limitRankType = " + limitRankType);
			return "";
		}

		if (timeType == Begin) {
			return ref.getRankBeginTime();
		} else {
			return ref.getRankEndTime();
		}
	}

	public int getRanking(SortboardType sortboardType, Player player) {
		if (player == null) {
			logger.error("argument player is null");
			return 0;
		}

		if (null == sortboardType) {
			logger.error("sortboardType is null!");
			return 0;
		}

		int type = sortboardType.getSortboardType();

		if (logger.isDebugEnabled()) {
			logger.debug("限时冲榜类型: sortboardType type = " + type);
		}

		SortboardData sortboardData = copySBD.get(type);
		if (sortboardData == null) {
			logger.error("sortboardData is null! sortboardType type = " + type);
			return 0;
		}

		List<SortboardScoreData> list = sortboardData.getScoreData();
		String pre = player.getId();
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				String playerId = list.get(i).getPlayerId();
				if (playerId.startsWith(pre)) {
					return i + 1;
				}
			}
		}
		return 0;
	}

	public static LimitRankType getLimitRankType(int type) {
		switch (type) {
		case 1:
			return LimitRankType.PlayerFightPower;
		case 2:
			return LimitRankType.PlayerLvl;
		case 3:
		case 4:
			return LimitRankType.PlayerMerit;
		case 5:
			return LimitRankType.PlayerWingLvl;
		case 6:
			return LimitRankType.MountLvl;
		case 7:
			return LimitRankType.TalismanLvl;
		default:
			return null;
		}
	}

	public static SortboardType getSortboardType(int type) {
		switch (type) {
		case 1:
			return SortboardType.PlayerFightPower;
		case 2:
			return SortboardType.PlayerLvl;
		case 3:
		case 4:
			return SortboardType.PlayerMerit;
		case 5:
			return SortboardType.PlayerWingLvl;
		case 6:
			return SortboardType.MountLvl;
		case 7:
			return SortboardType.TalismanLvl;

		default:
			return null;
		}
	}

	public static RankBeginEndTimeRef getRankBeginEndTimeRef(String refId) {
		return (RankBeginEndTimeRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
	}

	public static Map<String, Byte> getLimitTimeDataMaps() {
		return limitTimeDataMaps;
	}

	public static void setLimitTimeDataMaps(Map<String, Byte> limitTimeDataMaps) {
		LimitTimeActivityMgr.limitTimeDataMaps = limitTimeDataMaps;
	}

	public static Map<Byte, String> getBeginEndTimeMaps() {
		return beginEndTimeMaps;
	}

	public static void setBeginEndTimeMaps(Map<Byte, String> beginEndTimeMaps) {
		LimitTimeActivityMgr.beginEndTimeMaps = beginEndTimeMaps;
	}

	public static Map<Integer, SortboardData> getCopySBD() {
		return copySBD;
	}

	public static Map<Byte, Integer> getVersion() {
		return version;
	}

	public static List<LimitRankType> getLimitRankTypes() {
		return limitRankTypes;
	}

}
