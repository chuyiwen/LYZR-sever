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
package newbee.morningGlory.mmorpg.sceneActivities.chime;

import java.util.Calendar;

import sophia.mmorpg.utils.DateTimeUtil;

/**
 * 每天某时
 */
public class DayChime implements Chime {
	
	// 开始时间: HH:mm:ss
	private Calendar startCalendar;
	
	// 结束时间: HH:mm:ss
	private Calendar endCalendar;
	
	public DayChime() {}
	
	public DayChime(Calendar startCalendar, Calendar endCalendar) {
		this.startCalendar = startCalendar;
		this.endCalendar = endCalendar;
	}

	public Calendar getStartCalendar() {
		return startCalendar;
	}

	public void setStartCalendar(Calendar startCalendar) {
		this.startCalendar = startCalendar;
	}

	public Calendar getEndCalendar() {
		return endCalendar;
	}

	public void setEndCalendar(Calendar endCalendar) {
		this.endCalendar = endCalendar;
	}
	
	@Override
	public boolean checkBegin(final Calendar crtCalendar) {
		int crtHour = crtCalendar.get(Calendar.HOUR_OF_DAY);
		if (startCalendar.get(Calendar.HOUR_OF_DAY) < crtHour) {
			return true;
		}
		
		int crtMinute = crtCalendar.get(Calendar.MINUTE);
		if (startCalendar.get(Calendar.HOUR_OF_DAY) == crtHour && startCalendar.get(Calendar.MINUTE) < crtMinute) {
			return true;
		}
		
		int crtSecond = crtCalendar.get(Calendar.SECOND);
		if (startCalendar.get(Calendar.HOUR_OF_DAY) == crtHour && startCalendar.get(Calendar.MINUTE) == crtMinute && startCalendar.get(Calendar.SECOND) <= crtSecond) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean checkEnd(final Calendar crtCalendar) {
		int crtHour = crtCalendar.get(Calendar.HOUR_OF_DAY);
		if (endCalendar.get(Calendar.HOUR_OF_DAY) < crtHour) {
			return true;
		} 
		
		int crtMinute = crtCalendar.get(Calendar.MINUTE);
		if (endCalendar.get(Calendar.HOUR_OF_DAY) == crtHour && endCalendar.get(Calendar.MINUTE) < crtMinute) {
			return true;
		}
		
		int crtSecond = crtCalendar.get(Calendar.SECOND);
		if (endCalendar.get(Calendar.HOUR_OF_DAY) == crtHour && endCalendar.get(Calendar.MINUTE) == crtMinute && endCalendar.get(Calendar.SECOND) <= crtSecond) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isTheSameDay(final Calendar crtCalendar) {
		return true;
	}

	@Override
	public boolean checkPreStart(final int preStartSecond) {
		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.add(Calendar.SECOND, preStartSecond);
		if (checkBegin(crtCalendar)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean checkPreEnd(final int preEndSecond) {
		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.add(Calendar.SECOND, preEndSecond);
		if (checkEnd(crtCalendar)) {
			return true;
		}
		
		return false;
	}

	@Override
	public long getRemainEndTime() {
		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.set(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));
		return (endCalendar.getTimeInMillis() - crtCalendar.getTimeInMillis()) / 1000;
	}
	
	@Override
	public long getRemainStartTime() {
		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.set(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH));
		return (startCalendar.getTimeInMillis() - crtCalendar.getTimeInMillis()) / 1000;
	}

	@Override
	public long getStartTimeStamp() {
		long timeMillis = DateTimeUtil.getTimeMillis(startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), startCalendar.get(Calendar.SECOND));
		return timeMillis;
	}

	@Override
	public long getEndTimeStamp() {
		long timeMillis = DateTimeUtil.getTimeMillis(endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), endCalendar.get(Calendar.SECOND));
		return timeMillis;
	}
}
