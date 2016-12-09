package newbee.morningGlory.ref.loader.activity;

import java.util.Iterator;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.mutilExp.ref.MGMutilExpActivityRef;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MutilExpActivityRefLoader extends AbstractSceneActivityRefLoader{
	public MutilExpActivityRefLoader() {
		super(RefKey.multiExp);
	}

	@Override
	protected void fillNonPropertyDictionary(SceneActivityRef ref, JsonObject refData) {
		JsonElement activityData = refData.get("activityData").getAsJsonArray();
		Iterator<JsonElement> orderFieldData = activityData.getAsJsonArray().iterator();
		while (orderFieldData.hasNext()) {
			JsonObject elment = orderFieldData.next().getAsJsonObject();
			int  rate = elment.get("expMultiple").getAsByte();
			
			MGMutilExpActivityRef mutilExpRef = new MGMutilExpActivityRef();
			mutilExpRef.setRate(rate);
			ref.addComponentRef(mutilExpRef);
		}

		super.fillNonPropertyDictionary(ref, refData);
	}
}
