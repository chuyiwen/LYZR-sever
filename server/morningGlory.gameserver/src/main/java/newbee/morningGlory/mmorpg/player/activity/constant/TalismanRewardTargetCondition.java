package newbee.morningGlory.mmorpg.player.activity.constant;

public class TalismanRewardTargetCondition {
	private int number;
	private int level;

	public TalismanRewardTargetCondition() {

	}

	public TalismanRewardTargetCondition(int level, int number) {
		this.number = number;
		this.level = level;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
