package newbee.morningGlory.mmorpg.player.activity.ref;

import java.util.List;

import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class RideRewardRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 60507019474190929L;
	private List<ItemPair> itemPairs;

	public RideRewardRef() {

	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	public RideRewardRef getNextRideRewardRef() {
		String nextRefId = MGPropertyAccesser.getRideRewardNextRefId(getProperty());
		return (RideRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(nextRefId);
	}

	public int getRewardRideStage() {
		return MGPropertyAccesser.getStageLevel(getProperty());
	}
}
