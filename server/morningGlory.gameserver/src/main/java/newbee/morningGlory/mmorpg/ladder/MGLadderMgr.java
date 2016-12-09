package newbee.morningGlory.mmorpg.ladder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.ladder.timer.CacheMemberListener;
import newbee.morningGlory.mmorpg.ladder.timer.ChallengeCountChimeListener;
import newbee.morningGlory.mmorpg.ladder.timer.RewardReceivedMillsChimeListener;
import newbee.morningGlory.mmorpg.ladder.timer.SystemMessageListener;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;

public class MGLadderMgr {
	private static MGLadderMgr instance = new MGLadderMgr();
	
	private List<SFTimer> timers = new ArrayList<SFTimer>();
	
	private static final long oneDayMillis = 24 * 3600 * 1000L;
	
	private static final int Default_GetReward_DayInterval = 3;
	
	private long receiveRewardMillis;

	private MGLadderMgr(){
	}

	public static MGLadderMgr getInstance() {
		return instance;
	}
	
	
	public void initialize() {
		initializeReceiveRewardTime();
		
		initializeTimers();
	}
	
	private void initializeReceiveRewardTime() {
		long openServerTime = MorningGloryContext.getServerOpenTime();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(openServerTime);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		receiveRewardMillis = calendar.getTimeInMillis() + oneDayMillis;
		
		calculateReceiveRewardMillis();
	}
	
	public void calculateReceiveRewardMillis() {
		while (System.currentTimeMillis() >= receiveRewardMillis) {
			receiveRewardMillis += oneDayMillis * Default_GetReward_DayInterval;
		}
	}
	
	public long getReceiveRewardMillis() {
		return receiveRewardMillis;
	}

	private void initializeTimers() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		
		// 每小时发送一次系统消息
		SFTimer msgTimer = timerCreater.hourCalendarChime(new SystemMessageListener());
		
		// 修改玩家领奖状态，通知天梯前三名玩家系统消息
		SFTimer rewardStateTimer = timerCreater.minuteCalendarChime(new RewardReceivedMillsChimeListener());
		
		// 每日重置玩家挑战次数
		SFTimer challengeTimer = timerCreater.calendarChime(new ChallengeCountChimeListener(), SFTimeUnit.HOUR, 0);
		
		SFTimer chcheTimer = timerCreater.hourCalendarChime(new CacheMemberListener());
		
		timers.add(msgTimer);
		timers.add(rewardStateTimer);
		timers.add(challengeTimer);
		timers.add(chcheTimer);
	}

	public void destoryTimer() {
		for (SFTimer timer : timers) {
			if (null != timer) {
				timer.cancel();
			}
		}
	}
}
