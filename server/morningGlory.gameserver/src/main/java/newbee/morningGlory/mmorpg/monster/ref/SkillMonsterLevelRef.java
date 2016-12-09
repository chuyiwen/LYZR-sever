package newbee.morningGlory.mmorpg.monster.ref;

import sophia.game.ref.AbstractGameRefObjectBase;

public class SkillMonsterLevelRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = -2162778759538668371L;

	private String monsterRefId;
	private int level;
	private int expNeed;
	private String nextMonsterRefId;

	public String getNextMonsterRefId() {
		return nextMonsterRefId;
	}

	public void setNextMonsterRefId(String nextMonsterRefId) {
		this.nextMonsterRefId = nextMonsterRefId;
	}

	public String getMonsterRefId() {
		return monsterRefId;
	}

	public void setMonsterRefId(String monsterRefId) {
		this.monsterRefId = monsterRefId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExpNeed() {
		return expNeed;
	}

	public void setExpNeed(int expNeed) {
		this.expNeed = expNeed;
	}

}
