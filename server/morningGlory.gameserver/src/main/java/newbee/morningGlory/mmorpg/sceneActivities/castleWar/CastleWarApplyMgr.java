package newbee.morningGlory.mmorpg.sceneActivities.castleWar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityType;
import newbee.morningGlory.mmorpg.sceneActivities.chime.Chime;
import newbee.morningGlory.mmorpg.union.MGUnion;
import newbee.morningGlory.mmorpg.union.MGUnionMgr;
import newbee.morningGlory.mmorpg.union.MGUnionSaver;
import newbee.morningGlory.ref.loader.activity.AbstractSceneActivityRefLoader;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.task.Task;
import sophia.game.GameContext;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;
import sophia.mmorpg.utils.RuntimeResult;

public class CastleWarApplyMgr {
	private boolean canApply = false;

	private static CastleWarApplyMgr instance = new CastleWarApplyMgr();

	private SFTimer delayPeriodClaendarChime;

	private MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();

	private List<String> canWarUnion = new ArrayList<String>();

	public static long TwoDayTime = 2 * 24 * 60 * 60 * 1000;

	public static CastleWarApplyMgr getInstance() {
		return instance;
	}

	public int getDayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public int[] getTime() {
		Calendar calendar = Calendar.getInstance();
		int time[] = { calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND) };
		return time;
	}

	public int[] stringToInt(String[] time) {
		int hour = Integer.parseInt(time[0]);
		int min = Integer.parseInt(time[1]);
		int second = Integer.parseInt(time[2]);
		int newtime[] = { hour, min, second };
		return newtime;
	}

	public void setApplyTimer(String[] temp, final boolean turn) {
		int[] nowTime = getTime();
		int[] time = stringToInt(temp);
		if (!isBigger(nowTime, time)) {
			long waiteTime = (time[0] - nowTime[0]) * 60 * 60 + (time[1] - nowTime[1]) * 60 + (time[2] - nowTime[2]);
			if (waiteTime > 0) {
				switchApply(!turn);
				GameContext.getTaskManager().scheduleTask(new Task() {
					@Override
					public void run() throws Exception {
						switchApply(turn);
					}
				}, waiteTime * 1000);
			}
		} else {
			switchApply(turn);
		}
	}

	public void switchApply(boolean temp) {
		if (temp) {
			applyStart();
			MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
			String kingCityUnionName = unionMgr.getKingCityUnionName();
			if (!StringUtils.isEmpty(kingCityUnionName)) {
				signupWar(kingCityUnionName);
			}
		} else {
			applyEnd();
		}
	}

	/**
	 * a是不是比b大
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean isBigger(int[] a, int[] b) {
		boolean ret = true;
		if (a[0] > b[0]) {
			return ret;
		} else if (a[0] == b[0] && a[1] > b[1]) {
			return ret;
		} else if (a[0] == b[0] && a[1] == b[1] && a[2] > b[2]) {
			return ret;
		} else {
			ret = false;
		}
		return ret;
	}

	public void initCastleWarTimer() {
		dailyChecker();
		checkToResetCastleWarStartTime();
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		delayPeriodClaendarChime = timerCreater.calendarChime(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				dailyChecker();
				checkToResetCastleWarStartTime();
			}

			@Override
			public void handleServiceShutdown() {
			}
		}, SFTimeUnit.HOUR, 0);

	}

	public void destoryCastleWarTimer() {
		if (delayPeriodClaendarChime != null) {
			delayPeriodClaendarChime.cancel();
		}
	}

	private void dailyChecker() {
		CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.CastleWarMgr);
		String startApplyTime = castleWarMgr.getCastleWarRef().getStartApplyTime();
		String endApplyTime = castleWarMgr.getCastleWarRef().getEndApplyTime();
		String[] startTime = startApplyTime.split("\\|");
		String[] endTime = endApplyTime.split("\\|");

		int startDay = Integer.parseInt(startTime[0]);
		int endDay = Integer.parseInt(endTime[0]);
		if (getDayOfWeek() == startDay) {
			String[] timeToStart = startTime[1].split(":");
			setApplyTimer(timeToStart, true);
		} else if (getDayOfWeek() == endDay) {
			String[] timeToEnd = endTime[1].split(":");
			setApplyTimer(timeToEnd, false);
		} else {
			switchApply(isStartedDay(startDay, endDay));
		}
	}

	private void checkToResetCastleWarStartTime() {
		CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.CastleWarMgr);
		long serverOpenTime = MorningGloryContext.getServerOpenTime();
		long nowTime = System.currentTimeMillis();

		int firstIntervalDays = castleWarMgr.getCastleWarRef().getFirstIntervalDays();
		int rangeIntervalDays = castleWarMgr.getCastleWarRef().getRangeIntervalDays();
		int intervalDays = DateTimeUtil.getIntervalDays(nowTime, serverOpenTime);
		if (nowTime - serverOpenTime > 0 && intervalDays <= firstIntervalDays) {
			resetCastleWarStartTime(castleWarMgr, firstIntervalDays);
		} else if (nowTime - serverOpenTime > 0 && intervalDays > firstIntervalDays) {
			int nowIntervalDays = firstIntervalDays + (int) Math.ceil((double) (intervalDays - firstIntervalDays) / rangeIntervalDays) * rangeIntervalDays;
			resetCastleWarStartTime(castleWarMgr, nowIntervalDays);
		}
	}

	private void resetCastleWarStartTime(CastleWarMgr castleWarMgr, int intervalDays) {
		String openAndEndTime = castleWarMgr.getCastleWarRef().getOpenAndEndTime();
		String[] time = openAndEndTime.split("\\|");
		int[] startTime =  stringToInt(time[0].split(":"));
		int[] endTime = stringToInt(time[1].split(":"));
		
		long serverOpenTime = MorningGloryContext.getServerOpenTime();
		String timeToStart = getTimeString(serverOpenTime, intervalDays, startTime[0], startTime[1]);
		String timeToEnd = getTimeString(serverOpenTime, intervalDays, endTime[0], endTime[1]);
		String[] str = new String[] { timeToStart, timeToEnd };
		Chime readDateChime = AbstractSceneActivityRefLoader.readDateChime(str);
		if (castleWarMgr.getChimeList().size() <= 0) {
			castleWarMgr.getChimeList().clear();
			castleWarMgr.getChimeList().add(readDateChime);
		} else {
			castleWarMgr.getChimeList().set(0, readDateChime);
		}
	}

	/**
	 * 获取特定时间点 指定天数之后 某小时的时间戳
	 * 
	 * @param millis
	 * @param dayInterval
	 * @param hour
	 * @param minte
	 */
	private String getTimeString(long millis, int dayInterval, int hour, int minte) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		calendar.add(Calendar.DAY_OF_YEAR, dayInterval);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		long timeInMillis = calendar.getTimeInMillis();
		long addMillis = hour * 3600 * 1000L + minte * 60 * 1000L;

		timeInMillis += addMillis;

		calendar.setTimeInMillis(timeInMillis);

		return getTimeString(calendar.getTimeInMillis());
	}

	private String getTimeString(long mills) {
		// format yyyy-MM-dd HH:mm:ss
		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(mills));
	}

	private boolean isStartedDay(int startDay, int endDay) {
		for (int i = 0; i < 7; i++) {
			int day = getDayOfWeek() + i;
			day = day > 7 ? (day - 7) : day;
			if (day == startDay) {
				return false;
			} else if (day == endDay) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean isAlreadySignupWar(String unionName) {
		return canWarUnion.contains(unionName);
	}

	/**
	 * 申请加入攻城战
	 */
	public synchronized RuntimeResult signupWar(String unionName) {
		RuntimeResult canSightUp = canSightUp(unionName);
		if (canSightUp.isError()) {
			return canSightUp;
		}
		canWarUnion.add(unionName);
		MGUnion tmpUnion = unionMgr.getUnion(unionName);
		tmpUnion.setSignup(true);
		MGUnionSaver.getInstance().saveImmediateData(tmpUnion);
		return RuntimeResult.OK();
	}

	private synchronized RuntimeResult canSightUp(String unionName) {
		CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S012");
		PropertyDictionary pd = castleWarMgr.getCastleWarRef().getProperty();
		int canWarUnionNumber = MGPropertyAccesser.getMaxStackNumber(pd);

		if (castleWarMgr.isCastleWarStart()) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_CASTLEWAR_STARTED_CANT_APPLY);
		} else if (!canApply) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_CASTLEWAR_NOT_START);
		} else if (canWarUnionNumber < canWarUnion.size()) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_CASTLEWAR_LIST_ENOUGHT);
		} else if (isAlreadySignupWar(unionName)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_CASTLEWAR_APPLY_TWICE);
		}
		return RuntimeResult.OK();
	}

	public synchronized boolean exitWar(String unionName) {
		if (!canWarUnion.contains(unionName)) {
			return false;
		}
		canWarUnion.remove(unionName);
		MGUnion tmpUnion = unionMgr.getUnion(unionName);
		tmpUnion.setSignup(false);
		MGUnionSaver.getInstance().saveImmediateData(tmpUnion);
		return true;
	}

	public synchronized List<String> getSightUpUnionList() {
		List<String> castleWarList = new ArrayList<>();
		for (String unionName : canWarUnion) {
			castleWarList.add(unionName);
		}
		return castleWarList;
	}

	public synchronized void applyStart() {
		canApply = true;
	}

	public synchronized void applyEnd() {
		canApply = false;
	}

	public synchronized void clearSignupWarUnions() {
		for (String unionName : canWarUnion) {
			MGUnion union = unionMgr.getUnion(unionName);
			union.setSignup(false);
			MGUnionSaver.getInstance().saveImmediateData(union);
		}
		this.canWarUnion.clear();
	}

	public synchronized void setCanWarUnion(List<String> canWarUnion) {
		this.canWarUnion = canWarUnion;
	}

}
