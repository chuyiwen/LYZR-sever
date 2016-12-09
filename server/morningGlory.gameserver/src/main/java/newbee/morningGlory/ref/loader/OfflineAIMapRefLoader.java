package newbee.morningGlory.ref.loader;

import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatarMgr;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.OfflineAIMapRef;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonObject;

public class OfflineAIMapRefLoader extends AbstractGameRefObjectLoader<OfflineAIMapRef> {

	public OfflineAIMapRefLoader(){
		super(RefKey.offlineAIMap);
	}
	@Override
	protected OfflineAIMapRef create() {
		return new OfflineAIMapRef();
	}

	
	@Override
	protected void fillNonPropertyDictionary(OfflineAIMapRef ref, JsonObject refData) {
		int minLevelId = refData.get("minLevelId").getAsInt();
		int maxLevelId = refData.get("maxLevelId").getAsInt();
		String mapId = refData.get("mapId").getAsString();
		ref.setMinLevelId(minLevelId);
		ref.setMaxLevelId(maxLevelId);
		ref.setMapId(mapId);
		PlayerAvatarMgr.putOfflineAIMapRef(ref);
		super.fillNonPropertyDictionary(ref, refData);
	}
	
	
}
