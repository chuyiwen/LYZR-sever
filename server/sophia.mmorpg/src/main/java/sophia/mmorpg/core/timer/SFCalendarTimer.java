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

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;

/**
 * 日历类整点报时Timer
 */
final class SFCalendarTimer extends SFInnerTimer {
	private static final Logger logger = Logger.getLogger(SFCalendarTimer.class.getName());
	
	private final SFTimeUnit timeUnit;
	
	private final long timeValue;
	
	private boolean chimeFlag = false;
	
	private long lastTimeValue = -1;
	
	SFCalendarTimer(SFTimeChimeService timerChimeService, SFTimeChimeListener timeChimeListener, SFTimeUnit timeUnit,
			long timeValue) {
		super(timerChimeService, timeChimeListener);
		
		this.timeUnit = timeUnit;
		this.timeValue = timeValue;
	}
	
	@Override
	void timeTick(long crtTime, Calendar crtCalendar) {
		int crtTimeValue = 0;
		switch (timeUnit) {
		case MONTH:
			crtTimeValue = crtCalendar.get(Calendar.MONTH) + 1;
			doFixedTimeTick(crtTimeValue, crtTime);
			break;
		case WEEK:
			crtTimeValue = crtCalendar.get(Calendar.DAY_OF_WEEK);
			doFixedTimeTick(crtTimeValue, crtTime);
			break;
		case DAY:
			crtTimeValue = crtCalendar.get(Calendar.DAY_OF_MONTH);
			break;
		case HOUR:
			crtTimeValue = crtCalendar.get(Calendar.HOUR_OF_DAY);
			doFixedTimeTick(crtTimeValue, crtTime);
			break;
		case HALFHOUR:
			crtTimeValue = crtCalendar.get(Calendar.MINUTE);
			doHalfHourTimeTick(crtTimeValue, crtTime);
			break;
		case MINUTE:
			crtTimeValue = crtCalendar.get(Calendar.MINUTE);
			if (timeValue == -1) {
				doPerTimeTick(crtTimeValue, crtTime);
			} else {
				doFixedTimeTick(crtTimeValue, crtTime);
			}
			break;
		case SECOND:
			break;
		}
	}

	private void doHalfHourTimeTick(int crtTimeValue, long crtTime) {
		if (!chimeFlag && crtTimeValue % 30 == 0) {
			chimeFlag = true;
			
			try {
				timeChimeListener.handleTimeChime();
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug(getId() + "HalfHourTimeTick " + "crtTimeValue=" + crtTimeValue + "crtTime=" + crtTime);
			}
		} 
		
		if (chimeFlag && crtTimeValue % 30 != 0) {
			chimeFlag = false;
		}
	}

	private void doFixedTimeTick(int crtTimeValue, long crtTime) {
		if (!chimeFlag && timeValue == crtTimeValue) {
			chimeFlag = true;
			
			try {
				timeChimeListener.handleTimeChime();
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug(getId() + "HalfHourTimeTick " + "crtTimeValue=" + crtTimeValue + "crtTime=" + crtTime);
			}
		} 
		
		if (chimeFlag && timeValue != crtTimeValue) {
			chimeFlag = false;
		}
	}
	
	private void doPerTimeTick(int crtTimeValue, long crtTime) {
		if (!chimeFlag && lastTimeValue != crtTimeValue) {
			chimeFlag = true;
			
			try {
				timeChimeListener.handleTimeChime();
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
			
			lastTimeValue = crtTimeValue;
			
			if (logger.isDebugEnabled()) {
				logger.debug(toString() + " timeTick..." + "crtTimeValue=" + crtTimeValue + "crtTime=" + crtTime);
			}
		} 
		
		if (chimeFlag && lastTimeValue == crtTimeValue) {
			chimeFlag = false;
		}
	}

	@Override
	public String type() {
		return SFCalendarTimer.class.getSimpleName();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SFCalendarTimer [");
		builder.append("getId()=");
		builder.append(getId());
		builder.append(", lastTimeValue=");
		builder.append(lastTimeValue);
		builder.append(", timeUnit=");
		builder.append(timeUnit);
		builder.append(", timeValue=");
		builder.append(timeValue);
		builder.append("]");
		return builder.toString();
	}
}
