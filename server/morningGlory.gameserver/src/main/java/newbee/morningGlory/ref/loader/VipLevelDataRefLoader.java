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

import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.mmorpg.vip.MGVipLevelDataRef;
import newbee.morningGlory.ref.RefKey;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class VipLevelDataRefLoader extends AbstractGameRefObjectLoader<MGVipLevelDataRef> {

	@Override
	protected MGVipLevelDataRef create() {
		return new MGVipLevelDataRef();
	}

	public VipLevelDataRefLoader() {
		super(RefKey.vip);
	}

	@Override
	protected void fillNonPropertyDictionary(MGVipLevelDataRef ref, JsonObject refData) {
		JsonElement weaponData = refData.getAsJsonObject().get("weaponData");
		if (weaponData.isJsonNull()) {
			return;
		}
		for (JsonElement itemElement : weaponData.getAsJsonArray()) {
			String itemRefId = itemElement.getAsJsonObject().get("itemRefId").getAsString();
			int number = itemElement.getAsJsonObject().get("number").getAsInt();
			boolean bindStatus = itemElement.getAsJsonObject().get("bindStatus").getAsBoolean();
			
			int level = itemElement.getAsJsonObject().get("level").getAsInt();
			byte profession = itemElement.getAsJsonObject().get("profession").getAsByte();
			
			ItemPair itemPair = new ItemPair(itemRefId, number, bindStatus);			
			
			if(ref.getWeaponMaps().containsKey(profession)){
				ref.getWeaponMaps().get(profession).put(level, itemPair);
			}else{
				Map<Integer,ItemPair> levelMaps = new HashMap<Integer,ItemPair>(1);
				levelMaps.put(level, itemPair);
				ref.getWeaponMaps().put(profession, levelMaps);
			}
		}
		
		String vipRewardLevel = MGPropertyAccesser.getVipRewardLevel(ref.getProperty());
		String[] vipStrings = vipRewardLevel.split(",");
		for (String level : vipStrings) {
			int vipLevel = Integer.parseInt(level);
			ref.getViplevels().add(vipLevel);
		}
	}

}
