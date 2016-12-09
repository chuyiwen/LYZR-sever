package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class LimitTimeRankRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -8929721252404843133L;

	private String rankInterval;

	private byte rankType;

	private List<ItemPair> itemPairs;

	// <profession, List<ItemPair>>
	private Map<Byte, List<ItemPair>> professionRewardMap = new HashMap<Byte, List<ItemPair>>();

	public LimitTimeRankRef() {

	}

	public String getRankInterval() {
		return rankInterval;
	}

	public void setRankInterval(String rankInterval) {
		this.rankInterval = rankInterval;
	}

	public byte getRankType() {
		return rankType;
	}

	public void setRankType(byte rankType) {
		this.rankType = rankType;
	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	public Map<Byte, List<ItemPair>> professionRewardMap() {
		return professionRewardMap;
	}

	public void professionRewardMap(Map<Byte, List<ItemPair>> map) {
		this.professionRewardMap = map;
	}

}
