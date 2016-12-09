/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.mmorpg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateTimeUtil {

	/**
	 * 取得当天开始时的毫秒数
	 * 
	 * @return
	 */
	public static long getLongTimeOfToday() {
		Calendar zeroTimeMillis = getZeroTimeMillis(System.currentTimeMillis());
		return zeroTimeMillis.getTime().getTime();
	}

	/**
	 * 取得指定时间某天开始时的毫秒数
	 * 
	 * @return
	 */
	public static long getLongTimeOfToday(long time) {
		Calendar cal = getZeroTimeMillis(time);
		return cal.getTime().getTime();
	}

	public static String getDateString(long crtTime) {
		Calendar cal = getZeroTimeMillis(crtTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		return format.format(cal.getTime());
	}
	
	public static String getDateDetailString(long crtTime){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(crtTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
		return format.format(cal.getTime());
	}

	public static boolean isTheSameDay(final long timeStamp1, final long timeStamp2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(timeStamp1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(timeStamp2);
		if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)) {
			return false;
		}

		if (calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)) {
			return false;
		}

		if (calendar1.get(Calendar.DAY_OF_MONTH) != calendar2.get(Calendar.DAY_OF_MONTH)) {
			return false;
		}

		return true;
	}

	public static long getNowTimeMill() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date d = new Date();
		try {
			return (sdf.parse(sdf.format(d))).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getTimeStringOfToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}

	public static String getStringOfToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(new Date());
	}

	public static String getStringOfTow() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Date date = new Date();
		long time = date.getTime() + 24 * 3600 * 1000l;
		date.setTime(time);
		return sdf.format(date);
	}
	
	public static int getIntervalDays(long time1, long time2) {
		Calendar calendar1 = getZeroTimeMillis(time1);
		Calendar calendar2 = getZeroTimeMillis(time2);
		long timeInMillis1 = calendar1.getTimeInMillis();
		long timeInMillis2 = calendar2.getTimeInMillis();
		int intervalDays = Math.abs((int) ((timeInMillis1 - timeInMillis2) / (24 * 60 * 60 * 1000)));
		return intervalDays;
	}
	
	public static Calendar getZeroTimeMillis(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	
	public static long getTimeMillis(int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, 0);
		
		long timeInMillis = cal.getTimeInMillis();
		
		return timeInMillis;
	}

}
