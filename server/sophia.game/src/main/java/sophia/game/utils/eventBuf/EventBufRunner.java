package sophia.game.utils.eventBuf;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * 事件执行器<br>
 * 
 * @version 1.0
 */
public final class EventBufRunner implements Runnable {
	private static final Logger logger = Logger.getLogger(EventBufRunner.class);
	private List<IDoer> doers;
	private Executable executor;

	public EventBufRunner(List<IDoer> doers, Executable executor) {
		this.doers = doers;
		this.executor = executor;
	}

	@Override
	public void run() {
		try {
			if (doers == null || executor == null)
				throw new NullPointerException();
			for (IDoer doer : doers) {
				try {
					executor.execute(doer);
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		} catch (Throwable t) {
			logger.error("", t);
		}
	}

}
