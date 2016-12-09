package newbee.morningGlory.checker.refObjectChecker.activity;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.fund.ref.FundRef;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;

public class FundRefChecker extends BaseRefChecker<FundRef> {

	@Override
	public String getDescription() {
		return "基金";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		FundRef ref = (FundRef) gameRefObject;

		if (ref.getMoneyType() != 1 && ref.getMoneyType() != 2) {
			error(ref, "货币类型非法!!!");
		}

		ItemPair[] ary = ref.getGiftArrays();
		for (ItemPair itemPair : ary) {
			if (itemPair.getNumber() < 0) {
				error(ref, "奖励数据非法!!!");
			}
		}

		if (ref.getBuyPrice() < 0) {
			error(ref, "购买价格数据非法!!!");
		}
	}

}
