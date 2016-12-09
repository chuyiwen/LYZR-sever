package newbee.morningGlory.mmorpg.player.activity.ref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class SignRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 1589698614916271473L;
	private List<ItemPair> itemPairs = new ArrayList<ItemPair>();

	private Map<Byte, List<ItemPair>> map = new HashMap<Byte, List<ItemPair>>();

	public SignRef() {

	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	public Map<Byte, List<ItemPair>> getMap() {
		return map;
	}

	public void setMap(Map<Byte, List<ItemPair>> map) {
		this.map = map;
	}

}
