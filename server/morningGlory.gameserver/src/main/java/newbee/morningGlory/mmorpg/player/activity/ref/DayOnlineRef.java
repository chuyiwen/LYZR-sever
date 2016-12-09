package newbee.morningGlory.mmorpg.player.activity.ref;

import java.util.List;

import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class DayOnlineRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 4266843177696234482L;
	private List<ItemPair> itemPairs;

	public DayOnlineRef() {

	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	/**
	 * 下一个ref
	 */
	public DayOnlineRef getOnlineNextRef() {
		String nextRefId = MGPropertyAccesser.getOnlineNextRefId(getProperty());
		return (DayOnlineRef) GameRoot.getGameRefObjectManager().getManagedObject(nextRefId);
	}

	/**
	 * 本次计时时间
	 */
	public int getDayOnlineSeconds() {
		return MGPropertyAccesser.getOnlineSecond(getProperty());
	}

}
