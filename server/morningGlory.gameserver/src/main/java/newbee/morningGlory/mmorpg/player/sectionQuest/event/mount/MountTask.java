package newbee.morningGlory.mmorpg.player.sectionQuest.event.mount;

import newbee.morningGlory.mmorpg.player.sectionQuest.MGSectionQuestComponent;

import org.apache.log4j.Logger;

import sophia.foundation.task.Task;
import sophia.mmorpg.player.Player;

public class MountTask implements Task {
	private static final Logger logger = Logger.getLogger(MountTask.class);
	private final Player player;

	public MountTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			MGSectionQuestComponent.getInstance().timeUp(player);
		} catch (Throwable e) {
			logger.error(" Throwable ", e);
		}
	}

}