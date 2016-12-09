package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.mmorpg.player.dailyQuest.ref.MGDailyQuestRef;
import newbee.morningGlory.mmorpg.player.dailyQuest.ref.MGDailyQuestRefMgr;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.foundation.property.ValueProperty;
import sophia.mmorpg.player.quest.ref.condition.AcceptQuestRefCondition;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionItem;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionType;
import sophia.mmorpg.player.quest.ref.condition.SingleQuestCondition;
import sophia.mmorpg.player.quest.ref.condition.VisibleQuestRefCondition;
import sophia.mmorpg.player.quest.ref.npc.QuestRefNpcData;
import sophia.mmorpg.player.quest.ref.npc.SingleQuestRefNpc;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrder;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.SingleQuestRefOrder;
import sophia.mmorpg.player.quest.ref.order.TalkQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.reward.ItemQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.PlayerClassesQuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.PropertyQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.SingleQuestRefReward;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class MGDailyQuestRefLoader extends AbstractGameRefObjectLoader<MGDailyQuestRef> {
	private Logger logger = Logger.getLogger(MGDailyQuestRefLoader.class.getName());

	public MGDailyQuestRefLoader() {
		super(RefKey.dailyQuest);
	}

	@Override
	protected MGDailyQuestRef create() {
		return new MGDailyQuestRef();
	}

	@Override
	protected void fillNonPropertyDictionary(MGDailyQuestRef ref, JsonObject refDataRes) {
		String questType = refDataRes.get("refId").getAsString();
		ref.setQuestType(questType);

		JsonObject propertyData = refDataRes.get("property").getAsJsonObject();
		if ((propertyData.has("nextQuestId")) && (!propertyData.get("nextQuestId").isJsonNull())) {
			ref.setNextQuestId(propertyData.get("nextQuestId").getAsString());
		}

		JsonObject refData = refDataRes.get("questData").getAsJsonObject();

		// 存放conditionField
		if ((refData.has("conditionField")) && (!refData.get("conditionField").isJsonNull())) {
			SingleQuestCondition singleCondition = setQuestCondition(refData);
			ref.setConditionSet(singleCondition);
		}

		// 存放orderField
		List<QuestRefOrder> orderList = new ArrayList<QuestRefOrder>();
		ref.setOrderList(orderList);

		if (refData.has("orderField") && (!refData.get("orderField").isJsonNull())) {
			JsonElement orderField = refData.get("orderField");
			if (orderField.isJsonArray()) {
				SingleQuestRefOrder singleOrder = new SingleQuestRefOrder();
				Set<QuestRefOrderItem> orderItemSet = new LinkedHashSet<QuestRefOrderItem>();
				singleOrder.setQuestRefOrder(orderItemSet);
				orderList.add(singleOrder);

				Iterator<JsonElement> orderFieldData = orderField.getAsJsonArray().iterator();
				while (orderFieldData.hasNext()) {
					JsonObject elem = orderFieldData.next().getAsJsonObject();
					int orderType = elem.get("orderType").getAsInt();
					orderItemSet = add2OrderItemSet(orderType, orderItemSet, elem);
				}
			}
		}

		// 存放overOrderField
		if (refData.has("overOrderField") && (!refData.get("overOrderField").isJsonNull())) {
			JsonElement orderField = refData.get("overOrderField");
			if (orderField.isJsonArray()) {
				SingleQuestRefOrder singleOrder = new SingleQuestRefOrder();
				Set<QuestRefOrderItem> orderItemSet = new LinkedHashSet<QuestRefOrderItem>();
				singleOrder.setQuestRefOrder(orderItemSet);
				orderList.add(singleOrder);

				Iterator<JsonElement> orderFieldData = orderField.getAsJsonArray().iterator();
				while (orderFieldData.hasNext()) {
					JsonObject elem = orderFieldData.next().getAsJsonObject();
					int orderType = elem.get("orderType").getAsInt();
					orderItemSet = add2OrderItemSet(orderType, orderItemSet, elem);
				}
			}
		}

		// 存放rewardField
		if (refData.has("rewardField") && refData.get("rewardField") != null) {
			JsonObject rewardField = refData.get("rewardField").getAsJsonObject();
			if (rewardField.isJsonObject()) {
				setRewardByType(rewardField, ref, 1);
			}
		}

		// 存放lastRewardField
		if (refData.has("lastRewardField") && refData.get("lastRewardField") != null) {
			JsonObject rewardField = refData.get("lastRewardField").getAsJsonObject();
			if (rewardField.isJsonObject()) {
				setRewardByType(rewardField, ref, 2);
			}
		}

		// 存放overOrderRewarField(超出推荐环数之后的日常任务奖励)
		if (refData.has("overOrderRewarField") && refData.get("overOrderRewarField") != null) {
			JsonObject rewardField = refData.get("overOrderRewarField").getAsJsonObject();
			if (rewardField.isJsonObject()) {
				setRewardByType(rewardField, ref, 3);
			}
		}

		// 存放npcField
		if (refData.has("npcField") && (!refData.get("npcField").isJsonNull())) {
			SingleQuestRefNpc singleQuestNpc = getQuestNpc(refData);
			ref.setNpc(singleQuestNpc);
		}
		MGDailyQuestRefMgr.dailyQuestRefList.add(ref);
	}

	// 1=战士男 2=战士女 3=法师男 4=法师女 5=道士男 6=道士女
	private PlayerClassesQuestRefReward setGenderAndProffession(PlayerClassesQuestRefReward classReward, String proffessionRefId, Set<QuestRefRewardItem> itemRewardSet) {
		if (proffessionRefId.equals("1")) {
			classReward.addProfessionQuestRefRewardItemSet("1", (byte) 1, itemRewardSet);
		} else if (proffessionRefId.equals("2")) {
			classReward.addProfessionQuestRefRewardItemSet("1", (byte) 2, itemRewardSet);
		} else if (proffessionRefId.equals("3")) {
			classReward.addProfessionQuestRefRewardItemSet("2", (byte) 1, itemRewardSet);
		} else if (proffessionRefId.equals("4")) {
			classReward.addProfessionQuestRefRewardItemSet("2", (byte) 2, itemRewardSet);
		} else if (proffessionRefId.equals("5")) {
			classReward.addProfessionQuestRefRewardItemSet("3", (byte) 1, itemRewardSet);
		} else if (proffessionRefId.equals("6")) {
			classReward.addProfessionQuestRefRewardItemSet("3", (byte) 2, itemRewardSet);
		}
		return classReward;
	}

	private SingleQuestRefNpc getQuestNpc(JsonObject refData) {
		SingleQuestRefNpc singleQuestNpc = new SingleQuestRefNpc();
		JsonObject npcField = refData.get("npcField").getAsJsonObject();
		QuestRefNpcData questNpc = new QuestRefNpcData();
		if (npcField.isJsonObject()) {
			if (npcField.has("acceptNpc") && (!npcField.get("acceptNpc").isJsonNull())) {
				JsonObject acceptNpc = npcField.get("acceptNpc").getAsJsonObject();
				if (acceptNpc.has("sceneRefId")) {
					String accept_sceneRefId = acceptNpc.get("sceneRefId").getAsString();
					questNpc.setAcceptedNpcSceneRefId(accept_sceneRefId);
				}
				if (acceptNpc.has("npcRefId")) {
					String accept_npcRefId = acceptNpc.get("npcRefId").getAsString();
					questNpc.setAcceptedNpcRefId(accept_npcRefId);
				}
				if (acceptNpc.has("npcRefId")) {
					String accept_talkContent = acceptNpc.get("talkContent").getAsString();
					questNpc.setAcceptedTalkContext(accept_talkContent);
				}
			}

			if (npcField.has("submitNpc") && (!npcField.get("submitNpc").isJsonNull())) {
				JsonObject submitNpc = npcField.get("submitNpc").getAsJsonObject();
				if (submitNpc.has("sceneRefId")) {
					String submit_sceneRefId = submitNpc.get("sceneRefId").getAsString();
					questNpc.setTakeRewardNpcSceneRefId(submit_sceneRefId);
				}
				if (submitNpc.has("npcRefId")) {
					String submit_npcRefId = submitNpc.get("npcRefId").getAsString();
					questNpc.setTakeRewardNpcRefId(submit_npcRefId);
				}
				if (submitNpc.has("talkContent")) {
					String submit_talkContent = submitNpc.get("talkContent").getAsString();
					questNpc.setTakeRewardTalkContext(submit_talkContent);
				}
			}
		}
		singleQuestNpc.setQuestRefNpcData(questNpc);
		return singleQuestNpc;
	}

	public List<ValueProperty<?>> getPropertyList(JsonObject propertyReward) {
		List<ValueProperty<?>> propertyList = new ArrayList<>();
		for (Entry<String, JsonElement> entry : propertyReward.entrySet()) {
			if (entry.getKey().equals("exp")) {
				int exp = propertyReward.get("exp").getAsInt();
				propertyList.add(new ValueProperty<Integer>(MGPropertySymbolDefines.Exp_Id, exp));
			}
			if (entry.getKey().equals("gold")) {
				int unbindedCopper = propertyReward.get("gold").getAsInt();
				propertyList.add(new ValueProperty<Integer>(MGPropertySymbolDefines.Gold_Id, unbindedCopper));
			}
			if (entry.getKey().equals("unbindedGold")) {
				int unbindedGold = propertyReward.get("unbindedGold").getAsInt();
				propertyList.add(new ValueProperty<Integer>(MGPropertySymbolDefines.UnbindedGold_Id, unbindedGold));
			}
			if (entry.getKey().equals("bindedCopper")) {
				int bindedCopper = propertyReward.get("bindedCopper").getAsInt();
				propertyList.add(new ValueProperty<Integer>(MGPropertySymbolDefines.BindedCopper_Id, bindedCopper));
			}
			if (entry.getKey().equals("achievement")) {
				int achievement = propertyReward.get("achievement").getAsInt();
				propertyList.add(new ValueProperty<Integer>(MGPropertySymbolDefines.Achievement_Id, achievement));
			}
			if (entry.getKey().equals("merit")) {
				int merit = propertyReward.get("merit").getAsInt();
				propertyList.add(new ValueProperty<Integer>(MGPropertySymbolDefines.Merit_Id, merit));
			}
		}
		return propertyList;
	}

	public ItemQuestRefRewardItem getRewardItem(JsonObject temp, int relatedType) {
		ItemQuestRefRewardItem item = new ItemQuestRefRewardItem();
		if (temp.has("itemRefId")) {
			String itemRefId = temp.get("itemRefId").getAsString();
			item.setItemRefId(itemRefId);
		}
		if (temp.has("itemCount")) {
			int itemCount = temp.get("itemCount").getAsInt();
			item.setNumber(itemCount);
		}
		if (temp.has("bindStatus")) {
			int isBinded = temp.get("bindStatus").getAsInt();
			boolean binded = false;
			if (isBinded != 0) {
				binded = true;
			}
			item.setBinded(binded);
		}
		item.setRelateType(relatedType);
		return item;
	}

	private SingleQuestCondition setQuestCondition(JsonObject refData) {
		SingleQuestCondition singleCondition = new SingleQuestCondition();
		JsonArray conditionField = refData.get("conditionField").getAsJsonArray();
		Set<QuestRefConditionItem> conditionItemSet = singleCondition.getConditionItems(null);
		for (JsonElement entry : conditionField) {
			JsonObject elem = entry.getAsJsonObject();
			byte conditionType = elem.get("conditionType").getAsByte();
			singleCondition.setConditionType(conditionType);
			if (conditionType == QuestRefConditionType.Visiable_Condition_Type) {
				VisibleQuestRefCondition visibleCondition = new VisibleQuestRefCondition();
				int visibleLevel = elem.get("visibleLevel").getAsInt();
				visibleCondition.setVisibleLevel(visibleLevel);
				singleCondition.addConditionItems(visibleCondition);
				conditionItemSet.add(visibleCondition);
			} else if (conditionType == QuestRefConditionType.Accept_Condition_Type) {
				AcceptQuestRefCondition acceptCondition = new AcceptQuestRefCondition();
				int acceptLevel = elem.get("acceptLevel").getAsInt();
				acceptCondition.setAcceptLevel(acceptLevel);
				singleCondition.addConditionItems(acceptCondition);
				conditionItemSet.add(acceptCondition);
			}
		}
		singleCondition.setConditionItems(conditionItemSet);
		return singleCondition;
	}

	private Set<QuestRefOrderItem> add2OrderItemSet(int orderType, Set<QuestRefOrderItem> orderItemSet, JsonObject elem) {
		if (orderType == 1) {
			KillMonsterQuestRefOrderItem killQuest = new KillMonsterQuestRefOrderItem();
			String sceneRefId = elem.get("sceneRefId").getAsString();
			String monsterRefId = elem.get("monsterRefId").getAsString();
			int killCount = elem.get("killCount").getAsInt();
			killQuest.setSceneId(sceneRefId);
			killQuest.setMonsterRefId(monsterRefId);
			killQuest.setNumber(killCount);
			killQuest.setIndex((byte) 0);
			orderItemSet.add(killQuest);
		} else if (orderType == 2) {
			LootItemQuestRefOrderItem lootQuest = new LootItemQuestRefOrderItem();
			String sceneRefId = elem.get("sceneRefId").getAsString();
			String monsterRefId = elem.get("monsterRefId").getAsString();
			String itemRefId = elem.get("itemRefId").getAsString();
			int itemCount = elem.get("itemCount").getAsInt();
			lootQuest.setSceneRefId(sceneRefId);
			lootQuest.setMonsterRefId(monsterRefId);
			lootQuest.setItemRefId(itemRefId);
			lootQuest.setNumber(itemCount);
			lootQuest.setIndex((byte) 0);
			orderItemSet.add(lootQuest);
		} else if (orderType == 3) {
			TalkQuestRefOrderItem talkQuest = new TalkQuestRefOrderItem();
			String sceneRefId = elem.get("sceneRefId").getAsString();
			String npcRefId = elem.get("npcRefId").getAsString();
			String talkContent = elem.get("talkContent").getAsString();
			talkQuest.setSceneRefId(sceneRefId);
			talkQuest.setNpcRefId(npcRefId);
			talkQuest.setTalkContent(talkContent);
			talkQuest.setIndex((byte) 0);
			orderItemSet.add(talkQuest);
		} else if (orderType == 4) {
			CollectQuestRefOrderItem collectQuest = new CollectQuestRefOrderItem();
			String sceneRefId = elem.get("sceneRefId").getAsString();
			String npcRefId = elem.get("npcRefId").getAsString();
			String itemRefId = elem.get("itemRefId").getAsString();
			short itemCount = elem.get("itemCount").getAsShort();
			collectQuest.setSceneRefId(sceneRefId);
			collectQuest.setCollectTargetObjectId(npcRefId);
			collectQuest.setCollectItemId(itemRefId);
			collectQuest.setNumber(itemCount);
			collectQuest.setIndex((byte) 0);
			orderItemSet.add(collectQuest);
		}
		return orderItemSet;
	}

	private void setRewardByType(JsonObject rewardField, MGDailyQuestRef ref, int type) {
		// 属性奖励部分
		PropertyQuestRefRewardItem propertyRewardItem = new PropertyQuestRefRewardItem();
		if (rewardField.has("propertyReward") && (!rewardField.get("propertyReward").isJsonNull())) {
			JsonObject propertyReward = rewardField.get("propertyReward").getAsJsonObject();
			List<ValueProperty<?>> propertyList = getPropertyList(propertyReward);
			propertyRewardItem.setPropertyRewardList(propertyList);
		}

		if (rewardField.has("itemReward") && (!rewardField.get("itemReward").isJsonNull())) {
			Set<QuestRefRewardItem> itemRewardSet = new HashSet<>();
			JsonObject itemReward = rewardField.get("itemReward").getAsJsonObject();
			int relatedType = itemReward.get("relatedType").getAsInt();
			// 不区分职业
			if (relatedType == 0) {
				SingleQuestRefReward singleReward = new SingleQuestRefReward();
				Iterator<JsonElement> itemList = itemReward.get("itemList").getAsJsonArray().iterator();
				while (itemList.hasNext()) {
					JsonObject elem = itemList.next().getAsJsonObject();
					itemRewardSet.add(getRewardItem(elem, relatedType));
				}
				itemRewardSet.add(propertyRewardItem);
				singleReward.setRewardItemSet(itemRewardSet);
				setReward(type, ref, singleReward);
			}

			// 区分职业
			else if (relatedType == 1) {
				PlayerClassesQuestRefReward classReward = new PlayerClassesQuestRefReward();
				Iterator<JsonElement> professionList = itemReward.get("professionList").getAsJsonArray().iterator();
				while (professionList.hasNext()) {
					JsonObject elem = professionList.next().getAsJsonObject();
					String proffessionRefId = elem.get("proffessionRefId").getAsString();
					Iterator<JsonElement> itemList = elem.get("itemList").getAsJsonArray().iterator();
					while (itemList.hasNext()) {
						Set<QuestRefRewardItem> itemRewardSet2 = new HashSet<>();
						JsonObject temp = itemList.next().getAsJsonObject();
						itemRewardSet2.add(getRewardItem(temp, relatedType));
						itemRewardSet2.add(propertyRewardItem);
						classReward = setGenderAndProffession(classReward, proffessionRefId, itemRewardSet2);
					}
				}
				setReward(type, ref, classReward);
			}
		} else {
			SingleQuestRefReward singleReward = new SingleQuestRefReward();
			Set<QuestRefRewardItem> rewardItemSet = new HashSet<QuestRefRewardItem>();
			rewardItemSet.add(propertyRewardItem);
			singleReward.setRewardItemSet(rewardItemSet);
			setReward(type, ref, singleReward);
		}
	}

	private void setReward(int type, MGDailyQuestRef ref, QuestRefReward reward) {
		if (type == 1) {
			ref.setReward(reward);
		} else if (type == 2) {
			ref.setFinalReward(reward);
		} else if (type == 3) {
			ref.setOverOrderReward(reward);
		}
	}
}
