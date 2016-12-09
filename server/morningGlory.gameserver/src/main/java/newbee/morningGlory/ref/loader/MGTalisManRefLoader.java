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
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.talisman.MGTalismanRef;
import newbee.morningGlory.mmorpg.player.talisman.level.MGTalismanDataConfig;
import newbee.morningGlory.mmorpg.player.talisman.runtime.MGTalismanActiveClosure;
import newbee.morningGlory.ref.RefKey;
import sophia.foundation.property.PropertyDictionary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class MGTalisManRefLoader extends AbstractGameRefObjectLoader<MGTalismanDataConfig> {
	public MGTalisManRefLoader() {
		super(RefKey.talisman);
	}

	@Override
	protected MGTalismanDataConfig create() {
		return new MGTalismanDataConfig();
	}

	@Override
	protected void fillNonPropertyDictionary(MGTalismanDataConfig ref, JsonObject refData) {
	
		MGTalismanActiveClosure activeClosureGroovy = new MGTalismanActiveClosure();
		JsonObject talismanConfigData = refData.getAsJsonObject().get("configData").getAsJsonObject();
		Map<String, MGTalismanRef> talismanRefMap = new HashMap<String, MGTalismanRef>();
		if (talismanConfigData != null) {
			for (Entry<String, JsonElement> entry : talismanConfigData.entrySet()) {

				JsonElement jsonElement = entry.getValue();
				String id = jsonElement.getAsJsonObject().get("id").getAsString();
				JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();			
				JsonElement effectData = jsonElement.getAsJsonObject().get("effectData");
				String activeClosure =  jsonElement.getAsJsonObject().get("activeClosure").getAsString();
				String inactiveClosure =  jsonElement.getAsJsonObject().get("inactiveClosure").getAsString();
				JsonElement questData = jsonElement.getAsJsonObject().get("questData") ;
				String refId =  jsonElement.getAsJsonObject().get("talisRefId").getAsString();
				MGTalismanRef talismanRef = new MGTalismanRef();
				talismanRef.setId(refId);
				talismanRef.setActiveClosure(activeClosureGroovy.getActiveClosureMap().get(activeClosure));
				talismanRef.setUnactiveClosure(activeClosureGroovy.getInactiveClosureMap().get(inactiveClosure));
				fillPropertyDictionary(talismanRef.getProperty(), property);
				if(!effectData.isJsonNull())
					fillPropertyDictionary(talismanRef.getEffectData(), effectData.getAsJsonObject());
				
				if(!questData.isJsonNull()){
					PropertyDictionary pd = new PropertyDictionary();
					fillPropertyDictionary(pd, questData.getAsJsonObject());
					talismanRef.setQuestData(pd);
				}
				talismanRefMap.put(id, talismanRef);
			}
			
			ref.setId(MGTalismanDataConfig.MGTalisman_Id);
			ref.setTalismanLevelDataMap(talismanRefMap);
		}
	}
}
