package sophia.mmorpg.core;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class CDMgrTest {
	private static final Logger logger = Logger.getLogger(CDMgrTest.class);
	private static final long publicCDTime = 1000;
	private static final long thisCDTime = 2000;

	// private CDMgr cdManager = new CDMgr(publicCDTime);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsOutOfCD() {
		CDMgr cdManager = new CDMgr(publicCDTime);
		String id = "id1";

		if (!cdManager.isCDStarted(id)) {
			cdManager.startCD(id, thisCDTime);

		}

		assertTrue(cdManager.isOutOfCD(id));

		while (!cdManager.isOutOfCD(id)) {
			long interval = 100;
			logger.info("hello: " + System.currentTimeMillis());
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		cdManager.update(id);

		// second time
		long startTime2 = System.currentTimeMillis();
		logger.info("CD start2: " + startTime2);
		while (!cdManager.isOutOfCD(id)) {
			long interval = 100;
			logger.info("hello2: " + System.currentTimeMillis());
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long endTime2 = System.currentTimeMillis();
		long period2 = endTime2 - startTime2;
		logger.info("CD end2: " + endTime2);
		logger.info("CD time2: " + period2);
		assertTrue(period2 >= thisCDTime);

	}

}
