package sophia.mmorpg.player.gameEvent;

public class SinglePlayerTeam_GE {
	private String sceneRefId;

	public SinglePlayerTeam_GE(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}

	public String getSceneRefId() {
		return sceneRefId;
	}

	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}
}
