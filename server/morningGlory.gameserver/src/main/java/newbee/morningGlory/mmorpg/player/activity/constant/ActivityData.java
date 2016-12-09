package newbee.morningGlory.mmorpg.player.activity.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ActivityData {
	private static Logger logger = Logger.getLogger(ActivityData.class);
	private static List<String> signRerIdList = new ArrayList<String>();

	private static Map<String, AwardData> rideAwardMaps = new HashMap<String, AwardData>();
	private static Map<String, AwardData> wingAwardMaps = new HashMap<String, AwardData>();
	private static Map<String, AwardData> talisAwardMaps = new HashMap<String, AwardData>();
	private static Map<String, AwardData> levelAwardMaps = new HashMap<String, AwardData>();
	private static Map<String, AwardData> onlineAwardMaps = new HashMap<String, AwardData>();

	private static Map<String, TalismanRewardTargetCondition> talisConditionMap = new HashMap<String, TalismanRewardTargetCondition>();

	public static Map<String, AwardData> getSignAwardMap() {
		sort();
		Map<String, AwardData> signAwardMap = new HashMap<String, AwardData>();
		for (String signRefId : signRerIdList) {
			logger.debug("signRefId:" + signRefId);
			AwardData awardData = new AwardData();
			awardData.setType(AwardTypeDefine.RewardType_Singin);
			awardData.setState(AwardState.Init);
			signAwardMap.put(signRefId, awardData);
		}
		return signAwardMap;
	}

	public static void sort() {
		Collections.sort(signRerIdList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
	}

	public static List<String> getSignRerIdList() {
		return signRerIdList;
	}

	public static Map<String, AwardData> getRideAwardMaps() {
		return rideAwardMaps;
	}

	public static Map<String, AwardData> getWingAwardMaps() {
		return wingAwardMaps;
	}

	public static Map<String, AwardData> getTalisAwardMaps() {
		return talisAwardMaps;
	}

	public static Map<String, TalismanRewardTargetCondition> getTalisConditionMap() {
		return talisConditionMap;
	}

	public static Map<String, AwardData> getLevelAwardMaps() {
		return levelAwardMaps;
	}

	public static Map<String, AwardData> getOnlineAwardMaps() {
		return onlineAwardMaps;
	}

	public static void setOnlineAwardMaps(Map<String, AwardData> onlineAwardMaps) {
		ActivityData.onlineAwardMaps = onlineAwardMaps;
	}

}
