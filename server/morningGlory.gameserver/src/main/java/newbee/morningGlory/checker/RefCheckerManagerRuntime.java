package newbee.morningGlory.checker;

import sophia.game.ref.GameRefObject;
import sophia.game.ref.GameRefObjectLoader;

public interface RefCheckerManagerRuntime {

	public CheckOutputCtrl getOutputCtrl();
	
	public RefChecker<?> getRefObjectChecker(GameRefObject gameRefObject);
	
	public RefChecker<?> getRefObjectChecker(GameRefObjectLoader<?> loadSlaver);
}
