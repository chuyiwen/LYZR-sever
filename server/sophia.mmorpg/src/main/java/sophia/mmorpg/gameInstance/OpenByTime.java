package sophia.mmorpg.gameInstance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.common.base.Strings;

public class OpenByTime {
	public static final int OPEN = 0;

	public static final int CLOSE = -1;

	private byte conditionType;// 1为格式1,未来定点时间开启,2类型为格式2,3类型为格式3
	private List<OpenTimeData> openConditions;// 所有时间对象是按时间先后顺序排列的

	// 格式1: YYYY-MM-DD hh:mm:ss|持续时间(单位秒), 指在将来定时开启,多个时间使用&连接,如: 2013-01-20
	// 18:00:00|3600&2013-01-28 18:00:00|3600
	// 格式2: hh:mm:ss|持续时间(单位秒),指在每天定时开启,多个时间使用&连接,如:
	// 12:00:00|3600&1|18:00:00|3600
	// 格式3: 周几|hh:mm:ss|持续时间(单位秒),指在每周几当天的指定时间开启, 多个时间设定用&连接,如:
	// 1|12:00:00|3600&5|18:00:00|3600 (周1: 1, 周天:7)

	/**
	 * 
	 * @param now
	 *            (单位：毫秒)
	 * @return null==没有开放的副本
	 */
	public OpenTimeData getOpenTime(long now) {

		for (OpenTimeData data : openConditions) {
			long timestamp = getTimestamp(data);
			if (now >= timestamp && now < timestamp + data.getLastTime() * 1000) {
				// 此时副本正在开启,返回0
				return data;
			} else if (timestamp >= now) {
				// 此时副本还未开启,返回未来开启时间
				return null;
			}
		}
		// 表示此副本没有时间限制
		return null;
	}

	public long getStartTime() {
		long startTime = 0l;
		for (OpenTimeData data : openConditions) {
			long timestamp = getTimestamp(data);
			if (timestamp < startTime) {
				startTime = timestamp;
			}
			if (startTime == 0) {
				startTime = timestamp;
			}
		}
		return startTime;
	}

	public long getEndTime() {
		OpenTimeData openTime = null;
		long startTime = 0l;
		for (OpenTimeData data : openConditions) {
			long timestamp = getTimestamp(data);
			if (timestamp < startTime) {
				startTime = timestamp;
				openTime = data;
			}
			if (startTime == 0) {
				startTime = timestamp;
				openTime = data;
			}
		}

		return startTime + openTime.getLastTime() * 1000;
	}

	public long getTimestamp(OpenTimeData data) {
		long timestamp = 0;
		String startTime = data.getStartTime();
		if (conditionType == 1) {
			timestamp = Timestamp.valueOf(startTime).getTime();
		} else if (conditionType == 2) {
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH) + 1;
			int ds = cal.get(Calendar.DAY_OF_MONTH);
			String date = new StringBuffer().append(year).append("-").append(m < 10 ? (new StringBuffer("0").append(m).toString()) : m).append("-")
					.append(ds < 10 ? (new StringBuffer("0").append(ds).toString()) : ds).append(" ").append(startTime).toString();
			timestamp = Timestamp.valueOf(date).getTime();
		} else {
			// 格式3: 周几|hh:mm:ss|持续时间(单位秒),指在每周几当天的指定时间开启, 多个时间设定用&连接
			Calendar cal = Calendar.getInstance();
			int year = 0;
			int m = 0;
			int ds = 0;

			int dayInWeek = (cal.get(Calendar.DAY_OF_WEEK) == 1 ? cal.get(Calendar.DAY_OF_WEEK) + 7 : cal.get(Calendar.DAY_OF_WEEK)) - data.getDayInWeek() - 1;// 差几天
			if (dayInWeek == 0) {
				// 表示是当天
				year = cal.get(Calendar.YEAR);
				m = cal.get(Calendar.MONTH) + 1;
				ds = cal.get(Calendar.DAY_OF_MONTH);
			} else {
				cal.add(Calendar.DAY_OF_MONTH, -dayInWeek);
				year = cal.get(Calendar.YEAR);
				m = cal.get(Calendar.MONTH) + 1;
				ds = cal.get(Calendar.DAY_OF_MONTH);
			}
			String date = new StringBuffer().append(year).append("-").append(m < 10 ? (new StringBuffer("0").append(m).toString()) : m).append("-")
					.append(ds < 10 ? (new StringBuffer("0").append(ds).toString()) : ds).append(" ").append(startTime).toString();
			timestamp = Timestamp.valueOf(date).getTime();
		}
		return timestamp;
	}

	/**
	 * 解析时间格式数据,设置副本开启时间
	 */
	public void setOpenDetails(String openConditions) {
		this.openConditions = new ArrayList<OpenTimeData>();
		if (Strings.isNullOrEmpty(openConditions.trim())) {
			return;
		}
		int x = openConditions.indexOf("|");
		if (x <= 1) {
			// 每周固定格式
			this.conditionType = 3;

			String[] datas = openConditions.split("&");
			OpenTimeData open = null;
			for (String s : datas) {
				// 1|12:00:00|3600
				open = new OpenTimeData();
				String[] des = s.split("\\|");
				open.setDayInWeek(Byte.parseByte(des[0]));
				open.setStartTime(des[1]);
				open.setLastTime(Integer.parseInt(des[2]));
				this.openConditions.add(open);
			}
		} else {
			if (openConditions.contains("-")) {
				// 格式1 2013-01-20 18:00:00|3600&2013-01-28 18:00:00|3600
				this.conditionType = 1;
			} else {
				this.conditionType = 2;
			}
			String[] datas = openConditions.split("&");
			OpenTimeData open = null;
			for (String s : datas) {
				open = new OpenTimeData();
				open.setStartTime(s.split("\\|")[0]);
				open.setLastTime(Integer.parseInt(s.split("\\|")[1]));
				this.openConditions.add(open);
			}
		}
	}

	public List<OpenTimeData> getOpenConditions() {
		return openConditions;
	}

	public byte getConditionType() {
		return conditionType;
	}

	// /**
	// * 判断是否能开启副本,时间限制开启的副本
	// */
	// @Override
	// public boolean isCanOpen(List<PlayerCharacter> t, InstanceRef ref) {
	// long time = getOpenTime();
	// if (time == 0)
	// return true;
	//
	// return false;
	// }
	//

}
