package newbee.morningGlory.ref.loader.activity;

import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionScrollRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import com.google.gson.JsonObject;

public class MonsterInvasionScrollLoader extends AbstractGameRefObjectLoader<MonsterInvasionScrollRef> {

	public MonsterInvasionScrollLoader() {
		super(RefKey.monsterInvasionScroll);
	}

	@Override
	protected MonsterInvasionScrollRef create() {
		return new MonsterInvasionScrollRef();
	}

	@Override
	protected void fillNonPropertyDictionary(MonsterInvasionScrollRef ref, JsonObject refData) {
		String range = refData.get("range").getAsString();
		int stage = refData.get("stage").getAsInt();
		JsonObject propertyData = refData.get("property").getAsJsonObject();
		fillPropertyDictionary(ref.getProperty(), propertyData);
		ref.setRange(range);
		ref.setStage(stage);
		super.fillNonPropertyDictionary(ref, refData);
	}

}
