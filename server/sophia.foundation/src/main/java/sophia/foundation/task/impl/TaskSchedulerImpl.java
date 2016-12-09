package sophia.foundation.task.impl;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import sophia.foundation.task.TaskRejectedException;



public class TaskSchedulerImpl implements TaskScheduler {
	private static final Logger logger = Logger
			.getLogger(TaskSchedulerImpl.class.getName());

	private final ScheduledExecutorService executor;

	private volatile boolean isShutdown = false;

	private final AtomicInteger waitingSize = new AtomicInteger();
	
	public TaskSchedulerImpl(int requestedThreads) {
		if (logger.isDebugEnabled()) {
			logger.debug("Using " + requestedThreads
					+ " task consumer threads.");
		}

		this.executor = Executors.newScheduledThreadPool(requestedThreads);
	}

	@Override
	public TaskQueue createTaskQueue() {
		if (isShutdown)
			throw new IllegalStateException("Scheduler is shutdown");
		return new TaskQueueImpl();
	}

	@Override
	public RecurringTaskHandle scheduleRecurringTask(TaskRunnable task,
			long startTime, long period) {
		if (period <= 0)
			throw new IllegalArgumentException("Illegal period: " + period);

		return new RecurringTaskHandleImpl(new TaskDetail(task, startTime,
				period));
	}

	@Override
	public Future<?> scheduleTask(TaskRunnable task) {
		Future<?> future = null;
		try {
			TaskDetail detail = new TaskDetail(task, System.currentTimeMillis());
			future = executor.submit(new TaskRunner(detail));
		} catch (RejectedExecutionException ree) {
			throw new TaskRejectedException("Couldn't schedule task", ree);
		}
		
		return future;
	}

	@Override
	public Future<?> scheduleTask(TaskRunnable task, long startTime) {
		Future<?> future = null;
		try {
			TaskDetail detail = new TaskDetail(task, startTime);
			future = executor.schedule(new TaskRunner(detail), startTime
					- System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ree) {
			throw new TaskRejectedException("Couldn't schedule task", ree);
		}
		
		return future;
	}

	public void shutdown() {
		synchronized (this) {
			if (isShutdown)
				throw new IllegalStateException("Already shutdown");
			isShutdown = true;
		}
		executor.shutdown();
	}

	private class RecurringTaskHandleImpl implements RecurringTaskHandle {
		private final TaskDetail taskDetail;
		private boolean isCancelled = false;
		private boolean isStarted = false;
		private volatile ScheduledFuture<?> future = null;

		RecurringTaskHandleImpl(TaskDetail taskDetail) {
			if (isShutdown)
				throw new IllegalStateException("Scheduler is shutdown");
			this.taskDetail = taskDetail;
		}

		@Override
		public void cancel() {
			synchronized (this) {
//				if (isCancelled)
//					throw new IllegalStateException("Handle already cancelled");
				isCancelled = true;
			}
			
			if (future != null) {
				future.cancel(false);
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("RecurringTaskHandleImpl, wait for future");
					}
					future.get();
				} catch (Exception e) {
				}
			}
		}

		@Override
		public void start() {
			synchronized (this) {
				if (isCancelled)
					throw new IllegalStateException("Handle already cancelled");
				if ((future != null) || (isStarted))
					throw new IllegalStateException("Handle already used");
				isStarted = true;
			}

			long delay = taskDetail.startTime - System.currentTimeMillis();
			try {
				future = executor.scheduleAtFixedRate(
						new TaskRunner(taskDetail), delay, taskDetail.period,
						TimeUnit.MILLISECONDS);
			} catch (RejectedExecutionException ree) {
				throw new TaskRejectedException("The system has run out of "
						+ "resources and cannot start " + "the requested task",
						ree);
			}
		}

	}

	private static class TaskDetail {
		final TaskRunnable task;
		volatile long startTime;
		final long period;
//		final TaskQueueImpl queue;
		
		TaskDetail(TaskRunnable task, long startTime) {
			this(task, startTime, 0);
		}

		TaskDetail(TaskRunnable task, long startTime, long period) {
			if (task == null)
				throw new NullPointerException("Task cannot be null");

			this.task = task;
			this.startTime = startTime;
			this.period = period;
//			this.queue = null;
		}
		
		TaskDetail(TaskRunnable task, TaskQueueImpl queue) {
			if (task == null)
				throw new NullPointerException("Task cannot be null");
			if (queue == null)
				throw new NullPointerException("TaskQueue cannot be null");

			this.task = task;
			this.startTime = System.currentTimeMillis();
			this.period = 0;
//			this.queue = queue;
		}

		boolean isRecurring() {
			return period != 0;
		}
	}

	private static class TaskRunner implements Runnable {
		private final TaskDetail taskDetail;

		TaskRunner(TaskDetail taskDetail) {
			this.taskDetail = taskDetail;
		}

		@Override
		public void run() {
			try {
				taskDetail.task.run();
			} catch (Exception e) {
				if (taskDetail.isRecurring()) {
					logger.warn("failed to run task " + taskDetail.task, e);
				} else {
					logger.warn("failed to run recurrence of task "
							+ taskDetail.task, e);
				}
			}
		}
	}
	
	private final class TaskQueueImpl implements TaskQueue {
		private final LinkedList<TaskDetail> queue = new LinkedList<TaskDetail>();
		private boolean inScheduler = false;

		public void addTask(TaskRunnable task) {
			TaskDetail detail = new TaskDetail(task, this);
			waitingSize.incrementAndGet();
			synchronized (this) {
				if (inScheduler) {
					queue.offer(detail);
				} else {
					inScheduler = true;
					executor.submit(new TaskRunner(detail));
				}
			}
		}

//		void scheduleNextTask() {
//			synchronized (this) {
//				if (queue.isEmpty()) {
//					inScheduler = false;
//				} else {
//					TaskDetail detail = queue.poll();
//					detail.startTime = System.currentTimeMillis();
//					executor.submit(new TaskRunner(detail));
//				}
//			}
//		}
	}
}
