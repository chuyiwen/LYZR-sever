package newbee.morningGlory.checker.refObjectChecker.activity;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.ref.WingRewardRef;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;

public class WingRewardRefChecker extends BaseRefChecker<WingRewardRef>{

	@Override
	public String getDescription() {
		return "翅膀进阶";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		WingRewardRef ref = (WingRewardRef)gameRefObject;
		
		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "翅膀进阶奖励值非法!!!");
			}
		}
	}

}
