package newbee.morningGlory.checker.refObjectChecker.activity;

import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;
import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.ref.DayOnlineRef;

public class DayOnlineRefChecker extends BaseRefChecker<DayOnlineRef> {

	@Override
	public String getDescription() {
		return "每日累计计时";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		DayOnlineRef ref = (DayOnlineRef)gameRefObject;
		
		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "每日累计计时奖励值非法!!!");
			}
		}
	}

}
