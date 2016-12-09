package newbee.morningGlory.mmorpg.gameInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.gameInstance.quest.MGGameInstanceQuest;

public final class MGGameInstanceRestore {
	// gameInstanceRefId, GameInstanceId
	private Map<String, String> refIdToGameInstanceId = new HashMap<String, String>();
	
	private String currentGameInstanceId;
	
	private List<MGGameInstanceQuest> acceptQuest;

	public List<MGGameInstanceQuest> getAcceptQuest() {
		return acceptQuest;
	}

	public void setAcceptQuest(List<MGGameInstanceQuest> acceptQuest) {
		this.acceptQuest = acceptQuest;
	}

	public String getCurrentGameInstanceId() {
		return currentGameInstanceId;
	}

	public void setCurrentGameInstanceId(String currentGameInstanceId) {
		this.currentGameInstanceId = currentGameInstanceId;
	}

	public Map<String, String> getRefIdToGameInstanceId() {
		return refIdToGameInstanceId;
	}

	public void setRefIdToGameInstanceId(Map<String, String> refIdToGameInstanceId) {
		this.refIdToGameInstanceId = refIdToGameInstanceId;
	}
}
