package newbee.morningGlory.ref.loader.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.constant.ActivityData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.constant.TalismanRewardTargetCondition;
import newbee.morningGlory.mmorpg.player.activity.ref.TalisRewardRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TalisRewardRefLoader extends AbstractGameRefObjectLoader<TalisRewardRef> {
	private static Logger logger = Logger.getLogger(TalisRewardRefLoader.class);

	@Override
	protected TalisRewardRef create() {
		return new TalisRewardRef();
	}

	public TalisRewardRefLoader() {
		super(RefKey.talisReward);
	}

	@Override
	protected void fillNonPropertyDictionary(TalisRewardRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load talisReward info...refId:" + ref.getId());
		}
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();

		JsonObject jsonObject = refData.get("talismanReward").getAsJsonObject();
		if (null != jsonObject) {
			Iterator<JsonElement> elements = jsonObject.get("talismanReward").getAsJsonArray().iterator();
			while (elements.hasNext()) {
				JsonElement element = elements.next();
				String refId = element.getAsJsonObject().get("refId").getAsString();
				int number = element.getAsJsonObject().get("number").getAsInt();
				ItemPair itemPair = new ItemPair(refId, number, false);
				itemPairs.add(itemPair);
			}
		}
		ref.setItemPairs(itemPairs);
		AwardData awardData = new AwardData(AwardTypeDefine.RewardType_Advanced, AwardState.Init);
		ActivityData.getTalisAwardMaps().put(ref.getId(), awardData);

		int level = MGPropertyAccesser.getTalisManLevel(ref.getProperty());
		int number = MGPropertyAccesser.getTalisManNumber(ref.getProperty());
		TalismanRewardTargetCondition condition = new TalismanRewardTargetCondition(level, number);
		ActivityData.getTalisConditionMap().put(ref.getId(), condition);
		super.fillNonPropertyDictionary(ref, refData);
	}
}
