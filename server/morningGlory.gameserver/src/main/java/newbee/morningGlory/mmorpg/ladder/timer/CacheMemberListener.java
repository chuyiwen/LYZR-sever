package newbee.morningGlory.mmorpg.ladder.timer;

import newbee.morningGlory.mmorpg.ladder.MGLadderMemberMgr;
import sophia.mmorpg.core.timer.SFTimeChimeListener;

public class CacheMemberListener implements SFTimeChimeListener{

	@Override
	public void handleServiceShutdown() {
	}

	@Override
	public void handleTimeChimeCancel() {
	}

	@Override
	public void handleTimeChime() {
		MGLadderMemberMgr.removeChacheMember();
	}

}
