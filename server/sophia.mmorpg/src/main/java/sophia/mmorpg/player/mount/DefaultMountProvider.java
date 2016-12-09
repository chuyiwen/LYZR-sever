package sophia.mmorpg.player.mount;

import java.util.UUID;

import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;

public class DefaultMountProvider implements GameObjectProvider<Mount> {
	private static final GameObjectProvider<Mount> instance = new DefaultMountProvider();

	private DefaultMountProvider() {

	}

	public static GameObjectProvider<Mount> getInstance() {
		return instance;
	}

	@Override
	public Mount get(Class<Mount> type) {
		return new Mount();
	}

	@Override
	public Mount get(Class<Mount> type, Object... args) {
		
		String mountRefId = (String) args[0];
		MountRef mountRef = (MountRef) GameRoot.getGameRefObjectManager().getManagedObject(mountRefId);
		if(mountRef==null)
			return null;

		Mount mount = new Mount();
		mount.setId(UUID.randomUUID().toString());
		mount.setMountRef(mountRef);
		mount.setExp(0);
		return mount;
	}

}
