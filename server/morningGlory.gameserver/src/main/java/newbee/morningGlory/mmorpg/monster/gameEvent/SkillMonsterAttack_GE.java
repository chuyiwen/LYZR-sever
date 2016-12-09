package newbee.morningGlory.mmorpg.monster.gameEvent;


public class SkillMonsterAttack_GE {
	private String fighterId;
	
	public SkillMonsterAttack_GE (String fighterId) {
		this.setFighterId(fighterId);
	}

	public String getfighterId() {
		return fighterId;
	}

	public void setFighterId(String fighterId) {
		this.fighterId = fighterId;
	}

}
