package newbee.morningGlory.ref.loader;

import java.util.LinkedHashMap;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.sectionQuest.MGSectionQuestRef;
import newbee.morningGlory.ref.RefKey;
import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonObject;

public class MGSectionQuestRefLoader extends AbstractGameRefObjectLoader<MGSectionQuestRef>{

	public MGSectionQuestRefLoader(){
		super(RefKey.sectionQuest);
	}
	
	@Override
	protected MGSectionQuestRef create() {
		return new MGSectionQuestRef();
	}
	
	@Override
	protected void fillNonPropertyDictionary(MGSectionQuestRef ref, JsonObject refDataRes) {
		JsonObject refData = refDataRes.get("questData").getAsJsonObject();
		if ((refData.has("conditionField"))&&(!refData.get("conditionField").isJsonNull())){
			JsonObject condition = refData.get("conditionField").getAsJsonObject();
			Map<String, Integer> acceptCondition = new LinkedHashMap<>();
			if ((condition.has("wing"))&&(!condition.get("wing").isJsonNull())){
				JsonObject wing = condition.get("wing").getAsJsonObject();
				int wingLevel = wing.get("wingLevel").getAsInt();
				acceptCondition.put("wingLevel", wingLevel);
				ref.setAcceptCondition(acceptCondition);
			}
			else if ((condition.has("mount"))&&(!condition.get("mount").isJsonNull())){
				JsonObject mount = condition.get("mount").getAsJsonObject();
				int stageLevel = mount.get("stageLevel").getAsInt();
				int startLevel = mount.get("startLevel").getAsInt();
				acceptCondition.put("stageLevel", stageLevel);
				acceptCondition.put("startLevel", startLevel);
				ref.setAcceptCondition(acceptCondition);
			}
			else if ((condition.has("peerage"))&&(!condition.get("peerage").isJsonNull())){
				JsonObject peerage = condition.get("peerage").getAsJsonObject();
				int peerageLevel = peerage.get("knight").getAsInt();
				acceptCondition.put("knight", peerageLevel);
				ref.setAcceptCondition(acceptCondition);
			}
		}
		
		if (refData.has("orderField") && (!refData.get("orderField").isJsonNull())){
			JsonObject orderField = refData.get("orderField").getAsJsonObject();
			Map<String, Integer> orderCondition = new LinkedHashMap<>();
			if ((orderField.has("wing"))&&(!orderField.get("wing").isJsonNull())){
				JsonObject wing = orderField.get("wing").getAsJsonObject();
				int wingLevel = wing.get("wingLevel").getAsInt();
				orderCondition.put("wingLevel", wingLevel);
				ref.setOrderCondition(orderCondition);
			}
			else if ((orderField.has("mount"))&&(!orderField.get("mount").isJsonNull())){
				JsonObject mount = orderField.get("mount").getAsJsonObject();
				int stageLevel = mount.get("stageLevel").getAsInt();
				int startLevel = mount.get("startLevel").getAsInt();
				orderCondition.put("stageLevel", stageLevel);
				orderCondition.put("startLevel", startLevel);
				ref.setOrderCondition(orderCondition);
			}
			else if ((orderField.has("peerage"))&&(!orderField.get("peerage").isJsonNull())){
				JsonObject peerage = orderField.get("peerage").getAsJsonObject();
				int peerageLevel = peerage.get("knight").getAsInt();
				orderCondition.put("knight", peerageLevel);
				ref.setOrderCondition(orderCondition);
			}
		}
		if (refData.has("rewardField") && !refData.get("rewardField").isJsonNull()){
			JsonObject rewardField = refData.get("rewardField").getAsJsonObject();
			String itemRefId = null;
			int itemCount = 0;
			if ((rewardField.has("itemRefId"))&&(!rewardField.get("itemRefId").isJsonNull())){
				itemRefId = rewardField.get("itemRefId").getAsString();
			}
			if ((rewardField.has("itemCount"))&&(!rewardField.get("itemCount").isJsonNull())){
				itemCount = rewardField.get("itemCount").getAsInt();
			}
			ItemPair item = new ItemPair(itemRefId, itemCount, false);
			ref.addRewardList(item);
		}
	}

}
