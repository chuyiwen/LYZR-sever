package sophia.mmorpg.gameInstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 副本次数记录对象
 * 
 */

public final class TimeRecord {
	private String refId;// 副本的引用ID
	private List<Long> timesInDay;// 记录天完成的时间
	private List<Long> timesInWeek;// 一周内完成的时间

	public TimeRecord(String refId) {
		this.refId = refId;
	}

	public String getString() {
		StringBuffer values = new StringBuffer();

		values.append(refId).append("|");
		if (timesInDay != null && timesInDay.size() > 0) {

			for (long l : timesInDay) {
				values.append(l).append("&");
			}
		} else {
			values.append("N");
		}

		values.append("|");
		if (timesInWeek != null && timesInWeek.size() > 0) {
			for (long l : timesInWeek) {
				values.append(l).append("&");
			}
		} else {
			values.append("M");
		}

		return values.toString();
	}

	public void setString(String value) {
		String values[] = value.split("\\|");
		refId = values[0];
		if (!values[1].equals("N")) {
			timesInDay = new ArrayList<Long>();
			String times[] = values[1].split("&");
			for (String s : times) {
				if (s != null && s.length() > 0)
					timesInDay.add(Long.parseLong(s));
			}
		}

		if (!values[2].equals("M")) {
			timesInWeek = new ArrayList<Long>();
			String times[] = values[2].split("&");
			for (String s : times) {
				if (s != null && s.length() > 0)
					timesInWeek.add(Long.parseLong(s));
			}
		}
	}

	/**
	 * 判断此对象是否可以被系统回收
	 * 
	 * @return
	 */
	public boolean isCanRemove() {
		clearTimesInDay();
		clearTimesInThisWeek();
		if ((timesInDay == null || timesInDay.size() == 0) && (timesInWeek == null || timesInWeek.size() == 0)) {
			return true;
		}
		return false;
	}

	/**
	 * 返回本周的次数记录列表
	 * 
	 * @return
	 */
	public List<Long> getTimesInWeek() {
		if (timesInWeek == null || timesInWeek.size() == 0)
			return null;
		long time = 0;
		List<Long> records = new ArrayList<Long>();
		int i = timesInWeek.size() - 1;
		for (; i >= 0; i--) {
			time = timesInWeek.get(i);
			if (time >= getLongTimeOfThisWeek()) {
				// 大于当天初始值
				records.add(time);
			} else {
				// 小于当天的初始时间则跳出循环
				break;
			}
		}
		if (i > 0) {
			// 说明有昨天的记录在List中,进行清理
			clearTimesInThisWeek();
		}
		return records;
	}

	/**
	 * 返回本周的次数
	 * 
	 * @return
	 */
	public int getTimesInWeek(boolean is) {
		if (timesInWeek == null || timesInWeek.size() == 0)
			return 0;
		long time = 0;
		int count = 0;
		int i = timesInWeek.size() - 1;
		for (; i >= 0; i--) {
			time = timesInWeek.get(i);
			if (time >= getLongTimeOfThisWeek()) {
				// 大于当天初始值
				count++;
			} else {
				// 小于当天的初始时间则跳出循环
				break;
			}
		}
		if (i > 0) {
			// 说明有昨天的记录在List中,进行清理
			clearTimesInThisWeek();
		}
		return count;
	}

	/**
	 * 返回当天的次数记录列表
	 * 
	 * @return
	 */
	public List<Long> getTimesInDay() {
		if (timesInDay == null || timesInDay.size() == 0)
			return null;
		long time = 0;
		List<Long> records = new ArrayList<Long>();
		int i = timesInDay.size() - 1;
		for (; i >= 0; i--) {
			time = timesInDay.get(i);
			if (time >= getLongTimeOfToday()) {
				// 大于当天初始值
				records.add(time);
			} else {
				// 小于当天的初始时间则跳出循环
				break;
			}
		}
		if (i > 0) {
			// 说明有昨天的记录在List中,进行清理
			clearTimesInDay();
		}
		return records;
	}

	/**
	 * 返回当天的次数
	 * 
	 * @return
	 */
	public int getTimesInDay(boolean is) {
		if (timesInDay == null || timesInDay.size() == 0)
			return 0;
		long time = 0;
		int count = 0;
		int i = timesInDay.size() - 1;
		long today = getLongTimeOfToday();
		for (; i >= 0; i--) {
			time = timesInDay.get(i);
			if (time >= today) {
				// 大于当天初始值
				count++;
			} else {
				// 小于当天的初始时间则跳出循环
				break;
			}
		}
		if (i > 0) {
			// 说明有昨天的记录在List中,进行清理
			clearTimesInDay();
		}
		return count;
	}

	public void clearTimesInThisWeek() {
		if (timesInWeek != null) {
			for (int i = 0; i < timesInWeek.size(); i++) {
				if (timesInWeek.get(i) < getLongTimeOfThisWeek()) {
					timesInWeek.remove(i);
					i--;
				}
			}
		}
	}

	public void clearTimesInDay() {
		if (timesInDay != null) {
			for (int i = 0; i < timesInDay.size(); i++) {
				if (timesInDay.get(i) < getLongTimeOfToday()) {
					timesInDay.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * 返回本周一初始的long值
	 * 
	 * @return
	 */
	public long getLongTimeOfThisWeek() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		int day = cal.get(Calendar.DAY_OF_WEEK) - 2;
		if (day > 0) {
			cal.add(Calendar.DAY_OF_YEAR, -day);
		} else if (day < 0) {
			cal.add(Calendar.DAY_OF_YEAR, -6);
		}
		return cal.getTime().getTime();
	}

	/**
	 * 返回当天初始的long值
	 * 
	 * @return
	 */
	public long getLongTimeOfToday() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime().getTime();
	}

	public String getRefId() {
		return refId;
	}
}
