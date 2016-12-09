package sophia.foundation.task.impl;

import java.util.concurrent.Future;



public interface TaskScheduler {
	public Future<?> scheduleTask(TaskRunnable task);

	public Future<?> scheduleTask(TaskRunnable task, long startTime);

	public RecurringTaskHandle scheduleRecurringTask(TaskRunnable task,
			long startTime, long period);
	
	public TaskQueue createTaskQueue();
}
