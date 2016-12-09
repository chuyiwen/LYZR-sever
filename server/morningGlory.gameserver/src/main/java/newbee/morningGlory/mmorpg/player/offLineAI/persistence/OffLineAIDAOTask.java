package newbee.morningGlory.mmorpg.player.offLineAI.persistence;

import org.apache.log4j.Logger;

import sophia.mmorpg.core.timer.SFTimeChimeListener;

public class OffLineAIDAOTask implements SFTimeChimeListener{

	private static final Logger logger = Logger.getLogger(OffLineAIDAOTask.class);
	private static OffLineAIDAOTask intsance = new OffLineAIDAOTask();

	public static OffLineAIDAOTask getInstance() {
		return intsance;
	}
	@Override
	public void handleServiceShutdown() {
		
	}

	@Override
	public void handleTimeChimeCancel() {
		
	}

	private long count = 0;
	@Override
	public void handleTimeChime() {
		try {
			this.count++;
			if (this.count % OffLineAIDAOMgr.DAO_AFTERWRITE_TIME == 0) {
				OffLineAIDAOMgr.afterWrite();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

}
