package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.ref.DayOnlineRef;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DayOnlineRefLoader extends AbstractGameRefObjectLoader<DayOnlineRef> {
	private static final Logger logger = Logger.getLogger(DayOnlineRefLoader.class);

	@Override
	protected DayOnlineRef create() {
		return new DayOnlineRef();
	}

	public DayOnlineRefLoader() {
		super(RefKey.dailyOnline);
	}

	@Override
	protected void fillNonPropertyDictionary(DayOnlineRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load dayOnline info");
		}
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		if (refData.has("onlineReward")) {
			JsonObject onlineReward = refData.get("onlineReward").getAsJsonObject();
			if (onlineReward.has("dayOnlineReward")) {
				Iterator<JsonElement> iterator = onlineReward.get("dayOnlineReward").getAsJsonArray().iterator();
				while (iterator.hasNext()) {
					JsonElement element = iterator.next();
					String refId = element.getAsJsonObject().get("refId").getAsString();
					int number = element.getAsJsonObject().get("number").getAsInt();
					ItemPair itemPair = new ItemPair(refId, number ,false);
					itemPairs.add(itemPair);
				}
			}
		}
		ref.setItemPairs(itemPairs);
		super.fillNonPropertyDictionary(ref, refData);
	}
}
