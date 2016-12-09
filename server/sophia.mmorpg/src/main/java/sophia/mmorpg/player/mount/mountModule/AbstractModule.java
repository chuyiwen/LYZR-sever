package sophia.mmorpg.player.mount.mountModule;

import sophia.mmorpg.player.mount.MountManager;


public class AbstractModule {
	
	protected MountManager owner;

	protected AbstractModule(MountManager owner) {
		this.owner = owner;
	}

}
