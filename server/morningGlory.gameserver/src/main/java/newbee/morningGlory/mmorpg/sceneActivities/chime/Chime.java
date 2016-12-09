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

public interface Chime {
	
	boolean isTheSameDay(final Calendar crtCalendar);

	boolean checkBegin(final Calendar crtCalendar);
	
	boolean checkEnd(final Calendar crtCalendar);
	
	boolean checkPreStart(final int preStartSecond);
	
	boolean checkPreEnd(final int preEndSecond);
	
	/** 剩余的结束时间，单位s*/
    long getRemainEndTime();
    
    /** 剩余的开始时间，单位s*/
    long getRemainStartTime();
    
    long getStartTimeStamp();
    
    long getEndTimeStamp();
}
