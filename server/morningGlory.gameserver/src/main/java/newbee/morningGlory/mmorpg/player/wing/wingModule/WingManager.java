package newbee.morningGlory.mmorpg.player.wing.wingModule;

import newbee.morningGlory.mmorpg.player.wing.MGPlayerWing;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.SFRandomUtils;

public class WingManager {
	public static final String WingLevelUpItemRefId = "item_chibangExp";
	
	private MGPlayerWing playerWing = new MGPlayerWing();

	private AdvancedModule advancedModule;

	private LevelModule levelModule;

	private Player player;

	public WingManager() {
		advancedModule = new AdvancedModule(this);
		levelModule = new LevelModule(this);
	}

	public MGPlayerWing getPlayerWing() {
		return playerWing;
	}

	public void setPlayerWing(MGPlayerWing playerWing) {
		this.playerWing = playerWing;
	}

	public AdvancedModule getAdvancedModule() {
		return advancedModule;
	}

	public void setAdvancedModule(AdvancedModule advancedModule) {
		this.advancedModule = advancedModule;
	}

	public LevelModule getLevelModule() {
		return levelModule;
	}

	public void setLevelModule(LevelModule levelModule) {
		this.levelModule = levelModule;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void rewardExp(int totalExp) {
		getAdvancedModule().rewardExp(totalExp);
	}
	
	public static int getCritMultipleType() {
		int rdm = SFRandomUtils.random100();// [1,100]
		int critType = getBaoJiRate(rdm);
		if (critType < 1 || critType > 3) {
			critType = 1;
		}
		return critType;
	}

	public static int getBaoJiRate(int rdm) {
		int critType = 1;
		if (rdm > 0 && rdm <= 10) {
			critType = 3;
		} else if (rdm > 10 && rdm <= 50) {
			critType = 2;
		}
		return critType;
	}

}
