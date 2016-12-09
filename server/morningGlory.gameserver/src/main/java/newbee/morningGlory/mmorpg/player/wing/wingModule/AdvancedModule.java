package newbee.morningGlory.mmorpg.player.wing.wingModule;


public class AdvancedModule extends AbstractModule{

	
	public AdvancedModule(WingManager wingManager) {
		super(wingManager);
	}

	public void rewardExp(int exp) {
		long totalExp = wingManager.getPlayerWing().getExp();
		
		totalExp += exp;
		
		wingManager.getPlayerWing().setExp(totalExp);
		
		wingManager.getLevelModule().rewardExp();
	}

}
