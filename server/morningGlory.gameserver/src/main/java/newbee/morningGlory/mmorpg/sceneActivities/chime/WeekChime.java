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

/**
 * 每周某时
 */
public class WeekChime extends DayChime {
	
	// 周几
	private byte dayInWeek;
	
	public WeekChime() {}
	
	public WeekChime(byte dayInWeek, Calendar startCalendar, Calendar endCalendar) {
		this.dayInWeek = dayInWeek;
		super.setStartCalendar(startCalendar);
		super.setEndCalendar(endCalendar);
	}
	
	public byte getDayInWeek() {
		return dayInWeek;
	}

	public void setDayInWeek(byte dayInWeek) {
		this.dayInWeek = dayInWeek;
	}
	
	@Override
	public boolean isTheSameDay(final Calendar crtCalendar) {
		int crtDayInWeek = crtCalendar.get(Calendar.DAY_OF_WEEK);
		if (crtDayInWeek != dayInWeek) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean checkBegin(final Calendar crtCalendar) {
		int crtDayInWeek = crtCalendar.get(Calendar.DAY_OF_WEEK);
		if (crtDayInWeek != dayInWeek) {
			return false;
		}
		
		if (!super.checkBegin(crtCalendar)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean checkEnd(final Calendar crtCalendar) {
		int crtDayInWeek = crtCalendar.get(Calendar.DAY_OF_WEEK);
		if (crtDayInWeek != dayInWeek) {
			return true;
		}
		
		if (super.checkEnd(crtCalendar)) {
			return true;
		}
		
		return false;
	}
}
