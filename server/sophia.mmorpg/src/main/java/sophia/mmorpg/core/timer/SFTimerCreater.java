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

public interface SFTimerCreater {
	/**
	 * 整点报时
	 * 
	 * @param timeChimeListener
	 * @return
	 */
	SFTimer hourCalendarChime(SFTimeChimeListener timeChimeListener);

	/**
	 * 30分钟报时
	 * 
	 * @param timeChimeListener
	 * @return
	 */
	SFTimer halfHourCalendarChime(SFTimeChimeListener timeChimeListener);

	/**
	 * 分钟报时
	 * 
	 * @param timeChimeListener
	 * @return
	 */
	SFTimer minuteCalendarChime(SFTimeChimeListener timeChimeListener);

	/**
	 * 在指定的时间单位的指定时间值报时
	 * 
	 * @param timeChimeListener
	 * @param timeUnit
	 *            指定的时间单位。该方法不接受{@link SFTimeUnit#SECOND}单位。这是因为性能的原因。
	 * @param timeValue
	 *            指定的时间值。范围取决，时间单位。
	 * @return
	 */
	SFTimer calendarChime(SFTimeChimeListener timeChimeListener, SFTimeUnit timeUnit, int timeValue);

	// ------------------------------------------------------------------------------------------------

	/**
	 * 在指定的某天（月）某时报时
	 * 
	 * @param timeChimeListener
	 * @param dayOfMonth
	 *            [1..31]
	 * @param hour
	 *            [0..23]
	 * @return
	 */
	SFTimer monthCalendarChime(SFTimeChimeListener timeChimeListener, int dayOfMonth, int hour);

	/**
	 * 在指定的周？（周）某时报时
	 * 
	 * @param timeChimeListener
	 * @param dayOfWeek
	 *            [周1..周7]
	 * @param hour
	 *            [0..23]
	 * @return
	 */
	SFTimer weekCalendarChime(SFTimeChimeListener timeChimeListener, int dayOfWeek, int hour);

	/**
	 * 在指定的约束时间单位的时间值，在指定的时间单位的时间值报时
	 * 
	 * @param timeChimeListener
	 * @param chimeTimeUnit
	 *            报时时间单位
	 * @param chimeTimeValue
	 *            报时时间值。 范围取决，时间单位。
	 * @param constrainTimeUnit
	 *            约束时间单位
	 * @param constrainTimeValue
	 *            约束时间值。范围取决，时间单位。
	 * @return
	 */
	SFTimer constrainClaendarChime(SFTimeChimeListener timeChimeListener, SFTimeUnit chimeTimeUnit, int chimeTimeValue, SFTimeUnit constraintTimeUnit, int constraintTimeValue);

	// ------------------------------------------------------------------------------------------------
	/**
	 * 在指定的时间单位的时间值报时。每次执行后，延迟指定的时间后，再继续报时。
	 * 
	 * @param timeChimeListener
	 * @param chimeTimeUnit
	 *            报时时间单位。
	 * @param chimeTimeValue
	 *            报时时间值。范围取决，时间单位。
	 * @param delayPeriod
	 *            延迟的时间
	 * @return
	 */
	SFTimer delayPeriodClaendarChime(SFTimeChimeListener timeChimeListener, SFTimeUnit chimeTimeUnit, int chimeTimeValue, long delayPeriod);

	// ------------------------------------------------------------------------------------------------

	/**
	 * 秒报时。我们单独把它拿出来，特殊处理。
	 * 
	 * @param timeChimeListener
	 * @return
	 */
	SFTimer secondInterval(SFTimeChimeListener timeChimeListener);
}
