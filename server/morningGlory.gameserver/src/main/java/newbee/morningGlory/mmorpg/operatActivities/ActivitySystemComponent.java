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
package newbee.morningGlory.mmorpg.operatActivities;

import java.util.Calendar;

import org.apache.log4j.Logger;

import sophia.foundation.task.PeriodicTaskHandle;
import sophia.foundation.task.Task;
import sophia.foundation.task.TaskManager;
import sophia.game.GameContext;
import sophia.game.component.AbstractComponent;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service.State;

public class ActivitySystemComponent extends AbstractComponent {
	
	private static final Logger logger = Logger.getLogger(ActivitySystemComponent.class);
	private ScheduleTask scheduleTask = new ScheduleTask();
	private TickService tickService = new TickService();
	@Override
	public void ready() {
		State state = tickService.startAndWait();
		if (state == State.RUNNING) {
			if (logger.isDebugEnabled()) {
				logger.debug(" ActivitySystemTick was running.");
			}
		} else {
			logger.error(" ActivitySystemTick start failed.");
		}

	}
	
	@Override
	public void suspend() {
		State state = tickService.stopAndWait();

		if (state == State.TERMINATED) {
			if (logger.isDebugEnabled()) {
				logger.debug(" ActivitySystemTick was terminated.");
			} 
		} else {
			logger.error(" ActivitySystemTick stop failed.");
		}
		super.suspend();
	}

	@Override
	public void destroy() {
		State state = tickService.stopAndWait();

		if (state == State.TERMINATED) {
			if (logger.isDebugEnabled()) {
				logger.debug(" ActivitySystemTick was terminated.");
			}
		} else {
			logger.error(" ActivitySystemTick stop failed.");
		}
		super.destroy();
	}
	
	public void startActivityTimer(){
		
	}
	
	/**秒定时 
	 * 
	 */
	private void onSecondTimer() {

	}
	/**分钟定时
	 * 
	 */
	private void onMinuteTimer() {

	}
	/**
	 * 小时定时
	 */
	private void onHourTimer() {

	}
	/**日定时 */
	private void onDayTimer() {

	}
	/**
	 *  进入新的小时
	 * @param hour
	 */
	private void onEnterHour(int hour) {

	}
	/**
	 * 进入新的半小时
	 * @param hour
	 */
	private void onEnterHalfHour(int hour) {

	}
	/**
	 * 进入新的一天
	 * @param dayOfMonth
	 */
	private void onEnterDay(int dayOfMonth) {
		OperatActivityMgr.getInstance().onEnterDay();

	}
	/**
	 * 进入新的一周
	 */
	public void onEnterWeek() {
		
	}
	/**
	 * 进入新的一个月
	 * @param month
	 */
	public void onEnterMonth(int month) {
		
	}
	
	private final class TickService extends AbstractIdleService {
		private static final long DefaultTickTimeMillis = 1000l;
		private PeriodicTaskHandle periodicTaskHandle;

		@Override
		protected void shutDown() throws Exception {
			if (periodicTaskHandle != null) {
				periodicTaskHandle.cancel();
			}

			if (logger.isDebugEnabled()) {
				logger.debug("activitySystem shutdown");
			}
		}

		@Override
		protected void startUp() throws Exception {
			TaskManager taskManager = GameContext.getTaskManager();
			periodicTaskHandle = taskManager.schedulePeriodicTask(new Task() {
				public void run() {
					try {
						scheduleTask.schedule();
					} catch (Throwable t) {
						logger.error("activitySystem tick error!", t);
					}
				}
			}, 2000l, DefaultTickTimeMillis);

			if (logger.isDebugEnabled()) {
				logger.debug("activitySystem startUp");
			}
		}

	}
		
	private final class ScheduleTask{
		private long lastSecondTime = System.currentTimeMillis();
		private long lastMinuteTime = System.currentTimeMillis();
		private long lastHourTime = System.currentTimeMillis();
		private long lastDayTime = System.currentTimeMillis();
		private boolean enterHalfHourFlag = false;
		private boolean enterHourFlag = false;
		private boolean enterDayFlag = false;
		private long HourInMillis = 60 * 60 * 1000L;
		
		public void schedule() {
			Calendar rightNow = Calendar.getInstance();
			long timeInMillis = rightNow.getTimeInMillis();
			int hour = rightNow.get(Calendar.HOUR_OF_DAY);
			int minute = rightNow.get(Calendar.MINUTE);
			int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
			int month = rightNow.get(Calendar.MONTH);
			boolean bom = rightNow.get(Calendar.DAY_OF_MONTH) == 1;// 是否进入了新的一个月
			boolean bow = rightNow.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;// 是都进入了新的一周

			if (minute == 30) {
				if (!enterHalfHourFlag) {
					enterHalfHourFlag = true;
					try {
						onEnterHalfHour(hour);
					} catch (Throwable e) {
						logger.error("", e);
					}
				}
			} else {
				enterHalfHourFlag = false;
			}

			if (minute == 0) {
				if (!enterHourFlag) {
					enterHourFlag = true;
					try {
						onEnterHour(hour);
					} catch (Throwable e) {
						logger.error("", e);
					}
				}
			} else {
				enterHourFlag = false;
			}

			if (hour == 0 && minute == 0) {
				if (!enterDayFlag) {
					enterDayFlag = true;
					try {
						try {
							onEnterDay(dayOfMonth);
						} catch (Throwable e) {
							logger.error("", e);
						}
						if (bom) {
							try {
								onEnterMonth(month);
							} catch (Throwable e) {
								logger.error("", e);
							}
						}
						if (bow) {
							try {
								onEnterWeek();
							} catch (Throwable e) {
								logger.error("", e);
							}
						}
					} catch (Throwable e) {
						logger.error("", e);
					}
				}
			} else {
				enterDayFlag = false;
			}

			if (timeInMillis - lastDayTime >= HourInMillis * 24) {
				lastDayTime = timeInMillis;
				try {
					onDayTimer();
				} catch (Throwable e) {
					logger.error("", e);
				}
			}
			if (timeInMillis - lastHourTime >= HourInMillis) {
				lastHourTime = timeInMillis;
				try {
					onHourTimer();
				} catch (Throwable e) {
					logger.error("", e);
				}
			}
			if (timeInMillis - lastMinuteTime >= HourInMillis / 60) {
				lastMinuteTime = timeInMillis;
				try {
					onMinuteTimer();
				} catch (Throwable e) {
					logger.error("", e);
				}
			}
			if (timeInMillis - lastSecondTime >= 1000L) {
				lastSecondTime = timeInMillis;
				onSecondTimer();
			}
		}
	}


	
	
}
