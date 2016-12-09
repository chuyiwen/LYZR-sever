package newbee.morningGlory.mmorpg.player.activity.ladder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.ladder.ArenaMgr;

import sophia.game.GameRoot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;

public class LadderRewardUtil {
	// <refId, arenaRank>
	public static Map<String, String> refMaps = new HashMap<String, String>();

	public static ArenaRewardRef getArenaRewardRef(String refId) {
		return (ArenaRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
	}

	public static String getArenaRewardRefIdByRank(int rank) {
		String refId = null;
		for (Entry<String, String> entry : refMaps.entrySet()) {
			if (isRankInInterval(rank, entry.getKey())) {
				refId = entry.getKey();
				break;
			}
		}
		return refId;
	}

	public static boolean isRankInInterval(int rank, String refId) {
		ArenaRewardRef ref = getArenaRewardRef(refId);
		int minValue = ref.getMinValue();
		int maxValue = ref.getMaxValue();
		if (minValue == maxValue) {
			return rank == minValue;
		} else if (maxValue == Integer.MAX_VALUE) {
			return rank >= minValue;
		} else {
			return rank >= minValue && rank <= maxValue;
		}

	}
	
	public static List<ItemPair> getRewardItemPairs(Player player, int rewardRank){
		String rewardRefId = getArenaRewardRefIdByRank(rewardRank);
		ArenaRewardRef ref = getArenaRewardRef(rewardRefId);
		return ref.getRewardItemPairs(rewardRank);
	}
	
	public static List<ItemPair> getRewardByMemberType(byte resultType) {
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		ItemPair gold = new ItemPair("gold", 0, false);
		ItemPair merit = new ItemPair("merit", 0, false);
		if (resultType == ArenaMgr.Athletics_Win) {
			gold.setNumber(100000);
			merit.setNumber(20);
		} else if (resultType == ArenaMgr.Athletics_Defeat) {
			gold.setNumber(50000);
			merit.setNumber(10);
		}
		itemPairs.add(gold);
		itemPairs.add(merit);
		return itemPairs;
	}
	
}
