package sophia.mmorpg.monster.gameEvent;

import sophia.mmorpg.monster.Monster;

public class MonsterLeaveWorld_GE {

	private String sceneRefId;
	
	private Monster monster;

	public MonsterLeaveWorld_GE(String sceneRefId, Monster monster) {
		this.sceneRefId = sceneRefId;
		this.monster = monster;
	}
	
	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public String getSceneRefId() {
		return sceneRefId;
	}

	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}
}
