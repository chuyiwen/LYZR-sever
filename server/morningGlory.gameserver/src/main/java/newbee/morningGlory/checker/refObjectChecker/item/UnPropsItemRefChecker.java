package newbee.morningGlory.checker.refObjectChecker.item;

import newbee.morningGlory.checker.BaseRefChecker;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.item.ref.UnPropsItemRef;

public class UnPropsItemRefChecker extends BaseRefChecker<UnPropsItemRef> {

	@Override
	public String getDescription() {
		return "UnPropsItemRef";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		UnPropsItemRef ref = (UnPropsItemRef) gameRefObject;
		String refId = ref.getId();
		if (null == refId || "".equals(refId)) {
			error(gameRefObject, "UnPropsItemRef<refId>为空");
		}

		if (null == GameRoot.getGameRefObjectManager().getManagedObject(refId)) {
			error(gameRefObject, "UnPropsItemRef refId不存在:" + refId);
		}
	}

}
