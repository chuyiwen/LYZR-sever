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
import java.util.Date;

/**
 * 开始日期-结束日期
 */
public class DateChime implements Chime {

	private long startTimeStamp;
	
	private long endTimeStamp;
	
	public DateChime() {}
	
	public DateChime(long startTime, long endTime) {
		this.startTimeStamp = startTime;
		this.endTimeStamp = endTime;
	}

	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}

	public long getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(long endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
	}
	
	@Override
	public boolean isTheSameDay(final Calendar crtCalendar) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTimeInMillis(startTimeStamp);
		if (startCalendar.get(Calendar.YEAR) != crtCalendar.get(Calendar.YEAR)) {
			return false;
		}
		
		if (startCalendar.get(Calendar.MONTH) != crtCalendar.get(Calendar.MONTH)) {
			return false;
		}
		
		if (startCalendar.get(Calendar.DAY_OF_MONTH) != crtCalendar.get(Calendar.DAY_OF_MONTH)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean checkBegin(final Calendar crtCalendar) {
		Date crtTimeStamp = crtCalendar.getTime();
		if (crtTimeStamp.getTime() >= startTimeStamp) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean checkEnd(final Calendar crtCalendar) {
		Date crtTimeStamp = crtCalendar.getTime();
		if (crtTimeStamp.getTime() >= endTimeStamp) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean checkPreStart(int preStartSecond) {
		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.add(Calendar.SECOND, preStartSecond);
		if (checkBegin(crtCalendar)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean checkPreEnd(int preEndSecond) {
		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.add(Calendar.SECOND, preEndSecond);
		if (checkEnd(crtCalendar)) {
			return true;
		}
		
		return false;
	}

	@Override
	public long getRemainEndTime() {
		return (this.endTimeStamp - System.currentTimeMillis()) / 1000;
	}

	@Override
	public long getRemainStartTime() {
		return (this.startTimeStamp - System.currentTimeMillis()) / 1000;
	}
	
}
