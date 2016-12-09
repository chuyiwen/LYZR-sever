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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.ref.PlayerProfessionLevelData;
import sophia.mmorpg.player.ref.PlayerProfessionRef;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PlayerProfessionRefLoader extends
		AbstractGameRefObjectLoader<PlayerProfessionRef> {
	private static final Logger logger = Logger
			.getLogger(PlayerProfessionRefLoader.class);

	public PlayerProfessionRefLoader() {
		super(RefKey.character);
	}

	@Override
	protected PlayerProfessionRef create() {
		return new PlayerProfessionRef();
	}

	protected void fillNonPropertyDictionary(PlayerProfessionRef ref,
			JsonObject professionData) {

		// level data
		JsonElement levelData = professionData.get("levelData");
		if (levelData != null) {
			List<PlayerProfessionLevelData> playerProfessionLevel = new ArrayList<>();
			for (Entry<String, JsonElement> entry : levelData.getAsJsonObject()
					.entrySet()) {
				JsonObject oneLevelData = entry.getValue().getAsJsonObject();
				// level
				int level = oneLevelData.get("level").getAsInt();
				// property
				JsonObject propertyData = oneLevelData.get("property")
						.getAsJsonObject();
				PropertyDictionary pd = new PropertyDictionary();
				fillPropertyDictionary(pd, propertyData);
				playerProfessionLevel.add(new PlayerProfessionLevelData(ref
						.getId(), level, pd));
			}
			ref.setLevelDataList(playerProfessionLevel);
			ref.sortPlayerLevelDataList();
		}

		// PlayerProfessionFightSkillRef
	}

}
