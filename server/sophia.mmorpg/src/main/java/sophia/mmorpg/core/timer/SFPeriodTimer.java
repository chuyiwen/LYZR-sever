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
 * 周期时间报时
 */
final class SFPeriodTimer extends SFInnerTimer {
	private static final Logger logger = Logger.getLogger(SFPeriodTimer.class.getName());
	
	private final SFTimeUnit chimeTimeUnit;
	
	private final int chimeTimeValue;
	
	private boolean triggerFlag = false;
	
	private final long delayPeriod;
	
	private long lastChimeTime = -1;
	
	SFPeriodTimer(SFTimeChimeService timerChimeService, SFTimeChimeListener timeChimeListener, SFTimeUnit chimeTimeUnit,
			int chimeTimeValue, long delayPeriod) {
		super(timerChimeService, timeChimeListener);
		
		if (delayPeriod <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.chimeTimeUnit = chimeTimeUnit;
		this.chimeTimeValue = chimeTimeValue;
		this.delayPeriod = delayPeriod;
	}
	
	@Override
	void timeTick(long crtTime, Calendar crtCalendar) {
		int crtTimeValue = 0;
		switch (chimeTimeUnit) {
		case MONTH:
			crtTimeValue = crtCalendar.get(Calendar.MONTH) + 1;
			doCommonTimeTick(crtTimeValue, crtTime);
			break;
		case WEEK:
			crtTimeValue = crtCalendar.get(Calendar.DAY_OF_WEEK);
			doCommonTimeTick(crtTimeValue, crtTime);
			break;
		case DAY:
			crtTimeValue = crtCalendar.get(Calendar.DAY_OF_MONTH);
			doCommonTimeTick(crtTimeValue, crtTime);
			break;
		case HOUR:
			crtTimeValue = crtCalendar.get(Calendar.HOUR_OF_DAY);
			doCommonTimeTick(crtTimeValue, crtTime);
			break;
		case HALFHOUR:
			crtTimeValue = crtCalendar.get(Calendar.MINUTE);
			doHalfHourTimeTick(crtTimeValue, crtTime);
			break;
		case MINUTE:
			crtTimeValue = crtCalendar.get(Calendar.MINUTE);
			doCommonTimeTick(crtTimeValue, crtTime);
			break;
		case SECOND:
			break;
		}
	}

	private void doHalfHourTimeTick(int crtTimeValue, long crtTime) {
		if (!triggerFlag && crtTimeValue % 30 == 0 && (lastChimeTime == -1 || lastChimeTime + delayPeriod >= crtTime)) {
			triggerFlag = true;
			
			try {
				timeChimeListener.handleTimeChime();
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
			
			lastChimeTime = crtTime;
			
			if (logger.isDebugEnabled()) {
				logger.debug(toString() + " timeTick..." + "crtTimeValue=" + crtTimeValue + " ,crtTime=" + crtTime);
			}
		} 
		
		if (chimeTimeValue != crtTimeValue) {
			triggerFlag = false;
		}
	}

	private void doCommonTimeTick(int crtTimeValue, long crtTime) {
		if (!triggerFlag && chimeTimeValue == crtTimeValue && (lastChimeTime == -1 || lastChimeTime + delayPeriod >= crtTime)) {
			triggerFlag = true;
			
			try {
				timeChimeListener.handleTimeChime();
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
				
			lastChimeTime = crtTime;
			
			if (logger.isDebugEnabled()) {
				logger.debug(toString() + " timeTick..." + "crtTimeValue=" + crtTimeValue + " ,crtTime=" + crtTime);
			}
		} 
		
		if (chimeTimeValue != crtTimeValue) {
			triggerFlag = false;
		}
	}

	@Override
	public String type() {
		return SFPeriodTimer.class.getSimpleName();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SFPeriodTimer [chimeTimeUnit=");
		builder.append(chimeTimeUnit);
		builder.append(", chimeTimeValue=");
		builder.append(chimeTimeValue);
		builder.append(", delayPeriod=");
		builder.append(delayPeriod);
		builder.append(", lastChimeTime=");
		builder.append(lastChimeTime);
		builder.append(", getId()=");
		builder.append(getId());
		builder.append("]");
		return builder.toString();
	}
}
