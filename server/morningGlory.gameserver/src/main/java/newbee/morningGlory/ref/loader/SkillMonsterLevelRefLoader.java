package newbee.morningGlory.ref.loader;

import newbee.morningGlory.mmorpg.monster.ref.SkillMonsterLevelRef;
import newbee.morningGlory.mmorpg.player.summons.SummonMonsterExpComponent;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonObject;

public class SkillMonsterLevelRefLoader extends AbstractGameRefObjectLoader<SkillMonsterLevelRef> {

	public SkillMonsterLevelRefLoader() {
		super(RefKey.monsterLevel);
	}

	@Override
	protected SkillMonsterLevelRef create() {
		return new SkillMonsterLevelRef();
	}

	@Override
	protected void fillNonPropertyDictionary(SkillMonsterLevelRef ref, JsonObject refData) {
		SummonMonsterExpComponent.addSkillMonsterLevelRef(ref);
		String refId = refData.get("refId").getAsString();
		ref.setId(refId);
		JsonObject monsterData = refData.get("monsterData").getAsJsonObject();
		if ((monsterData.has("level")) && (!monsterData.get("level").isJsonNull())) {
			int monsterLevel = monsterData.get("level").getAsInt();
			ref.setLevel(monsterLevel);
		}
		if ((monsterData.has("monsterExp")) && (!monsterData.get("monsterExp").isJsonNull())) {
			int monsterExpNeed = monsterData.get("monsterExp").getAsInt();
			ref.setExpNeed(monsterExpNeed);
		}
		if ((monsterData.has("nestMonsterRefId")) && (!monsterData.get("nestMonsterRefId").isJsonNull())) {
			String nextMonsterRefId = monsterData.get("nestMonsterRefId").getAsString();
			ref.setNextMonsterRefId(nextMonsterRefId);
		}
	}

}
