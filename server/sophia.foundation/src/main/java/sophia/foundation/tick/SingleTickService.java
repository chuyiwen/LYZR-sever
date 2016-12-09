/**
 * 
 */
package sophia.foundation.tick;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.AbstractIdleService;



public final class SingleTickService extends AbstractIdleService implements
		TickService {
	private static final Logger logger = Logger
			.getLogger(SingleTickService.class.getName());

	private LostedTimeListenerManager lostedTimeListenerManager = new SimpleLostedTimeListenerManager();

	public static final String Interval_Time_Property = "may.tick.TickService.intervalTime";

	public static final long Default_Interval_Time = 2000;

	private long startTime;

	private long stopTime;

	private long intervalTime = Default_Interval_Time;

	private final AtomicLong tickCounter = new AtomicLong(0);

	private final ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(1);

	private ScheduledFuture<?> future;

	@Override
	protected void shutDown() throws Exception {
		stopTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("TickService will stopping.");
		}

		if (future != null)
			future.cancel(false);
	}

	@Override
	protected void startUp() throws Exception {
		startTime = System.currentTimeMillis();
		future = executor.scheduleAtFixedRate(new TickTask(), 0, intervalTime,
				TimeUnit.MILLISECONDS);
	}

	@Override
	public long getIntervalTime() {
		return intervalTime;
	}

	@Override
	public LostedTimeListenerManager getLostedTimeListenerManager() {
		return lostedTimeListenerManager;
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
	public void setIntervalTime(long intervalTime) {
		this.intervalTime = intervalTime;
	}

	private class TickTask implements Runnable {
		@Override
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("Current LostedTimeListener number: "
						+ getLostedTimeListenerManager().size());
			}

			try {
				Collection<LostedTimeListener> collection = getLostedTimeListenerManager()
						.getCollection();

				for (LostedTimeListener listener : collection) {
					listener.lostedTimeEvent(tickCounter.get(), intervalTime);
				}
				tickCounter.incrementAndGet();
			} catch (Exception e) {
				logger.error("Tick Task Exception.", e);
			}
		}

	}
}
