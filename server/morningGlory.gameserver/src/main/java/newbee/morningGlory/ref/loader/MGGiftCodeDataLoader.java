/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package newbee.morningGlory.ref.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeDataConfig;
import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeDataTypeRef;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGGiftCodeDataLoader extends AbstractGameRefObjectLoader<MGGiftCodeDataTypeRef> {
	private static final Logger logger = Logger.getLogger(MGGiftCodeDataLoader.class);

	@Override
	protected MGGiftCodeDataTypeRef create() {
		return new MGGiftCodeDataTypeRef();
	}

	public MGGiftCodeDataLoader() {
		super(RefKey.giftCode);
	}

	@Override
	protected void fillNonPropertyDictionary(MGGiftCodeDataTypeRef ref, JsonObject refData) {
		String expiredTime = MGPropertyAccesser.getExpiredTime(ref.getProperty());
		String openTime = MGPropertyAccesser.getOpenTime(ref.getProperty());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long end = format.parse(expiredTime).getTime() + 24 * 3600 * 1000;
			long now = System.currentTimeMillis();
			if (now > end) {
				return;
			}
			long start = format.parse(openTime).getTime();
			ref.setExpiredTime(end);
			ref.setOpenTime(start);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		byte codeType = MGPropertyAccesser.getItemType(ref.getProperty());

		JsonElement rewardItems = refData.get("rewardItems");

		List<ItemPair> rewards = new ArrayList<ItemPair>();
		for (JsonElement rewardElement : rewardItems.getAsJsonArray()) {
			String itemRefId = rewardElement.getAsJsonObject().get("itemRefId").getAsString();
			int number = rewardElement.getAsJsonObject().get("number").getAsInt();
			byte bindStatus = rewardElement.getAsJsonObject().get("bindStatus").getAsByte();
			ItemPair itemPair = new ItemPair(itemRefId, number, bindStatus);
			rewards.add(itemPair);
		}
		ref.setRewards(rewards);

		if (codeType == 1) {
			String keyCode = refData.get("keyCode").getAsString();
			ref.setDoubleGiftKeyCode(keyCode);
		} else {
			JsonObject configData = refData.getAsJsonObject().get("configData").getAsJsonObject();

			Set<String> giftCodeSingleSet = new HashSet<String>();
			if (configData != null) {
				for (Entry<String, JsonElement> entry : configData.entrySet()) {
					JsonElement jsonElement = entry.getValue();
					String keycode = jsonElement.getAsJsonObject().get("keycode").getAsString();
					giftCodeSingleSet.add(keycode);
				}
			}

			ref.setGiftCodeSingleSet(giftCodeSingleSet);
		}
		MGGiftCodeDataConfig.addGiftCodeRef(ref);
	}

}
