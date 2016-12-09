package newbee.morningGlory.mmorpg.ladder.timer;

import newbee.morningGlory.mmorpg.ladder.MGLadderSystemMessageFacade;
import sophia.mmorpg.core.timer.SFTimeChimeListener;

public class SystemMessageListener implements SFTimeChimeListener{

	@Override
	public void handleServiceShutdown() {
	}

	@Override
	public void handleTimeChimeCancel() {
	}

	@Override
	public void handleTimeChime() {
		MGLadderSystemMessageFacade.sendNewStarNotice();
	}
	
}
