package sophia.mmorpg.pluck.gameEvent;

import java.util.List;

import sophia.mmorpg.player.itemBag.ItemPair;

public class PluckRefresh_GE {
	public static final byte RefreshType_Begin = 1;
	public static final byte RefreshType_End = 2;
	
	private String pluckRefId;
	private List<ItemPair> itemPairs;
	private byte pluckType;
	private byte refreshType;// 开始进入刷新， 刷新完毕

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

	public byte getRefreshType() {
		return refreshType;
	}

	public void setRefreshType(byte refreshType) {
		this.refreshType = refreshType;
	}
	
}
