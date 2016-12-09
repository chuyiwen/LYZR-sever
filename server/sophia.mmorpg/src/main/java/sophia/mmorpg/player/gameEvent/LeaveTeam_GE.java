package sophia.mmorpg.player.gameEvent;

public class LeaveTeam_GE {
	private String sceneRefId;

	public LeaveTeam_GE(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}

	public String getSceneRefId() {
		return sceneRefId;
	}

	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}
}
