package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.constant.ActivityData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.ref.OnlineRef;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class OnlineRefLoader extends AbstractGameRefObjectLoader<OnlineRef> {
	private static final Logger logger = Logger.getLogger(OnlineRefLoader.class);

	@Override
	protected OnlineRef create() {
		return new OnlineRef();
	}

	public OnlineRefLoader() {
		super(RefKey.online);
	}

	@Override
	protected void fillNonPropertyDictionary(OnlineRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load online info");
		}
		
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		if (refData.has("onlineReward")) {
			JsonObject onlineReward = refData.get("onlineReward").getAsJsonObject();
			if (onlineReward.has("AccumulateonlineReward")) {
				Iterator<JsonElement> iterator = onlineReward.get("AccumulateonlineReward").getAsJsonArray().iterator();
				while (iterator.hasNext()) {
					JsonElement element = iterator.next();
					String refId = element.getAsJsonObject().get("refId").getAsString();
					int number = element.getAsJsonObject().get("number").getAsInt();
					byte bindStatus = element.getAsJsonObject().get("bindStatus").getAsByte();
					ItemPair itemPair = new ItemPair(refId, number, bindStatus);
					itemPairs.add(itemPair);
				}
			}
		}
		ref.setItemPairs(itemPairs);
		AwardData awardData = new AwardData(AwardTypeDefine.RewardType_CumulativeTime, AwardState.Init);
		ActivityData.getOnlineAwardMaps().put(ref.getId(), awardData);
		super.fillNonPropertyDictionary(ref, refData);
	}

}
