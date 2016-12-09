package newbee.morningGlory.http.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public final class TimeStampUtil {

	public static final int TinyOffset = 15 * 1000;
	public static final int MiddleOffset = 60 * 1000;
	public static final int BigOffset = 15 * 60 * 1000;

	/**
	 * 检查两个时间戳(毫秒)在offset的差值范围内是否想等
	 * 
	 * @param timeStamp1
	 * @param timeStamp2
	 * @param offset
	 * @return
	 */
	public static boolean check(long timeStamp1, long timeStamp2, int offset) {
		long mirror = Math.abs(timeStamp1 - timeStamp2);
		if (mirror > offset)
			return false;
		else
			return true;
	}

	/**
	 * 检查指定时间戳(毫秒)与当前时间在offset的差值范围内是否想等
	 * 
	 * @param timeStamp1
	 * @param timeStamp2
	 * @param offset
	 * @return
	 */
	public static boolean check(long timeStamp, int offset) {
		return check(timeStamp, System.currentTimeMillis(), offset);
	}

	public static boolean isToday(Date date) throws Exception {
		Date parseDate = getDate(date);
		if (parseDate == null) {
			return false;
		}
		return (parseDate.getTime() == getDate(new Date()).getTime());
	}

	public static final String DATE_STRING_REGEX = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";

	/**
	 * 格式化指定时间
	 * 
	 * @param pattern
	 *            时间显示格式（date=yyyy-MM-dd，time=HH:mm:ss，datetime=yyyy-MM-dd
	 *            HH:mm:ss
	 * @param d
	 * @return
	 */
	public static String getDate(String pattern, Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		if (pattern.toLowerCase().equals("date"))
			sdf.applyPattern("yyyy-MM-dd");
		else if (pattern.toLowerCase().equals("time"))
			sdf.applyPattern("HH:mm:ss");
		else if (pattern.toLowerCase().equals("datetime"))
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		else
			sdf.applyPattern(pattern);
		return sdf.format(d);
	}

	public static Date getDate(Date d) throws Exception {
		return new SimpleDateFormat("yyyy-MM-dd").parse(getDate("date", d));
	}

	public static String getDate(String pattern, long ms) {
		return getDate(pattern, new Date(ms));
	}

	public static String getDate(String pattern) {
		return getDate(pattern, new Date());
	}

	public static String getDate() {
		return getDate("datetime");
	}

	/**
	 * 把秒数转换为字符串形式
	 * 
	 * @param t
	 *            单位：秒(s)
	 * @return
	 */
	public static String getTimeString(int t) {
		long sec = t % 60;
		long min = (t = t / 60) % 60;
		long hour = (t = t / 60) % 24;
		long day = t / 24;
		return ((day > 0L) ? day + "天" : "") + (((hour > 0L) || (day > 0L)) ? hour + "小时" : "") + (((hour > 0L) || (day > 0L) || (min > 0L)) ? min + "分" : "") + sec + "秒";
	}

	public static Date addHour(Date date, int hours) {
		return new Date(date.getTime() + hours * 1000 * 60 * 60L);
	}

	public static Date addHour(int hours) {
		return addHour(new Date(), hours);
	}

	/**
	 * 在指定的时间上加上days天
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDay(Date date, int days) {
		return new Date((date.getTime() + 86400000L * days));
	}

	/**
	 * 在当前的时间上加上days天
	 * 
	 * @param days
	 * @return
	 */
	public static Date addDay(int days) {
		return addDay(new Date(), days);
	}

	/**
	 * 在指定时间上加上months个月
	 * 
	 * @param date
	 * @param months
	 * @return
	 */
	public static Date addMonth(Date date, int months) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(java.util.Calendar.MONTH, months);
		return gc.getTime();
	}

	public static Date addMonth(int months) {
		return addMonth(new Date(), months);
	}

	public static Date addYear(Date date, int years) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(java.util.Calendar.YEAR, years);
		return gc.getTime();
	}

	public static Date addYear(int years) {
		return addYear(new Date(), years);
	}

	public static long hourDiff(long d1, long d2) {
		return (d1 - d2) / (1000 * 60 * 60);
	}

	public static long hourDiff(Date d1, Date d2) {
		return hourDiff(d1.getTime(), d2.getTime());
	}

	public static long hourDiff(String d1, String d2) {
		String p = "yyyy-MM-dd HH:mm:ss";
		return hourDiff(parseDateTime(d1, p), parseDateTime(d1, p));
	}

	public static long minuteDiff(long d1, long d2) {
		return (d1 - d2) / (1000 * 60);
	}

	public static long minuteDiff(Date d1, Date d2) {
		return minuteDiff(d1.getTime(), d2.getTime());
	}

	public static long minuteDiff(String d1, String d2) {
		return minuteDiff(parseDateTime(d1), parseDateTime(d2));
	}

	public static long secondDiff(long d1, long d2) {
		return (d1 - d2) / 1000L;
	}

	public static long secondDiff(Date d1, Date d2) {
		return secondDiff(d1.getTime(), d2.getTime());
	}

	public static long secondDiff(String d1, String d2) {
		return secondDiff(parseDateTime(d1), parseDateTime(d2));
	}

	/**
	 * 返回两个时间相差的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int dayDiff(String date1, String date2) {
		Date d1 = parseDateTime(date1, "yyyy-MM-dd");
		Date d2 = parseDateTime(date2, "yyyy-MM-dd");
		if (d1 != null && d2 != null)
			return dayDiff(d1, d2);
		return 0;
	}

	/**
	 * 返回两个时间相差的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int dayDiff(Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return 0;
		return dayDiff(date1.getTime(), date2.getTime());
	}

	/**
	 * 返回两个时间相差的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int dayDiff(long date1, long date2) {
		return (int) ((date1 - date2) / 86400000L);
	}

	/**
	 * 返回两个时间相差的月份数
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int monthDiff(Date d1, Date d2) {
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		c1.setTime(d1);
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		c1.setTime(d2);
		int i = c1.get(java.util.Calendar.YEAR);
		int j = c2.get(java.util.Calendar.YEAR);
		int m1 = c1.get(java.util.Calendar.MONTH);
		int m2 = c2.get(java.util.Calendar.MONTH);
		if (i == j)
			return (m1 - m2);
		return (m1 - m2 + (i - j) * 12);
	}

	public static int monthDiff(long d1, long d2) {
		return monthDiff(new Date(d1), new Date(d2));
	}

	public static boolean isSameMinute(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
				&& c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY) && c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE);
	}

	public static boolean isSameHour(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
				&& c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY);
	}

	public static boolean isSameDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}
	
	public static boolean isSameMinute(long t1,long t2){
		return isSameMinute(new Date(t1), new Date(t2));
	}

	public static boolean isSameHour(long t1,long t2){
		return isSameHour(new Date(t1), new Date(t2));
	}
	
	public static boolean isSameDay(long t1,long t2){
		return isSameDay(new Date(t1), new Date(t2));
	}
	
	public static boolean isSameMonth(long t1,long t2){
		return isSameDay(new Date(t1), new Date(t2));
	}
	/**
	 * 是否是同一个月
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isSameMonth(Date d1, Date d2) {
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		c1.setTime(d1);
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		c2.setTime(d2);
		return (c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR)) && (c1.get(java.util.Calendar.MONTH) == c2.get(java.util.Calendar.MONTH));
	}

	/**
	 * 是否是同一年
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isSameYear(Date d1, Date d2) {
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		c1.setTime(d1);
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		c2.setTime(d2);
		return c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR);
	}

	/**
	 * 将字符串日期时间转换成Date对象
	 * 
	 * @param s
	 * @param pattern
	 * @param dv
	 * @return
	 */
	public static Date parseDateTime(String s, String pattern, Date dv) {
		try {
			SimpleDateFormat df = new SimpleDateFormat();
			if ((pattern != null) && (pattern.length() > 0))
				df.applyPattern(pattern);
			return df.parse(s);
		} catch (Exception e) {
		}
		return dv;
	}

	public static Date parseDateTime(String s, String pattern) {
		return parseDateTime(s, pattern, null);
	}

	public static Date parseDateTime(String s, Date dv) {
		return parseDateTime(s, null, dv);
	}

	public static Date parseDateTime(String s) {
		return parseDateTime(s, null, null);
	}

	/**
	 * 获得当前时间所有各项值
	 * 
	 * @return
	 */
	public static int[] getDateArray() {
		return getDateArray(new Date());
	}

	/**
	 * 获得指定时间所有各项值 年 月 日 DAY_OF_WEEK 時 分 秒
	 * 
	 * @param date
	 * @return
	 */
	public static int[] getDateArray(Date date) {
		int[] v = new int[7];
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		v[0] = c.get(java.util.Calendar.YEAR);
		v[1] = c.get(java.util.Calendar.MONTH) + 1;
		v[2] = c.get(java.util.Calendar.DAY_OF_MONTH);
		v[3] = c.get(java.util.Calendar.DAY_OF_WEEK);
		v[3] = v[3] - 1;
		v[4] = c.get(java.util.Calendar.HOUR_OF_DAY);
		v[5] = c.get(java.util.Calendar.MINUTE);
		v[6] = c.get(java.util.Calendar.SECOND);
		return v;
	}

	/**
	 * 判断指定时间是否是当月的第一天
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isBOM(Date d) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(d);
		return c.get(java.util.Calendar.DAY_OF_MONTH) == 1;
	}

	/**
	 * 判断当前是否是当月的第一天
	 * 
	 * @return
	 */
	public static boolean isBOM() {
		return isBOM(new Date());
	}

	/**
	 * 判断指定时间是否是当月的最后一天
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isEOM(Date d) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(d);
		int m = c.get(java.util.Calendar.MONTH);
		c.setTime(addDay(d, 1));
		return c.get(java.util.Calendar.MONTH) != m;
	}

	/**
	 * 判断当前时间是否是当月的最后一天
	 * 
	 * @return
	 */
	public static boolean isEOM() {
		return isEOM(new Date());
	}

	/**
	 * 格式化日期
	 */
	public static String formatDate(Date date) throws Exception {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		Date now = new Date();
		String[] messages = new String[] { "一分钟前 ", "两分钟前 ", "三分钟前 ", "四分钟前 ", "五分钟前 ", "六分钟前 ", "七分钟前 ", "八分钟前 ", "九分钟前 ", "十分钟前 " };
		long interval = now.getTime() - date.getTime();
		if (interval < 1L)
			interval = 1L;
		if ((interval + 59999) / 60000 - 1 <= 9)
			return messages[(int) ((interval + 59999) / 60000 - 1)];
		else if (getDate(now).getTime() - getDate(date).getTime() == 0L)
			return "今天 " + sdf2.format(date) + " ";
		else if (getDate(now).getTime() - getDate(date).getTime() < 48 * 3600000L)
			return "昨天 " + sdf2.format(date) + " ";
		else if (getDate(now).getTime() - getDate(date).getTime() < 72 * 3600000L)
			return "前天 " + sdf2.format(date) + " ";
		else {
			java.util.Calendar cDate = java.util.Calendar.getInstance();
			cDate.setTime(date);
			java.util.Calendar cNow = java.util.Calendar.getInstance();
			cNow.setTime(now);
			if (cDate.get(java.util.Calendar.YEAR) == cNow.get(java.util.Calendar.YEAR))
				return sdf1.format(date) + " ";
			return sdf.format(date) + " ";
		}
	}

	// ---------------------------------------
	/**
	 * TimeZone.getTimeZone("GMT+8") GMT+8是指偏移GMT+0（即基准线）八个小时
	 * 也可以精确到分，比如GMT+0856,表示偏移GMT+0八小时56分 TimeZone.getTimeZone暂不支持到秒的表示
	 */
	public static Map<String, String> citiesMap = new HashMap<String, String>();

	static String[][] cities = { { "埃尼维托克岛", "GMT-12" }, { "澳大利亚诺福克岛", "GMT-1130" }, { "阿皮亚", "GMT-11" }, { "阿拉斯加", "GMT-9" }, { "洛杉矶", "GMT-8" }, { "加利福尼亚", "GMT-8" },
			{ "盐湖城", "GMT-7" }, { "墨西哥城", "GMT-6" }, { "渥太华", "GMT-5" }, { "华盛顿", "GMT-5" }, { "加拉加斯", "GMT-4" }, { "圣地亚哥", "GMT-4" }, { "纽芬兰", "GMT-0330" }, { "巴西利亚", "GMT-3" },
			{ "布宜诺斯艾利斯", "GMT-3" }, { "普拉亚", "GMT-2" }, { "亚速尔群岛", "GMT-1" }, { "伦敦", "GMT-0" }, { "哥本哈根", "GMT+1" }, { "柏林", "GMT+1" }, { "巴黎", "GMT+1" }, { "雅典", "GMT+2" },
			{ "莫斯科", "GMT+3" }, { "巴格达", "GMT+3" }, { "德黑兰", "GMT+0330" }, { "维多利亚", "GMT+4" }, { "喀布尔", "GMT+0430" }, { "伊斯兰堡", "GMT+5" }, { "新德里", "GMT+0530" },
			{ "加德满都", "GMT+0540" }, { "达卡", "GMT+6" }, { "内比都", "GMT+0630" }, { "曼谷", "GMT+7" }, { "河内", "GMT+7" }, { "雅加达", "GMT+7" }, { "北京", "GMT+8" }, { "新加坡", "GMT+8" },
			{ "吉隆坡", "GMT+8" }, { "东京", "GMT+9" }, { "汉城", "GMT+9" }, { "阿得莱德", "GMT+0930" }, { "堪培拉", "GMT+10" }, { "所罗门群岛", "GMT+11" }, { "惠灵顿", "GMT+12" } };

	static {
		for (int i = 0; i < cities.length; i++)
			citiesMap.put(cities[i][0], cities[i][1]);
	}

	/**
	 * 根据城市名称获取时间
	 */
	public static String getCityTime(String city) throws Exception {
		Date date = getDateByTimeZone(TimeZone.getTimeZone((String) citiesMap.get(city)));
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
		return format.format(date);
	}

	/**
	 * 根据城市名称获取时间
	 */
	public static Date getCityDate(String city) throws Exception {
		return getDateByTimeZone(TimeZone.getTimeZone((String) citiesMap.get(city)));
	}

	/**
	 * 根据城市名称获取该城市所属时区，如果没有则返回服务器默认时区
	 */
	public static TimeZone getTimeZoneByCity(String city) {
		TimeZone timeZone = TimeZone.getTimeZone((String) citiesMap.get(city));
		return timeZone == null ? TimeZone.getDefault() : timeZone;
	}

	/**
	 * 根据时区获取该时区当前的时间Date
	 */
	public static Date getDateByTimeZone(TimeZone timezone) throws Exception {
		Date date = new Date();
		TimeZone defaultTimeZone = TimeZone.getDefault();
		long offset = timezone.getOffset(date.getTime()) - defaultTimeZone.getOffset(date.getTime());
		return new Date(date.getTime() + offset);
	}
	
	public static boolean compareTime(String date1,String date2){
	       
		DateFormat df = new SimpleDateFormat("HH:mm");
       try {
           Date dt1 = df.parse(date1);
           Date dt2 = df.parse(date2);
           if (dt1.getTime() >= dt2.getTime()) {
               return true;
           } else if (dt1.getTime() < dt2.getTime()) {
               return false;
           } 
           
       } catch (Exception exception) {
           exception.printStackTrace();
       }

		return false;
	}
	
	public static boolean compareDate(String date1,String date2){
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	       try {
	           Date dt1 = df.parse(date1);
	           Date dt2 = df.parse(date2);
	           if (dt1.compareTo(dt2)>=0) {
	               return true;
	           } 
	           
	       } catch (Exception exception) {
	           exception.printStackTrace();
	       }
	       
		return false;
	}

}
