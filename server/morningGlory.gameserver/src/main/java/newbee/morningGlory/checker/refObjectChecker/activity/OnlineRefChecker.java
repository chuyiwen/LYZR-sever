package newbee.morningGlory.checker.refObjectChecker.activity;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.ref.OnlineRef;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;

public class OnlineRefChecker extends BaseRefChecker<OnlineRef>{

	@Override
	public String getDescription() {
		return "在线时长";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		OnlineRef ref = (OnlineRef)gameRefObject;
		
		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "在线时长奖励值非法!!!");
			}
		}
	}

}
