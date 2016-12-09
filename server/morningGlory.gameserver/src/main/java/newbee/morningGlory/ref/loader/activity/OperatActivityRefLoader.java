package newbee.morningGlory.ref.loader.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.operatActivities.OperatActivityGroup;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.operatActivities.awardCond.CondType;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardContent;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardSendType;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class OperatActivityRefLoader extends AbstractGameRefObjectLoader<OperatActivityRef> {
	private static Logger logger = Logger.getLogger(OperatActivityRefLoader.class);
	private List<OperatActivityRef> refs = new ArrayList<OperatActivityRef>();
	@Override
	protected OperatActivityRef create() {
		return new OperatActivityRef();
	}

	public OperatActivityRefLoader() {	
		super(RefKey.operatActivity);
		OperatActivityMgr.getInstance();
		OperatActivityMgr.refMap.put(OperatActivityGroup.RechargeConsumptionActivity, refs);
	}

	@Override
	protected void fillNonPropertyDictionary(OperatActivityRef ref, JsonObject refData) {
		String refId = refData.getAsJsonObject().get("refId").getAsString();
		AwardContent awardContent = new AwardContent();
		awardContent.setDesc("");
		awardContent.setOtherData("");
		awardContent.setSendType(AwardSendType.PlayersReceiveManually);
		List<AwardItem> awardItems = new ArrayList<AwardItem>();
		JsonObject rechargeData = refData.getAsJsonObject().get("configData").getAsJsonObject();
		int operatActivityType = 0;
		if (rechargeData != null) {
			for (Entry<String, JsonElement> entry : rechargeData.entrySet()) {

				JsonElement jsonElement = entry.getValue();
				JsonObject property = jsonElement.getAsJsonObject().get("property").getAsJsonObject();

				String openTime = property.get("openTime").getAsString();
				String endTime = property.get("expiredTime").getAsString();
				operatActivityType = property.get("itemGroup").getAsInt();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

				try {
					ref.setOpenTime(format.parse(openTime));
					ref.setEndTime(format.parse(endTime));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String condValue = "";
				String otherData = "";
				String itemDesc = "";
				if(property.has("totalValue")){
					condValue = property.get("totalValue").getAsString();
				}
				if(property.has("operatOtherData")){
					otherData = property.get("operatOtherData").getAsString();
				}
				if(property.has("description")){
				    itemDesc = property.get("description").getAsString();
				}
				String operatActivityId = property.get("operatActivityId").getAsString();

				AwardItem awardItem = new AwardItem();
				awardItem.setCondType(CondType.ComplexData);
				awardItem.setCondValue(condValue);
				awardItem.setId(operatActivityId);
				awardItem.setItemOtherData(otherData);
				awardItem.setItemDesc(itemDesc);
				List<ItemPair> items = new ArrayList<ItemPair>();
				JsonElement rewardElement = jsonElement.getAsJsonObject().get("rewardItems");
				if(rewardElement.isJsonNull()){
					continue;
				}
				if(rewardElement.isJsonArray()){
					for (JsonElement itemElement : rewardElement.getAsJsonArray()) {
						String itemRefId = itemElement.getAsJsonObject().get("itemRefId").getAsString();
						int number = itemElement.getAsJsonObject().get("number").getAsInt();
						byte bindStatus = itemElement.getAsJsonObject().get("bindStatus").getAsByte();
						byte proffessionRefId = ItemPair.DEFAULT_PROFESSION;
						if(itemElement.getAsJsonObject().get("proffessionRefId") != null){
							proffessionRefId = itemElement.getAsJsonObject().get("proffessionRefId").getAsByte();
						}
						ItemPair itemPair = new ItemPair(itemRefId, number, bindStatus,proffessionRefId);
						items.add(itemPair);
					}
				}else{
					String itemRefId = rewardElement.getAsJsonObject().get("itemRefId").getAsString();
					int number = rewardElement.getAsJsonObject().get("number").getAsInt();
					byte bindStatus = rewardElement.getAsJsonObject().get("bindStatus").getAsByte();
					byte proffessionRefId = ItemPair.DEFAULT_PROFESSION;
					if(rewardElement.getAsJsonObject().get("proffessionRefId") != null){
						proffessionRefId = rewardElement.getAsJsonObject().get("proffessionRefId").getAsByte();
					}
					ItemPair itemPair = new ItemPair(itemRefId, number, bindStatus,proffessionRefId);
					items.add(itemPair);
				}
				awardItem.setItems(items);
				awardItems.add(awardItem);
			}
		}
		ref.setType(operatActivityType);
		awardContent.setAwardItems(awardItems);
		ref.setAwardContent(awardContent);
		OperatActivityMgr.refMap.get(OperatActivityGroup.RechargeConsumptionActivity).add(ref);
		
	}

}
