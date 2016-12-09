package newbee.morningGlory.checker.refObjectChecker.activity;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.ref.RideRewardRef;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;

public class RideRewardRefChecker extends BaseRefChecker<RideRewardRef>{

	@Override
	public String getDescription() {
		return "坐骑进阶";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		RideRewardRef ref = (RideRewardRef)gameRefObject;
		
		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "坐骑进阶奖励值非法!!!");
			}
		}
	}

}
