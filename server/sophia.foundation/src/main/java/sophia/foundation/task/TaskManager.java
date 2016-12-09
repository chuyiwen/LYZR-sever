package sophia.foundation.task;

import java.util.concurrent.Future;


public interface TaskManager {
	Future<?> scheduleTask(Task task);
	
	Future<?> scheduleTask(Task task, long delay);
	
	PeriodicTaskHandle schedulePeriodicTask(Task task, long delay, long period);
	
	/** 每天每小时执行的任务 */
	PeriodicTaskHandle schedulePerHourPeriodicTask(Task task);
	
	/** 每天某时刻执行的任务 */
	PeriodicTaskHandle scheduleDayPeriodicTask(Task task, int hourOfDay);
	
	/** 每周,某天,某时刻执行的任务 */
	PeriodicTaskHandle scheduleDayOfWeekPeriodicTask(Task task, int dayOfWeek, int hourOfDay);
	
	PeriodicTaskHandle scheduleMonthPeriodicTask(Task task, int dayOfMonth, long time);
}
