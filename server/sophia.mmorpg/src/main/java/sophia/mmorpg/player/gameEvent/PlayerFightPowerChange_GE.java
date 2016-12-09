package sophia.mmorpg.player.gameEvent;

public class PlayerFightPowerChange_GE {
	private int fightPower;

	public PlayerFightPowerChange_GE(int fightPower) {
		this.fightPower = fightPower;
	}

	public int getFightPower() {
		return fightPower;
	}

	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}

}
