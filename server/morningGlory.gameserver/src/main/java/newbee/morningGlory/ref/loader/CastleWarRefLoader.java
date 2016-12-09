package newbee.morningGlory.ref.loader;

import java.util.Map;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarInstanceTransfer;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarOutSceneTransfer;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarRef;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarSceneTransfer;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.activity.AbstractSceneActivityRefLoader;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CastleWarRefLoader extends AbstractSceneActivityRefLoader {

	public CastleWarRefLoader() {
		super(RefKey.castle);
	}

	@Override
	protected void fillNonPropertyDictionary(SceneActivityRef ref, JsonObject refData) {
		JsonObject activityData = refData.get("activityData").getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : activityData.entrySet()) {
			CastleWarRef castleWarRef = new CastleWarRef();
			PropertyDictionary pd = castleWarRef.getProperty();
			String castleWarId = entry.getKey();
			castleWarRef.setId(castleWarId);

			JsonObject allData = entry.getValue().getAsJsonObject();
			JsonObject castleData = allData.get("castleData").getAsJsonObject();
			if ((castleData.has("giftRefID")) && (!castleData.get("giftRefID").isJsonNull())) {
				String giftRefID = castleData.get("giftRefID").getAsString();
				castleWarRef.setGiftRefID(giftRefID);
			}
			JsonObject applyTimeData = castleData.get("applyTimeData").getAsJsonObject();
			if ((applyTimeData.has("startTime")) && (!applyTimeData.get("startTime").isJsonNull())) {
				String startApplyTime = applyTimeData.get("startTime").getAsString();
				castleWarRef.setStartApplyTime(startApplyTime);
			}
			if ((applyTimeData.has("endTime")) && (!applyTimeData.get("endTime").isJsonNull())) {
				String endApplyTime = applyTimeData.get("endTime").getAsString();
				castleWarRef.setEndApplyTime(endApplyTime);
			}

			int firstIntervalDays = allData.get("firstIntervalDays").getAsInt();
			int rangeIntervalDays = allData.get("rangeIntervalDays").getAsInt();
			String openAndEndTime = allData.get("openAndEndTime").getAsString();

			castleWarRef.setFirstIntervalDays(firstIntervalDays);
			castleWarRef.setRangeIntervalDays(rangeIntervalDays);
			castleWarRef.setOpenAndEndTime(openAndEndTime);

			JsonObject property = allData.get("property").getAsJsonObject();
			MGPropertyAccesser.setOrPutMaxStackNumber(pd, property.get("maxStackNumber").getAsInt());
			MGPropertyAccesser.setOrPutGold(pd, property.get("gold").getAsInt());
			MGPropertyAccesser.setOrPutItemId(pd, property.get("itemId").getAsString());
			MGPropertyAccesser.setOrPutTimesADay(pd, property.get("timesADay").getAsShort());
			MGPropertyAccesser.setOrPutMonsterRefId(pd, property.get("monsterRefId").getAsString());
			setTransferData(castleData, castleWarRef);
			ref.addComponentRef(castleWarRef);
		}
		super.fillNonPropertyDictionary(ref, refData);
	}

	public void setTransferData(JsonObject castleData, CastleWarRef ref) {
		if ((castleData.has("transfer")) && (!castleData.get("transfer").isJsonNull())) {
			JsonObject transferData = castleData.get("transfer").getAsJsonObject();
			CastleWarInstanceTransfer instanceTransfer = new CastleWarInstanceTransfer();
			JsonObject instance = transferData.get("instance").getAsJsonObject();
			String targetScene = instance.get("targetScene").getAsString();
			int tranferInId = instance.get("tranferInId").getAsInt();
			instanceTransfer.setTargetScene(targetScene);
			instanceTransfer.setTranferInId(tranferInId);
			ref.setCastleWarInstanceTransfer(instanceTransfer);

			CastleWarSceneTransfer warScene = new CastleWarSceneTransfer();
			JsonObject warMap = transferData.get("warMap").getAsJsonObject();
			String warMapScene = warMap.get("targetScene").getAsString();
			int warMaptranferId = warMap.get("tranferInId").getAsInt();
			warScene.setTargetScene(warMapScene);
			warScene.setTranferInId(warMaptranferId);
			ref.setCastleWarSceneTransfer(warScene);

			CastleWarOutSceneTransfer outScene = new CastleWarOutSceneTransfer();
			JsonObject kickOutData = transferData.get("kickOut").getAsJsonObject();
			String OutScene = kickOutData.get("targetScene").getAsString();
			int tranferOutId = kickOutData.get("tranferInId").getAsInt();
			outScene.setTargetScene(OutScene);
			outScene.setTranferInId(tranferOutId);
			ref.setCastleWarOutSceneTransfer(outScene);
		}
	}
}
