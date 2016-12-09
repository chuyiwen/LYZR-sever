package newbee.morningGlory.ref.loader;

import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementRef;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonObject;

public class MGPlayerAchievementRefLoader extends AbstractGameRefObjectLoader<MGPlayerAchievementRef> {
	private static Logger logger = Logger.getLogger(MGPlayerAchievementRef.class);

	public MGPlayerAchievementRefLoader() {
		super(RefKey.achievement);
	}

	@Override
	protected MGPlayerAchievementRef create() {
		return new MGPlayerAchievementRef();
	}

	@Override
	protected void fillNonPropertyDictionary(MGPlayerAchievementRef ref, JsonObject refData) {
		if (refData.has("achieveReward") && refData.get("achieveReward") != null) {
			JsonObject rewardObject = refData.get("achieveReward").getAsJsonObject();
			if (rewardObject != null) {
				int achievementNum = rewardObject.get("achievement").getAsInt();
				int goldNum = rewardObject.get("gold").getAsInt();
				int bindedGoldNum = rewardObject.get("bindedGold").getAsInt();
				
				ItemPair achievement = new ItemPair("achievement", achievementNum, false);
				ItemPair gold = new ItemPair("gold", goldNum, false);
				ItemPair bindedGold = new ItemPair("bindedGold", bindedGoldNum, false);
				
				ref.getItemPairs().add(achievement);
				ref.getItemPairs().add(gold);
				ref.getItemPairs().add(bindedGold);
			}
			
		}

	}

}
