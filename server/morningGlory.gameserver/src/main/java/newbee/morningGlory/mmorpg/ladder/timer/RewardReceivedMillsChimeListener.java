package newbee.morningGlory.mmorpg.ladder.timer;

import newbee.morningGlory.mmorpg.ladder.MGLadderMemberMgr;
import newbee.morningGlory.mmorpg.ladder.MGLadderMgr;
import newbee.morningGlory.mmorpg.ladder.MGLadderSystemMessageFacade;

import org.apache.log4j.Logger;

import sophia.mmorpg.core.timer.SFTimeChimeListener;

public class RewardReceivedMillsChimeListener implements SFTimeChimeListener {
	private static Logger logger = Logger.getLogger(RewardReceivedMillsChimeListener.class);
	
	@Override
	public void handleServiceShutdown() {
	}

	@Override
	public void handleTimeChimeCancel() {
	}

	@Override
	public void handleTimeChime() {
		long receiveRewardMillis = MGLadderMgr.getInstance().getReceiveRewardMillis();
		long now = System.currentTimeMillis();
		
		if (receiveRewardMillis > now) {
			return;
		}

		logger.debug("天梯本次奖励倒计时结束~~~");
		MGLadderMemberMgr.modifyAllMemberRewardRank();
		MGLadderSystemMessageFacade.sendWolrdNotice();
		MGLadderMgr.getInstance().calculateReceiveRewardMillis();
	}

	
}
