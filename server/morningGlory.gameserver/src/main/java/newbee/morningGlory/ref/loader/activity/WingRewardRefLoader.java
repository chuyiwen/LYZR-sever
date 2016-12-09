package newbee.morningGlory.ref.loader.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.constant.ActivityData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.ref.WingRewardRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WingRewardRefLoader extends AbstractGameRefObjectLoader<WingRewardRef> {
	private static Logger logger = Logger.getLogger(WingRewardRefLoader.class);

	@Override
	protected WingRewardRef create() {
		return new WingRewardRef();
	}

	public WingRewardRefLoader() {
		super(RefKey.wingReward);
	}

	@Override
	protected void fillNonPropertyDictionary(WingRewardRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load wingReward info...refId:" + ref.getId());
		}
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();

		JsonObject jsonObject = refData.get("wingReward").getAsJsonObject();
		if (null != jsonObject) {
			Iterator<JsonElement> elements = jsonObject.get("wingReward").getAsJsonArray().iterator();
			while (elements.hasNext()) {
				JsonElement element = elements.next();
				String refId = element.getAsJsonObject().get("refId").getAsString();
				int number = element.getAsJsonObject().get("number").getAsInt();
				byte bindStatus = element.getAsJsonObject().get("bindStatus").getAsByte();
				ItemPair itemPair = new ItemPair(refId, number, bindStatus);
				itemPairs.add(itemPair);
			}
		}
		ref.setItemPairs(itemPairs);
		AwardData awardData = new AwardData(AwardTypeDefine.RewardType_Advanced, AwardState.Init);
		ActivityData.getWingAwardMaps().put(ref.getId(), awardData);
		super.fillNonPropertyDictionary(ref, refData);
	}

}
