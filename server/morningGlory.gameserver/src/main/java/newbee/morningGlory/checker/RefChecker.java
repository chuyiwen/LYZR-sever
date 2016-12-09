package newbee.morningGlory.checker;

import java.util.Collection;

import sophia.game.ref.GameRefObject;
import sophia.game.ref.GameRefObjectLoader;

public interface RefChecker<T extends GameRefObject> {

	public String getDescription();

	public Collection<T> delegateLoadAll(GameRefObjectLoader<T> loadSlaver);

	public void check(GameRefObject gameRefObject);
}