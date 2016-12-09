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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.pk.ref.MGInvasionPair;
import newbee.morningGlory.mmorpg.player.pk.ref.MGRefIdContain;
import newbee.morningGlory.mmorpg.player.pk.ref.MGScenePKDropRef;
import newbee.morningGlory.ref.RefKey;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGPkDropLoader extends AbstractGameRefObjectLoader<MGScenePKDropRef> {
	private static final Logger logger = Logger.getLogger(MGPkDropLoader.class);

	@Override
	protected MGScenePKDropRef create() {
		// TODO Auto-generated method stub
		return new MGScenePKDropRef();
	}

	public MGPkDropLoader() {
		super(RefKey.pkdrop);
	}

	@Override
	protected void fillNonPropertyDictionary(MGScenePKDropRef ref, JsonObject refData) {

		String refId = refData.get("refId").getAsString();
		JsonObject pd = refData.getAsJsonObject().get("property").getAsJsonObject();
		String sceneRefId = pd.get("sceneRefId").getAsString();
		String[] sceneIds = sceneRefId.split(",");
		for(String sceneId : sceneIds){
			MGRefIdContain.putPkDropRefId(sceneId, refId);
		}		
		String dropMethod = refData.getAsJsonObject().get("dropMethod").getAsString();
		ref.setDropMethod(dropMethod);
		if (StringUtils.equals(refId, "pkDrop_1")) {
			JsonObject dropConfigData = refData.getAsJsonObject().get("dropConfigData").getAsJsonObject();

			Map<String, List<MGInvasionPair>> invasionMaps = new HashMap<String, List<MGInvasionPair>>();
			if (dropConfigData != null) {
				for (Entry<String, JsonElement> entry : dropConfigData.entrySet()) {
					JsonElement element = entry.getValue();
					if (element.isJsonArray()) {
						for (JsonElement jsonElement : element.getAsJsonArray()) {
							String key = jsonElement.getAsJsonObject().get("key").getAsString();

							JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();
							int number = property.get("number").getAsInt();
							int probability = property.get("probability").getAsInt();
							String itemRefId = property.get("itemRefId").getAsString();
							byte bindStatus = property.get("bindStatus").getAsByte();
							MGInvasionPair pair = new MGInvasionPair(key, number, probability, itemRefId, bindStatus);
							if (invasionMaps.containsKey(key)) {
								invasionMaps.get(key).add(pair);
							} else {
								List<MGInvasionPair> pairs = new ArrayList<MGInvasionPair>();
								pairs.add(pair);
								invasionMaps.put(key, pairs);
							}
						}
					} else {
						String key = element.getAsJsonObject().get("key").getAsString();
						JsonObject property = element.getAsJsonObject().get("property").getAsJsonObject();
						int number = property.get("number").getAsByte();
						int probability = property.get("probability").getAsByte();
						String itemRefId = property.get("itemRefId").getAsString();
						byte bindStatus = property.get("bindStatus").getAsByte();
						MGInvasionPair pair = new MGInvasionPair(key, number, probability, itemRefId, bindStatus);
						List<MGInvasionPair> pairs = new ArrayList<MGInvasionPair>();
						pairs.add(pair);
						invasionMaps.put(key, pairs);
					}
				}
			}
			ref.setInvasionMap(invasionMaps);
		}

	}

}
