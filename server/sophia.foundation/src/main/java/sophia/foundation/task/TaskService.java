package sophia.foundation.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import sophia.foundation.task.impl.RecurringTaskHandle;
import sophia.foundation.task.impl.TaskRunnable;
import sophia.foundation.task.impl.TaskSchedulerImpl;
import sophia.foundation.util.DateWrapper;

import com.google.common.util.concurrent.AbstractIdleService;


public class TaskService extends AbstractIdleService implements TaskManager {
	private static final Logger logger=Logger.getLogger(TaskService.class.getName());
	
	public static final String Enable_Property = "sophia.task.TaskService.enable";
	public static final String Processor_Count_Property = "sophia.task.TaskService.processorCount";
	
	public static final int Default_Processor_Count = 50;
	
	private TaskSchedulerImpl taskScheduler;
	private final int processorCount;
	
	public TaskService(int processorCount) {
		this.processorCount = processorCount;
	}
	
//	@Override
//	protected void doReady() {
//		// unused
//	}
//	
//	@Override
//	protected void doStart() {
//		if (taskScheduler == null) {
//			taskScheduler = new TaskSchedulerImpl(processorCount);
//		}
//	}
//	
//	@Override
//	public void doStop() {
//		taskScheduler.shutdown();
//	}
	@Override
	public PeriodicTaskHandle scheduleMonthPeriodicTask(Task task, int dayOfMonth, long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeriodicTaskHandle scheduleDayPeriodicTask(Task task, int hourOfDay) {
		final long period = 1 * 24 * 60 * 60 * 1000;
		Calendar crtCalendar=Calendar.getInstance();
		
		crtCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		crtCalendar.set(Calendar.MINUTE, 0);
		crtCalendar.set(Calendar.SECOND, 0);
		
		Date crtDate=crtCalendar.getTime();
		
		Date currentDate = new Date();
		if(crtDate.before(currentDate))
		{
			crtDate=addDay(crtDate,1);
		}
		
		long delay =crtDate.getTime()- currentDate.getTime();
		
		if(logger.isInfoEnabled())
		{
			DateFormat currentDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String template1="【每天某时刻】定时执行的任务:当前时间:%s;hourOfDay:%d;延迟时间:%s;周期:%s;";
			String str=String.format(template1,currentDateFormat.format(currentDate),hourOfDay,formatDiffDate(delay),formatDiffDate(period));
			
			logger.info(str);
		}
		return schedulePeriodicTask(task, delay, period);
	}
	
	private final Date addDay(Date date,int dayNum)
	{
		Calendar oldCanlendar=Calendar.getInstance();
		oldCanlendar.setTime(date);
		oldCanlendar.add(Calendar.DAY_OF_MONTH, dayNum);
		return oldCanlendar.getTime();
	}
	
	private final Date addHour(Date date,int hourNum)
	{
		Calendar oldCanlendar=Calendar.getInstance();
		oldCanlendar.setTime(date);
		oldCanlendar.add(Calendar.HOUR_OF_DAY, hourNum);
		return oldCanlendar.getTime();
	}
	
	private static String formatDiffDate(long milisencod)
	{
		long secondDiff=milisencod/1000;
		long day=secondDiff/(24*3600);
		
		long secondH=secondDiff-day*24*3600;
		long hour=secondH/3600;
		
		long secondM=secondH-hour*3600;
		long minute=secondM/60;
		
		long secondS=secondM-minute*60;
//		long second=secondS/60;
		
		String template="%d天%d时%d分%d秒";
		String str=String.format(template, day,hour,minute,secondS);
		return str;
	}
	
	@Override
	public PeriodicTaskHandle schedulePerHourPeriodicTask(Task task) {
		final long period = 1 * 60 * 60 * 1000;

		Date currentDate = new Date();
		
		Calendar crtCalendar=Calendar.getInstance();
		crtCalendar.setTime(currentDate);
		crtCalendar.set(Calendar.MINUTE,0);
		crtCalendar.set(Calendar.SECOND,0);
		
		Date crtDate=crtCalendar.getTime();
		
		if(crtDate.before(currentDate))
		{
			crtDate=addHour(crtDate,1);
		}

		long delay =crtDate.getTime()- currentDate.getTime();
		
		if(logger.isInfoEnabled())
		{
			DateFormat currentDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String template1="【每天每小时】定时执行的任务:当前时间:%s;延迟时间:%s;周期:%s;";
			String str=String.format(template1,currentDateFormat.format(currentDate),formatDiffDate(delay),formatDiffDate(period));
			
			logger.info(str);
		}
		return schedulePeriodicTask(task, delay, period);
	}
	
	@Override
	public PeriodicTaskHandle scheduleDayOfWeekPeriodicTask(Task task, int dayOfWeek, int hourOfDay) {

		final long period = 7 * 24 * 60 * 60 * 1000;

		Date currentDate = new Date();
		long delay=DateWrapper.getDelayTime(currentDate,dayOfWeek, hourOfDay);
		
		if(logger.isInfoEnabled())
		{
			DateFormat currentDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String template1="【每周某天某时刻】定时执行的任务:当前时间:%s;dayOfWeek:%d;hourOfDay:%d;延迟时间:%s;周期:%s;";
			String str=String.format(template1,currentDateFormat.format(currentDate),dayOfWeek, hourOfDay,formatDiffDate(delay),formatDiffDate(period));
			
			logger.info(str);
		}
		
		return schedulePeriodicTask(task, delay, period);
	}
	
	@Override
	public PeriodicTaskHandle schedulePeriodicTask(Task task, long delay,
			long period) {
		long startTime = System.currentTimeMillis() + delay;
		
		RecurringTaskHandle handle = taskScheduler.scheduleRecurringTask(new TaskRunner(task), startTime, period);
		handle.start();
		return new PeriodicTaskHandleImpl(handle);
	}

	@Override
	public Future<?> scheduleTask(Task task) {
		if (task == null)
			throw new NullPointerException("Task must not be null");
		
		return taskScheduler.scheduleTask(new TaskRunner(task));
	}

	@Override
	public Future<?> scheduleTask(Task task, long delay) {
		if (task == null)
			throw new NullPointerException("Task must not be null");
		
		long startTime = System.currentTimeMillis() + delay;

		if (delay < 0)
			throw new IllegalArgumentException("Delay must not be negative");
		
		return taskScheduler.scheduleTask(new TaskRunner(task), startTime);
	}

	private static class TaskRunner implements TaskRunnable {
		private final String taskType;
		private final Task task;
		
		TaskRunner(Task task) {
			this.taskType = Task.class.getName();
			this.task = task;
		}
		
		@Override
		public String getBaseTaskType() {
			return taskType;
		}

		@Override
		public void run() throws Exception {
			task.run();
		}
		
	}
	
	private static class PeriodicTaskHandleImpl implements PeriodicTaskHandle {
		private RecurringTaskHandle backingHandle;
		
		PeriodicTaskHandleImpl(RecurringTaskHandle handle) {
			this.backingHandle = handle;
		}
		
		@Override
		public void cancel() {
			if (backingHandle != null) {
				backingHandle.cancel();
			}
		}
		
	}

	@Override
	protected void shutDown() throws Exception {
		taskScheduler.shutdown();
	}

	@Override
	protected void startUp() throws Exception {
		if (taskScheduler == null) {
			taskScheduler = new TaskSchedulerImpl(processorCount);
		}
	}
}
