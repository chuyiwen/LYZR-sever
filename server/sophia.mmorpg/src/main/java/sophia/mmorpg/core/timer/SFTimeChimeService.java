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
package sophia.mmorpg.core.timer;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.core.FoundationContext;
import sophia.foundation.task.PeriodicTaskHandle;
import sophia.foundation.task.Task;

import com.google.common.util.concurrent.AbstractIdleService;

public final class SFTimeChimeService extends AbstractIdleService {

	private long startTime;

	private long stopTime;

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private final SFTimerCreater timerCreater = new SFTimerCreaterImpl(this);

	private final CopyOnWriteArrayList<SFInnerTimer> secondTimerList = new CopyOnWriteArrayList<>();

	private final CopyOnWriteArrayList<SFInnerTimer> commonTimerList = new CopyOnWriteArrayList<>();

	private PeriodicTaskHandle commTickTaskHandler;

	private ScheduledFuture<?> secondsTickTaskHandler;

	public SFTimeChimeService() {

	}

	public SFTimerCreater getTimerCreater() {
		return timerCreater;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getStopTime() {
		return stopTime;
	}

	@Override
	protected void startUp() throws Exception {
		startTime = System.currentTimeMillis();
		commTickTaskHandler = FoundationContext.getTaskManager().schedulePeriodicTask(new CommonTimeTickTask(), 0, 1000 * 15);
		secondsTickTaskHandler = executor.scheduleAtFixedRate(new SecondTimeTickTask(), 0, 1, TimeUnit.SECONDS);
	}

	@Override
	protected void shutDown() throws Exception {
		stopTime = System.currentTimeMillis();

		for (SFInnerTimer timer : secondTimerList) {
			timer.getTimeChimeListener().handleServiceShutdown();
		}

		for (SFInnerTimer timer : commonTimerList) {
			timer.getTimeChimeListener().handleServiceShutdown();
		}

		if (commTickTaskHandler != null) {
			commTickTaskHandler.cancel();
		}

		if (secondsTickTaskHandler != null) {
			secondsTickTaskHandler.cancel(false);
		}
	}

	public void cancel(SFInnerTimer theTimer) {
		if (StringUtils.equals(SFSecondTimer.class.getSimpleName(), theTimer.type())) {
			for (SFInnerTimer timer : secondTimerList) {
				if (theTimer.equals(timer)) {
					secondTimerList.remove(theTimer);
					theTimer.getTimeChimeListener().handleTimeChimeCancel();
					break;
				}
			}
		} else {
			for (SFInnerTimer timer : commonTimerList) {
				if (theTimer.equals(timer)) {
					commonTimerList.remove(theTimer);
					theTimer.getTimeChimeListener().handleTimeChimeCancel();
					break;
				}
			}
		}
	}

	private final class CommonTimeTickTask implements Task {
		private final Calendar crtCalendar = new GregorianCalendar();

		@Override
		public void run() throws Exception {
//			if (logger.isDebugEnabled()) {
//				logger.debug("CommonTimeTickTask running...");
//			}
//			
			long currentTimeMillis = System.currentTimeMillis();
			crtCalendar.setTimeInMillis(currentTimeMillis);

			for (SFInnerTimer timer : commonTimerList) {
				timer.timeTick(currentTimeMillis, crtCalendar);
			}
		}
	}

	private final class SecondTimeTickTask implements Runnable {
		private final Calendar crtCalendar = new GregorianCalendar();

		@Override
		public void run() {
			long currentTimeMillis = System.currentTimeMillis();
			crtCalendar.setTimeInMillis(currentTimeMillis);

			for (SFInnerTimer timer : secondTimerList) {
				timer.timeTick(currentTimeMillis, crtCalendar);
			}
		}
	}

	private final class SFTimerCreaterImpl implements SFTimerCreater {
		private final SFTimeChimeService timeChimeService;

		SFTimerCreaterImpl(SFTimeChimeService timeChimeService) {
			this.timeChimeService = timeChimeService;
		}

		@Override
		public SFTimer hourCalendarChime(SFTimeChimeListener timeChimeListener) {
			return calendarChime(timeChimeListener, SFTimeUnit.MINUTE, 0);
		}

		@Override
		public SFTimer halfHourCalendarChime(SFTimeChimeListener timeChimeListener) {
			return calendarChime(timeChimeListener, SFTimeUnit.MINUTE, 30);
		}

		@Override
		public SFTimer minuteCalendarChime(SFTimeChimeListener timeChimeListener) {
			return calendarChime(timeChimeListener, SFTimeUnit.MINUTE, -1);
		}

		@Override
		public SFTimer calendarChime(SFTimeChimeListener timeChimeListener, SFTimeUnit timeUnit, int timeValue) {
			SFCalendarTimer ret = new SFCalendarTimer(timeChimeService, timeChimeListener, timeUnit, timeValue);
			commonTimerList.add(ret);
			return ret;
		}

		@Override
		public SFTimer monthCalendarChime(SFTimeChimeListener timeChimeListener, int dayOfMonth, int hour) {
			SFTimeUnit chimeTimeUnit = SFTimeUnit.HOUR;
			SFTimeUnit constrainTimeUnit = SFTimeUnit.DAY;
			return constrainClaendarChime(timeChimeListener, chimeTimeUnit, hour, constrainTimeUnit, dayOfMonth);
		}

		@Override
		public SFTimer weekCalendarChime(SFTimeChimeListener timeChimeListener, int dayOfWeek, int hour) {
			SFTimeUnit chimeTimeUnit = SFTimeUnit.HOUR;
			SFTimeUnit constrainTimeUnit = SFTimeUnit.WEEK;
			return constrainClaendarChime(timeChimeListener, chimeTimeUnit, hour, constrainTimeUnit, dayOfWeek);
		}

		@Override
		public SFTimer constrainClaendarChime(SFTimeChimeListener timeChimeListener, SFTimeUnit chimeTimeUnit, int chimeTimeValue, SFTimeUnit constrainTimeUnit,
				int constrainTimeValue) {
			SFConstraintCalendarTimer ret = new SFConstraintCalendarTimer(timeChimeService, timeChimeListener, chimeTimeUnit, chimeTimeValue, constrainTimeUnit, constrainTimeValue);
			commonTimerList.add(ret);
			return ret;
		}

		@Override
		public SFTimer delayPeriodClaendarChime(SFTimeChimeListener timeChimeListener, SFTimeUnit chimeTimeUnit, int chimeTimeValue, long delayPeriod) {
			SFPeriodTimer ret = new SFPeriodTimer(timeChimeService, timeChimeListener, chimeTimeUnit, chimeTimeValue, delayPeriod);
			commonTimerList.add(ret);
			return ret;
		}

		@Override
		public SFTimer secondInterval(SFTimeChimeListener timeChimeListener) {
			SFSecondTimer ret = new SFSecondTimer(timeChimeService, timeChimeListener);
			secondTimerList.add(ret);
			return ret;
		}
	}
}
