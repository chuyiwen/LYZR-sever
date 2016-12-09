package newbee.morningGlory.mmorpg.monster.gameEvent;

public class SkillMonsterLevelUp_GE {

	private String nextLevelRefId;

	public SkillMonsterLevelUp_GE(String nextLevelRefId) {
		this.nextLevelRefId = nextLevelRefId;
	}

	public String getNextLevelRefId() {
		return nextLevelRefId;
	}

	public void setNextLevelRefId(String nextLevelRefId) {
		this.nextLevelRefId = nextLevelRefId;
	}

}
