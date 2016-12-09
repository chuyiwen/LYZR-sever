package newbee.morningGlory.ref.loader.activity;

import java.util.Iterator;
import java.util.Map;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionRef;
import newbee.morningGlory.ref.RefKey;
import org.apache.log4j.Logger;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MonsterInvasionLoader extends AbstractSceneActivityRefLoader {

	private static Logger logger = Logger.getLogger(MonsterInvasionLoader.class);

	public MonsterInvasionLoader() {
		super(RefKey.monsterInvasion);
	}

	@Override
	protected void fillNonPropertyDictionary(SceneActivityRef ref, JsonObject refData) {
		JsonObject activityData = refData.get("activityData").getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : activityData.entrySet()) {
			JsonObject allData = entry.getValue().getAsJsonObject();
			MonsterInvasionRef monsterIntrusionRef = new MonsterInvasionRef();
			Iterator<JsonElement> contantItem = allData.get("contantItem").getAsJsonArray().iterator();
			while (contantItem.hasNext()) {
				String itemRefId = contantItem.next().getAsString();
				monsterIntrusionRef.addItemRefIdList(itemRefId);
			}
			Iterator<JsonElement> contantScene = allData.get("contantScene").getAsJsonArray().iterator();
			while (contantScene.hasNext()) {
				String sceneRefId = contantScene.next().getAsString();
				monsterIntrusionRef.addSceneRefIdList(sceneRefId);
			}
			
			PropertyDictionary pd = monsterIntrusionRef.getProperty();
			String key = entry.getKey();
			monsterIntrusionRef.setId(key);
			JsonObject property = allData.get("property").getAsJsonObject();
			MGPropertyAccesser.setOrPutExpMultiple(pd, property.get("expMultiple").getAsFloat());
			MGPropertyAccesser.setOrPutLevel(pd, property.get("level").getAsInt());
			JsonObject time = refData.get("time").getAsJsonObject();
			String timeDuration = time.get("duration").getAsString();
			monsterIntrusionRef.setTime(timeDuration);

			ref.addComponentRef(monsterIntrusionRef);
		}
		super.fillNonPropertyDictionary(ref, refData);
	}
}
