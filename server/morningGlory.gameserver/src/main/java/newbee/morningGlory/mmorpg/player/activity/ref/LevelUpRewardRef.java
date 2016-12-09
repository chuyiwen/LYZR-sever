package newbee.morningGlory.mmorpg.player.activity.ref;

import java.util.List;

import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class LevelUpRewardRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 4170416665612146997L;
	private List<ItemPair> itemPairs;

	public LevelUpRewardRef() {

	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	public LevelUpRewardRef getLevelUpRewardNextRef() {
		String nextRefId = MGPropertyAccesser.getLevelUpRewardNextRefId(getProperty());
		return (LevelUpRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(nextRefId);
	}

	public int getCurNeedLevel() {
		return MGPropertyAccesser.getLevel(getProperty());
	}

}
