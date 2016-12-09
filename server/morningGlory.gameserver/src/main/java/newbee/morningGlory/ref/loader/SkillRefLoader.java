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

import groovy.lang.Closure;

import java.util.Map;

import newbee.morningGlory.mmorpg.sprite.player.fightSkill.FightSkillRuntimeMgr;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.fightSkill.ref.PlayerFightSkillRefTree;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SkillRefLoader extends AbstractGameRefObjectLoader<SkillRef> {
	private static final Logger logger = Logger.getLogger(SkillRefLoader.class);

	public SkillRefLoader() {
		super(RefKey.skill);
	}

	@Override
	protected SkillRef create() {
		return new SkillRef();
	}

	@Override
	protected void fillNonPropertyDictionary(SkillRef ref, JsonObject refData) {
		super.fillNonPropertyDictionary(ref, refData);

		// closure
		String skillRefId = ref.getId();
		Closure<RuntimeResult> skillRuntime = FightSkillRuntimeMgr.getSkillRuntime(skillRefId);
		ref.setRuntime(skillRuntime);

		// skill level data
		JsonElement levelData = refData.get("skillLevel");
		if (!levelData.isJsonNull()) {
			for (Map.Entry<String, JsonElement> entry : levelData.getAsJsonObject().entrySet()) {
				JsonObject oneLevelData = entry.getValue().getAsJsonObject();
				// refId
				String refId = oneLevelData.get("refId").getAsString();

				// level
				int level = oneLevelData.get("level").getAsInt();

				// property
				JsonObject propertyData = oneLevelData.get("property").getAsJsonObject();
				PropertyDictionary pd = new PropertyDictionary();
				fillPropertyDictionary(pd, propertyData);
				
				// closure is temporarily not needed

				// runtime parameter
				JsonElement runtimeParameter = oneLevelData.get("effectData");
				if (!runtimeParameter.isJsonNull()) {
					PropertyDictionary parameter = new PropertyDictionary();
					fillPropertyDictionary(parameter, runtimeParameter.getAsJsonObject());

					ref.add(new SkillLevelRef(refId, pd, level, parameter));
					if (logger.isDebugEnabled()) {
						logger.debug("runtime parameter: " + oneLevelData.get("effectData"));
					}
				}

			}
		}

		// for PlayerFightSkillRefTree
		PlayerFightSkillRefTree.registerFightSkillRef(MGPropertyAccesser.getProfessionId(ref.getProperty()), ref);
	}

}
