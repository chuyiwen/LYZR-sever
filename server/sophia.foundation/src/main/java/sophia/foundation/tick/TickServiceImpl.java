/**
 * 
 */
package sophia.foundation.tick;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import sophia.foundation.core.FoundationContext;
import sophia.foundation.task.PeriodicTaskHandle;
import sophia.foundation.task.Task;

import com.google.common.util.concurrent.AbstractIdleService;


public class TickServiceImpl extends AbstractIdleService implements TickService {
	private static final Logger logger = Logger.getLogger(TickServiceImpl.class.getName());

	private LostedTimeListenerManager lostedTimeListenerManager = new SimpleLostedTimeListenerManager();

	public static final String Enable_Property = "sophia.tick.TickService.enable";
	public static final String Interval_Time_Property = "sophia.tick.TickService.intervalTime";

	public static final long Default_Interval_Time = 2000;

	private long startTime;

	private long stopTime;

	private long intervalTime = Default_Interval_Time;

	private final AtomicLong tickCounter = new AtomicLong(0);

	private PeriodicTaskHandle periodicTaskHandle;

	@Override
	protected void shutDown() throws Exception {
		stopTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("TickService will stopping.");
		}
		periodicTaskHandle.cancel();
	}

	@Override
	protected void startUp() throws Exception {
		startTime = System.currentTimeMillis();
		periodicTaskHandle = FoundationContext.getTaskManager().schedulePeriodicTask(new TickTask(), 0, intervalTime);
	}

	@Override
	public long getIntervalTime() {
		return intervalTime;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getStopTime() {
		return stopTime;
	}

	@Override
	public LostedTimeListenerManager getLostedTimeListenerManager() {
		return lostedTimeListenerManager;
	}

	@Override
	public void setIntervalTime(long intervalTime) {
		this.intervalTime = intervalTime;
	}

	private class TickTask implements Task {
		@Override
		public void run() throws Exception {
//			TaskManager taskManager = FoundationContext.getTaskManager();
//			long preRun = System.currentTimeMillis();
			if (logger.isDebugEnabled()) {
				logger.debug("Current LostedTimeListener number: " + getLostedTimeListenerManager().size());
			}

			Collection<LostedTimeListener> collection = getLostedTimeListenerManager().getCollection();
			
//			String name = "";
			for(LostedTimeListener listener : collection) {
//				name = listener.getClass().getSimpleName();
				listener.lostedTimeEvent(tickCounter.get(), intervalTime);
			}
			tickCounter.incrementAndGet();
//			long postRun = System.currentTimeMillis();
//			long time = postRun -preRun;
//			logger.info(name + "process time: " + time);
		}
	}
}
