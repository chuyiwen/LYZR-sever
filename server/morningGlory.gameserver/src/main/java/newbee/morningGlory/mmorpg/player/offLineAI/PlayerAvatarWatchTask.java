package newbee.morningGlory.mmorpg.player.offLineAI;

import org.apache.log4j.Logger;

import sophia.mmorpg.core.timer.SFTimeChimeListener;

public class PlayerAvatarWatchTask implements SFTimeChimeListener {

	private static final Logger logger = Logger.getLogger(PlayerAvatarWatchTask.class);
	private static PlayerAvatarWatchTask intsance = new PlayerAvatarWatchTask();

	public static PlayerAvatarWatchTask getInstance() {
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
			if (this.count % PlayerAvatarMgr.PLAYERAVATAR_WATCH_TIME == 0) {
				PlayerAvatarMgr.watchPlayerAvatar();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

}
