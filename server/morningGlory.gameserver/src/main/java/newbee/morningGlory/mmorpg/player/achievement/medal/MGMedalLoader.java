package newbee.morningGlory.mmorpg.player.achievement.medal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGMedalLoader extends AbstractGameRefObjectLoader<MGMedalConfig> {
	private static Logger logger = Logger.getLogger(MGMedalConfig.class);

	public MGMedalLoader() {
		super(RefKey.medal);
	}

	@Override
	protected MGMedalConfig create() {
		return new MGMedalConfig();
	}

	@Override
	protected void fillNonPropertyDictionary(MGMedalConfig ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load medal refId:" + ref.getId());
		}
		String refId = refData.get("refId").getAsString();

		if ("medal".equals(refId)) {
			Map<String, MGMedalDataRef> medalDataRefMap = new HashMap<String, MGMedalDataRef>();
			JsonObject jsonConfigData = refData.getAsJsonObject("configData").getAsJsonObject();
			if (jsonConfigData != null) {
				for (Entry<String, JsonElement> entry : jsonConfigData.entrySet()) {
					String crtRefId = entry.getKey();
					JsonElement jsonElement = entry.getValue();
					JsonObject jsonObject = jsonElement.getAsJsonObject().get("property").getAsJsonObject();
					MGMedalDataRef dataRef = new MGMedalDataRef();
					fillPropertyDictionary(dataRef.getProperty(), jsonObject);
					medalDataRefMap.put(crtRefId, dataRef);
				}
			}
			ref.setId(MGMedalConfig.MEDALID);
			ref.setMedalDataRefMap(medalDataRefMap);
		}
	}

}
