package newbee.morningGlory.ref.loader.activity;

import java.util.Iterator;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.payonPalace.ref.MGPayonPalaceActivityRef;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PayOnPalaceRefLoader extends AbstractSceneActivityRefLoader{
	public PayOnPalaceRefLoader() {
		super(RefKey.payOnPalace);
	}

	@Override
	protected void fillNonPropertyDictionary(SceneActivityRef ref, JsonObject refData) {
		JsonElement activityData = refData.get("activityData").getAsJsonArray();
		Iterator<JsonElement> orderFieldData = activityData.getAsJsonArray().iterator();
		while (orderFieldData.hasNext()) {
			MGPayonPalaceActivityRef PayonPalaceRef = new MGPayonPalaceActivityRef();
			JsonObject elment = orderFieldData.next().getAsJsonObject();
			int transfer = elment.get("transfer").getAsInt();
			
			PayonPalaceRef.setTransferIn(transfer);
			
			JsonElement items = elment.get("consumeitems");
			if (items.isJsonArray()) {
				Iterator<JsonElement> consumeitemsData = items.getAsJsonArray().iterator();
				while (consumeitemsData.hasNext()) {
					JsonObject elem = consumeitemsData.next().getAsJsonObject();
					String refId = elem.get("itemRefId").getAsString();
					int number = elem.get("number").getAsInt();
					PayonPalaceRef.getConsumptionItems().put(refId, number);				
				}
			}
			
			ref.addComponentRef(PayonPalaceRef);	
		}

		super.fillNonPropertyDictionary(ref, refData);
	}
}
