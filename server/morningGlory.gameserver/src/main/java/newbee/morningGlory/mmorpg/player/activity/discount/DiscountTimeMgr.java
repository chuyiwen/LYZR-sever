package newbee.morningGlory.mmorpg.player.activity.discount;

import java.util.Calendar;

import sophia.mmorpg.utils.DateTimeUtil;

public class DiscountTimeMgr {
	// discount refresh time interval
	public static final byte Refresh_Interval = 8;
	
	public static long getRemainTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		long remainTime = 0;
		long refreshIntervalMillis = getRefreshIntervalMillis();
		remainTime = cal.getTimeInMillis() - System.currentTimeMillis();
		do {
			remainTime = remainTime + refreshIntervalMillis;
		} while (remainTime < 0);
		
		return remainTime;
	}
	
	public static long getRefreshIntervalMillis() {
		return Refresh_Interval * 3600 * 1000L;
	}
	
	public static boolean isRefreshTimeOver() {
		Calendar calendar = Calendar.getInstance();
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		return hour == 0 || hour == 8 || hour == 16;
		
	}
	
	public static boolean isSamePeriod(long millis1, long millis2) {
		if (!DateTimeUtil.isTheSameDay(millis1, millis2)) {
			return false;
		}
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(millis1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(millis2);
		
		int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
		int hour2 = calendar2.get(Calendar.HOUR_OF_DAY);
		
		boolean result = false;
		if (hour1 >= 0 && hour1 < 8) {
			result = hour2 >= 0 && hour2 < 8;
		}
		
		if (hour1 >= 8 && hour1 < 16) {
			result = hour2 >= 8 && hour2 < 16;
		}
		
		if (hour1 >= 16 && hour1 < 24) {
			result = hour2 >= 16 && hour2 < 24;
		}
		
		return result;
	}
}
