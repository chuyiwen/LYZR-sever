package sophia.mmorpg.pluck.gameEvent;

import java.util.List;

import sophia.mmorpg.player.itemBag.ItemPair;

public class PluckSuccess_GE {
	private String pluckRefId;// npcRefId
	private List<ItemPair> itemPairs;
	private byte pluckType;

	public String getPluckRefId() {
		return pluckRefId;
	}

	public void setPluckRefId(String pluckRefId) {
		this.pluckRefId = pluckRefId;
	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	public byte getPluckType() {
		return pluckType;
	}

	public void setPluckType(byte pluckType) {
		this.pluckType = pluckType;
	}

}
