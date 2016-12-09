package newbee.morningGlory.mmorpg.player.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import sophia.mmorpg.utils.DateTimeUtil;

public class Utils {

	/**
	 * 计算两个时间相差多少天
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int daysBetween(long time1, long time2) {
		long between_days = (DateTimeUtil.getLongTimeOfToday(time1) - DateTimeUtil.getLongTimeOfToday(time2))/(1000 * 3600 * 24l);
		return Math.abs(Integer.parseInt(String.valueOf(between_days)));
	}
	

	public static String formatTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(time);
		return sdf.format(date);
	}

}
