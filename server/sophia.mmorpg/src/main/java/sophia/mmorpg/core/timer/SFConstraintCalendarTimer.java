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
 * 有特地约束概念的日历类整点报时
 */
final class SFConstraintCalendarTimer extends SFInnerTimer {
	private static final Logger logger = Logger.getLogger(SFConstraintCalendarTimer.class.getName());
	
	private final SFTimeUnit constraintTimeUnit;
	
	private final long constraintTimeValue;
	
	private final SFTimeUnit timeUnit;
	
	private final long timeValue;
	
	boolean triggerFlag = false;
	
	SFConstraintCalendarTimer(SFTimeChimeService timerChimeService, SFTimeChimeListener timeChimeListener, SFTimeUnit timeUnit,
			long timeValue, SFTimeUnit constraintTimeUnit, long constraintTimeValue) {
		super(timerChimeService, timeChimeListener);
		
		this.timeUnit = timeUnit;
		this.timeValue = timeValue;
		
		this.constraintTimeUnit = constraintTimeUnit;
		this.constraintTimeValue = constraintTimeValue;
	}
	
	@Override
	void timeTick(long crtTime, Calendar crtCalendar) {
		int crtConstraintTimeValue = 0;
		switch(constraintTimeUnit) {
		case MONTH:
			crtConstraintTimeValue = crtCalendar.get(Calendar.MONTH) + 1;
			break;
		case WEEK:
			crtConstraintTimeValue = crtCalendar.get(Calendar.WEDNESDAY);
			break;
		case DAY:
			crtConstraintTimeValue = crtCalendar.get(Calendar.DAY_OF_MONTH);
			break;
		case HOUR:
			crtConstraintTimeValue = crtCalendar.get(Calendar.HOUR_OF_DAY);
			break;
		case HALFHOUR:
			break;
		case MINUTE:
			crtConstraintTimeValue = crtCalendar.get(Calendar.MINUTE);
			break;
		case SECOND:
			break;
		}
		
		int crtTimeValue = 0;
		switch(timeUnit) {
		case MONTH:
			crtTimeValue = crtCalendar.get(Calendar.MONTH) + 1;
			doTimeTick(crtTimeValue, crtConstraintTimeValue);
			break;
		case WEEK:
			crtTimeValue = crtCalendar.get(Calendar.WEDNESDAY);
			doTimeTick(crtTimeValue, crtConstraintTimeValue);
			break;
		case DAY:
			break;
		case HOUR:
			crtTimeValue = crtCalendar.get(Calendar.HOUR_OF_DAY);
			doTimeTick(crtTimeValue, crtConstraintTimeValue);
			break;
		case HALFHOUR:
			break;
		case MINUTE:
			crtTimeValue = crtCalendar.get(Calendar.MINUTE);
			doTimeTick(crtTimeValue, crtConstraintTimeValue);
			break;
		case SECOND:
			break;
		}
	}
	
	private void doTimeTick(int crtTimeValue, int crtConstraintTimeValue) {
		if (!triggerFlag && constraintTimeValue == crtConstraintTimeValue && timeValue == crtTimeValue) {
			triggerFlag = true;
			
			try {
				timeChimeListener.handleTimeChime();
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug(toString() + " TimeTick..." + "crtTimeValue=" + crtTimeValue + "crtConstraintTimeValue=" + crtConstraintTimeValue);
			}
		}
		
		if (triggerFlag && timeValue != crtTimeValue) {
			triggerFlag = false;
		}
	}

	@Override
	public String type() {
		return SFConstraintCalendarTimer.class.getSimpleName();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SFConstraintCalendarTimer [constraintTimeUnit=");
		builder.append(constraintTimeUnit);
		builder.append(", constraintTimeValue=");
		builder.append(constraintTimeValue);
		builder.append(", timeUnit=");
		builder.append(timeUnit);
		builder.append(", timeValue=");
		builder.append(timeValue);
		builder.append(", getId()=");
		builder.append(getId());
		builder.append("]");
		return builder.toString();
	}
}
