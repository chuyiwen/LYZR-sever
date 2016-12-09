package newbee.morningGlory.mmorpg.player.dailyQuest;

import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.player.Player;

public class MGDailyQuestTimer implements SFTimeChimeListener {

	private MGDailyQuestManager dailyQuestManager;
	
	private Player player;
	
	public MGDailyQuestTimer(MGDailyQuestManager dailyQuestManager, Player player) {
		this.dailyQuestManager = dailyQuestManager;
		this.player = player;
	}
	
	@Override
	public void handleServiceShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleTimeChimeCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleTimeChime() {
		dailyQuestManager.twentyFourHourResetEvent();
		MGDailyQuestComponent dailyQuestComponent = (MGDailyQuestComponent) player.getTagged(MGDailyQuestComponent.Tag);
		dailyQuestComponent.sendDailyQuestList();
	}

}
