package sophia.mmorpg.player.gameEvent;

public class MGGameInstanceLeave_GE {
	private String gameInstanceRefId;

	private String gameInstanceSceneId;

	public MGGameInstanceLeave_GE() {
	}

	public MGGameInstanceLeave_GE(String gameInstanceRefId, String gameInstanceSceneId) {
		super();
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
