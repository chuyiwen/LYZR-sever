package sophia.mmorpg.pluck;

import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;

public class DefaultPluckProvider implements GameObjectProvider<Pluck> {
	private static DefaultPluckProvider instance = new DefaultPluckProvider();

	private DefaultPluckProvider() {

	}

	public static DefaultPluckProvider getInstance() {
		return instance;
	}

	@Override
	public Pluck get(Class<Pluck> type) {
		return new Pluck();
	}

	@Override
	public Pluck get(Class<Pluck> type, Object... args) {
		String pluckRefId = (String)args[0];
		PluckRef pluckRef = (PluckRef)GameRoot.getGameRefObjectManager().getManagedObject(pluckRefId);
		Pluck pluck = new Pluck(pluckRef);
		return pluck;
	}

}
