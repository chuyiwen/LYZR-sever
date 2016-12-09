package newbee.morningGlory.mmorpg.player.activity.ladder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class ArenaRewardRef extends AbstractGameRefObjectBase {
	private static Logger logger = Logger.getLogger(ArenaRewardRef.class);
	private static final long serialVersionUID = 8452206546475626094L;

	public static final byte Type_Merit = 1;
	public static final byte Type_Gold = 2;

	private String arenaRank;
	private String meritReward;
	private String goldReward;

	private int minValue;
	private int maxValue;

	public ArenaRewardRef() {

	}

	public String getArenaRank() {
		return arenaRank;
	}

	public void setArenaRank(String arenaRank) {
		this.arenaRank = arenaRank;
	}

	public String getMeritReward() {
		return meritReward;
	}

	public void setMeritReward(String meritReward) {
		this.meritReward = meritReward;
	}

	public String getGoldReward() {
		return goldReward;
	}

	public void setGoldReward(String goldReward) {
		this.goldReward = goldReward;
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setValue() {
		if (arenaRank.matches("[0-9]+")) {
			minValue = Integer.parseInt(arenaRank);
			maxValue = minValue;
			return;
		}

		String newArenaRank = arenaRank.substring(1, arenaRank.length() - 1);// 4,50
		String[] numbers = newArenaRank.split(",");
		if (numbers.length != 2) {
			if (logger.isDebugEnabled()) {
				logger.error("奖励范围数据有错");
			}
		}

		minValue = Integer.parseInt(numbers[0]);
		if (numbers[1].startsWith("+")) {
			maxValue = Integer.MAX_VALUE;
		} else {
			maxValue = Integer.parseInt(numbers[1]);
		}
	}

	public int getMeirtOrGoldReward(int rank, byte type) {
		int reward = -1;
		String rewardString = type == Type_Merit ? meritReward : goldReward;

		if (rank > maxValue || rank < minValue) {
			if (logger.isDebugEnabled()) {
				logger.debug("rank值不在范围之内");
			}
			return reward;
		}
		// 2080-rank*20
		try {
			reward = Integer.parseInt(rewardString);
		} catch (NumberFormatException e) {
			String[] stringArray = rewardString.split("-|\\*");

			int param1 = -1;
			int param2 = -1;
			try {
				param1 = Integer.parseInt(stringArray[0]);
				param2 = Integer.parseInt(stringArray[2]);

			} catch (NumberFormatException e2) {
				if (logger.isDebugEnabled()) {
					logger.debug("功勋奖励数据错误");
				}
				return reward;
			}
			reward = param1 - param2 * rank;
		}
		return reward;
	}

	public List<ItemPair> getRewardItemPairs(int rewardRank){
		int goldNum = getMeirtOrGoldReward(rewardRank, ArenaRewardRef.Type_Gold);
		int meritNum = getMeirtOrGoldReward(rewardRank, ArenaRewardRef.Type_Merit);
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();

		ItemPair itemPair1 = new ItemPair("gold", goldNum, false);
		ItemPair itemPair2 = new ItemPair("merit", meritNum, false);
		itemPairs.add(itemPair1);
		itemPairs.add(itemPair2);
		return itemPairs;
	}
	
}
