package newbee.morningGlory.mmorpg.sortboard;

import org.apache.log4j.Logger;

import sophia.mmorpg.core.timer.SFTimeChimeListener;

public class SortboardTimer implements SFTimeChimeListener {
	private static Logger logger = Logger.getLogger(SortboardTimer.class);
	@Override
	public void handleServiceShutdown() {
		
	}

	@Override
	public void handleTimeChimeCancel() {
		
	}

	@Override
	public void handleTimeChime() {
		try {
			SortboardMgr.getInstance().newSortBoardData();
		} catch (Exception e) {
			logger.error("newSortBoardData error :" + e);
		}
	}

}
