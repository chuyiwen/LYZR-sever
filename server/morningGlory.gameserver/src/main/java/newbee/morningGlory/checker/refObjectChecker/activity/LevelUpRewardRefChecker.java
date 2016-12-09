package newbee.morningGlory.checker.refObjectChecker.activity;

import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;
import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.ref.LevelUpRewardRef;

public class LevelUpRewardRefChecker extends BaseRefChecker<LevelUpRewardRef>{

	@Override
	public String getDescription() {
		return "升级奖励";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		LevelUpRewardRef ref = (LevelUpRewardRef)gameRefObject;
		
		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "升级奖励奖励值非法!!!");
			}
		}
	}

}
