package newbee.morningGlory.ref.loader.activity;

import java.util.Iterator;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.mining.ref.MGMiningRefConfigRef;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MiningSceneActivityRefLoader extends AbstractSceneActivityRefLoader {

	public MiningSceneActivityRefLoader(String jsonKey) {
		super(jsonKey);
	}

	@Override
	protected void fillNonPropertyDictionary(SceneActivityRef ref, JsonObject refData) {
		JsonElement activityData = refData.get("activityData").getAsJsonArray();
		Iterator<JsonElement> orderFieldData = activityData.getAsJsonArray().iterator();
		while (orderFieldData.hasNext()) {
			JsonObject elment = orderFieldData.next().getAsJsonObject();
			byte limitCount = elment.get("limitCount").getAsByte();
			int limitLevel = elment.get("limitLevel").getAsInt();
			MGMiningRefConfigRef miningRef = new MGMiningRefConfigRef();
			miningRef.setLimitCount(limitCount);
			miningRef.setLevel(limitLevel);
			ref.addComponentRef(miningRef);
		}
		super.fillNonPropertyDictionary(ref, refData);
	}
}
