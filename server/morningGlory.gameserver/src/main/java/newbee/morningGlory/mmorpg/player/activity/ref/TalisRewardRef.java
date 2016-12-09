package newbee.morningGlory.mmorpg.player.activity.ref;

import java.util.List;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class TalisRewardRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 6856370194984789469L;

	private List<ItemPair> itemPairs;

	public TalisRewardRef() {

	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

}
