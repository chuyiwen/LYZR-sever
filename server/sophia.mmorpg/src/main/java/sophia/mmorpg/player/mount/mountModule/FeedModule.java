package sophia.mmorpg.player.mount.mountModule;

import sophia.mmorpg.player.mount.MountManager;

public class FeedModule extends AbstractModule {

	/**
	 * @param owner
	 *           
	 */
	public FeedModule(MountManager owner) {
		super(owner);
	}

	public void rewardExp(int exp) {
		
		long totalExp = owner.getCrtMount().getExp();
		totalExp += exp;
		owner.getCrtMount().setExp(totalExp); 
		
		owner.getLevelModule().rewardExp();
	}

}
