package newbee.morningGlory.mmorpg.player.activity.ref;

import java.util.List;

import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class WingRewardRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -225012330275351684L;
	private List<ItemPair> itemPairs;

	public WingRewardRef() {

	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	public int getWingLevel() {
		return MGPropertyAccesser.getWingLevel(getProperty());
	}

	public WingRewardRef getNextWingRewardRef() {
		String nextWingRewardRefId = MGPropertyAccesser.getWingRewardNextRefId(getProperty());
		return (WingRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(nextWingRewardRefId);
	}
}
