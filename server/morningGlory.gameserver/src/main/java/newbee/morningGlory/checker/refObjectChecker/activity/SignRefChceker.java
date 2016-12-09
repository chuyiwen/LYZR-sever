package newbee.morningGlory.checker.refObjectChecker.activity;

import java.util.List;
import java.util.Map.Entry;

import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;
import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.ref.SignRef;

public class SignRefChceker extends BaseRefChecker<SignRef>{

	@Override
	public String getDescription() {
		return "签到";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		SignRef ref = (SignRef)gameRefObject;
		
		
		for (Entry<Byte, List<ItemPair>> entry : ref.getMap().entrySet()) {
			byte professionId = entry.getKey();
			if (professionId != 0 || professionId != 1 || professionId != 2 || professionId != 3) {
				error(ref, "签到奖励职业值非法!!!");
			}
			
			List<ItemPair> itemPairs = entry.getValue();
			for (ItemPair itemPair : itemPairs) {
				if (itemPair.getNumber() < 0) {
					error(ref, "签到奖励值非法!!!");
				}
			}
		}
		
		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "签到奖励值非法!!!");
			}
		}
	}

}
