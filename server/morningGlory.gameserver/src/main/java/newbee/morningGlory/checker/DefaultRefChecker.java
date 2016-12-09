package newbee.morningGlory.checker;

import sophia.game.ref.GameRefObject;

public class DefaultRefChecker extends BaseRefChecker<GameRefObject> {

	private RefCheckerManager refCheckerManager = new RefCheckerManager();
	
	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		refCheckerManager.getOutputCtrl().warn( gameRefObject + " 使用了错误的Ref校验器!" );
	}

}
