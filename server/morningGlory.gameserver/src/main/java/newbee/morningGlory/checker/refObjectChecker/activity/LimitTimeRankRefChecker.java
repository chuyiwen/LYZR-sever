package newbee.morningGlory.checker.refObjectChecker.activity;

import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.LimitTimeRankRef;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;

public class LimitTimeRankRefChecker extends BaseRefChecker<LimitTimeRankRef> {

	@Override
	public String getDescription() {
		return "限时冲榜";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		LimitTimeRankRef ref = (LimitTimeRankRef) gameRefObject;

		int rankType = ref.getRankType();
		if (rankType <= 0) {
			error(ref, "限时冲榜类型值非法!!!");
		}

		if (!ref.getRankInterval().matches("[0-9]+-[0-9]+")) {
			error(ref, "限时冲榜排名区间值非法!!!");
		}

		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "限时冲榜奖励值非法!!!");
			}
		}

		for (Entry<Byte, List<ItemPair>> entry : ref.professionRewardMap().entrySet()) {
			byte professionId = entry.getKey();
			if (professionId != 0 || professionId != 1 || professionId != 2 || professionId != 3) {
				error(ref, "限时冲榜奖励职业值非法!!!");
			}
			
			List<ItemPair> itemPairs = entry.getValue();
			for (ItemPair itemPair : itemPairs) {
				if (itemPair.getNumber() < 0) {
					error(ref, "限时冲榜奖励值非法!!!");
				}
			}
		}
	}

}
