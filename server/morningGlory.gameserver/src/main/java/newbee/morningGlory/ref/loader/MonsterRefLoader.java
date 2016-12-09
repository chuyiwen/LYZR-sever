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

import java.util.Map;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.monster.fightSkill.MonsterFightSkillTree;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MonsterRefLoader extends AbstractGameRefObjectLoader<MonsterRef> {

	private static final Logger logger = Logger.getLogger(MonsterRefLoader.class);

	public MonsterRefLoader() {
		super(RefKey.monster);
	}

	@Override
	protected MonsterRef create() {
		return new MonsterRef();
	}

	@Override
	protected void fillNonPropertyDictionary(MonsterRef ref, JsonObject refData) {
		// can move
		JsonObject configData = refData.get("configData").getAsJsonObject();
		byte canMove = configData.get("canMove").getAsByte();
		ref.setCanMove(canMove);

		// skill
		JsonObject skillJsonObject = refData.get("skill").getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : skillJsonObject.entrySet()) {
			String skillRefId = entry.getValue().getAsString();
			if (Strings.isNullOrEmpty(skillRefId)) {
				continue;
			}

			SkillRef skillRef = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(skillRefId);
			if (skillRef == null) {
				logger.error("Load Monster SkillRef Error can't find " + skillRefId);
				continue;
			}

			FightSkill fightSkill = MonsterFightSkillTree.getFightSkill(skillRefId);
			if (fightSkill == null) {
				fightSkill = new FightSkill(skillRefId, skillRef);
				MonsterFightSkillTree.addFightSkill(fightSkill);
			}

			ref.getFightSkillList().add(fightSkill);
		}

		if (ref.getFightSkillList().size() == 0 && MGPropertyAccesser.getKind(ref.getProperty()) != 6) {
			logger.error("Load Monster SkillRef Error no skills monsterRefId = " + ref.getId());
		}

		super.fillNonPropertyDictionary(ref, refData);
	}

}
