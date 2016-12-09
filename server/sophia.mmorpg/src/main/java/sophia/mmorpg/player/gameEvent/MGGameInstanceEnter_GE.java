package sophia.mmorpg.player.gameEvent;

public class MGGameInstanceEnter_GE {
	private String gameInstanceRefId;

	private String gameInstanceSceneId;

	public MGGameInstanceEnter_GE() {
	}

	public MGGameInstanceEnter_GE(String gameInstanceRefId, String gameInstanceSceneId) {
		this.gameInstanceRefId = gameInstanceRefId;
		this.gameInstanceSceneId = gameInstanceSceneId;
	}

	public String getGameInstanceRefId() {
		return gameInstanceRefId;
	}

	public void setGameInstanceRefId(String gameInstanceRefId) {
		this.gameInstanceRefId = gameInstanceRefId;
	}

	public String getGameInstanceSceneId() {
		return gameInstanceSceneId;
	}

	public void setGameInstanceSceneId(String gameInstanceSceneId) {
		this.gameInstanceSceneId = gameInstanceSceneId;
	}
}
