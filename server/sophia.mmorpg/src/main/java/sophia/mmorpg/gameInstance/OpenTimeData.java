package sophia.mmorpg.gameInstance;

public class OpenTimeData {
	private int lastTime;// 副本持续的时间,单位为秒
	private String startTime;// 副本开启的时间
	private byte dayInWeek;// 周几

	// 格式1: YYYY-MM-DD hh:mm:ss|持续时间(单位秒), 指在将来定时开启,多个时间使用&连接,如: 2013-01-20
	// 18:00:00|3600&2013-01-28 18:00:00|3600
	// 格式2: hh:mm:ss|持续时间(单位秒),指在每天定时开启,多个时间使用&连接,如: 12:00:00|3600&18:00:00|3600
	// 格式3: 周几|hh:mm:ss|持续时间(单位秒),指在每周几当天的指定时间开启, 多个时间设定用&连接,如:
	// 1|12:00:00|3600&5|18:00:00|3600 (周1: 1, 周天:7)

	public int getLastTime() {
		return lastTime;
	}

	public byte getDayInWeek() {
		return dayInWeek;
	}

	public void setDayInWeek(byte dayInWeek) {
		this.dayInWeek = dayInWeek;
	}

	public void setLastTime(int lastTime) {
		this.lastTime = lastTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
}
