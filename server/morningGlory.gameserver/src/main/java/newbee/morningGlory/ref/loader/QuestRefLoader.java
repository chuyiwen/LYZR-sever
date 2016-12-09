/**
 * 
 */
package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.foundation.property.ValueProperty;
import sophia.mmorpg.player.quest.ref.QuestRef;
import sophia.mmorpg.player.quest.ref.condition.AcceptQuestRefCondition;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionItem;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionType;
import sophia.mmorpg.player.quest.ref.condition.SingleQuestCondition;
import sophia.mmorpg.player.quest.ref.condition.VisibleQuestRefCondition;
import sophia.mmorpg.player.quest.ref.npc.QuestRefNpcData;
import sophia.mmorpg.player.quest.ref.npc.SingleQuestRefNpc;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.SingleQuestRefOrder;
import sophia.mmorpg.player.quest.ref.order.TalkQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.reward.ItemQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.PlayerClassesQuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.PropertyQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.SingleQuestRefReward;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class QuestRefLoader extends AbstractGameRefObjectLoader<QuestRef> {

	private static final Logger logger = Logger.getLogger(QuestRefLoader.class);

	public QuestRefLoader() {
		super(RefKey.questData);
	}

	@Override
	protected QuestRef create() {
		return new QuestRef();
	}

	@Override
	protected void fillNonPropertyDictionary(QuestRef ref, JsonObject refDataRes) {
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
			ref.addCondition(singleCondition);
		}

		// 存放orderField
		if (refData.has("orderField") && (!refData.get("orderField").isJsonNull())) {
			JsonElement orderField = refData.get("orderField");
			if (orderField.isJsonArray()) {
				SingleQuestRefOrder singleOrder = setQuestOrder(orderField);
				ref.setOrder(singleOrder);
			}
		} else {
			SingleQuestRefOrder singleOrder = new SingleQuestRefOrder();
			ref.setOrder(singleOrder);
		}

		// 存放npcField
		if (refData.has("npcField") && (!refData.get("npcField").isJsonNull())) {
			SingleQuestRefNpc singleQuestNpc = getQuestNpc(refData);
			ref.setNpc(singleQuestNpc);
		}

		// 存放rewardField
		if (refData.has("rewardField") && refData.get("rewardField") != null) {
			JsonObject rewardField = refData.get("rewardField").getAsJsonObject();
			if (rewardField.isJsonObject()) {
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
						ref.setReward(singleReward);
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
						ref.setReward(classReward);
					}
				} else {
					SingleQuestRefReward singleReward = new SingleQuestRefReward();
					Set<QuestRefRewardItem> rewardItemSet = new HashSet<QuestRefRewardItem>();
					rewardItemSet.add(propertyRewardItem);
					singleReward.setRewardItemSet(rewardItemSet);
					ref.setReward(singleReward);
				}
			}
		}
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

	private SingleQuestRefOrder setQuestOrder(JsonElement orderField) {
		SingleQuestRefOrder singleOrder = new SingleQuestRefOrder();
		Iterator<JsonElement> orderFieldData = orderField.getAsJsonArray().iterator();
		Set<QuestRefOrderItem> orderItemSet = new HashSet<QuestRefOrderItem>();
		while (orderFieldData.hasNext()) {
			JsonObject elem = orderFieldData.next().getAsJsonObject();
			int orderType = elem.get("orderType").getAsInt();
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
				String itemRefId = elem.get("itemRefId").getAsString();
				int itemCount = elem.get("itemCount").getAsInt();
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
			} else if (orderType == 9) {
				ChineseModeStringQuestRefOrderItem chineseMode = new ChineseModeStringQuestRefOrderItem();
				short orderEventId = elem.get("orderEventId").getAsShort();
				chineseMode.setOrderEventId(orderEventId);
				switch (orderEventId) {
				case QuestChineseOrderDefines.GameInstanceEnter:
					String sceneRefId = elem.get("sceneRefId").getAsString();
					String gameInstanceRefId = elem.get("gameInstanceRefId").getAsString();
					chineseMode.setChineseModeValue(sceneRefId);
					chineseMode.setChineseModeTarget(gameInstanceRefId);
					break;
				case QuestChineseOrderDefines.PeerageLevelUp:
					int peerageLevel = elem.get("peerageLevel").getAsInt();
					chineseMode.setNumber(peerageLevel);
					break;
				case QuestChineseOrderDefines.MountLevelUp:
					int mountStageLevel = elem.get("mountStageOrderLevel").getAsInt();
					chineseMode.setNumber(mountStageLevel);
					if (elem.has("mountStartOrderLevel") && (!elem.get("mountStartOrderLevel").isJsonNull())) {
						int mountStartLevel = elem.get("mountStartOrderLevel").getAsInt();
						chineseMode.setCount(mountStartLevel);
					}
					break;
				case QuestChineseOrderDefines.GameInstanceFinish:
					String targetInstanceRefId = elem.get("gameInstanceRefId").getAsString();
					chineseMode.setChineseModeTarget(targetInstanceRefId);
					break;
				case QuestChineseOrderDefines.GameInstanceEverEnter:
					String targetInstanceRefId2 = elem.get("gameInstanceRefId").getAsString();
					chineseMode.setChineseModeTarget(targetInstanceRefId2);
					break;
				case QuestChineseOrderDefines.WingLevelUp:
					// int wingLevel = elem.get("wingLevel").getAsInt();
					// chineseMode.setNumber(wingLevel);

					int wingStageLevel = elem.get("wingStageOrderLevel").getAsInt();
					chineseMode.setNumber(wingStageLevel);
					if (elem.has("wingStartOrderLevel") && (!elem.get("wingStartOrderLevel").isJsonNull())) {
						int wingStartLevel = elem.get("wingStartOrderLevel").getAsInt();
						chineseMode.setCount(wingStartLevel);
					}

					break;
				case QuestChineseOrderDefines.BuyStoreItem:
					int buyNum = elem.get("buyNum").getAsInt();
					String buyItem = elem.get("buyItem").getAsString();
					chineseMode.setNumber(buyNum);
					chineseMode.setChineseModeTarget(buyItem);
					break;
				case QuestChineseOrderDefines.HasBuffer:
					String bufferRefId = elem.get("bufferRefId").getAsString();
					chineseMode.setChineseModeTarget(bufferRefId);
					break;
				case QuestChineseOrderDefines.Ladder:
					int ladderTime = elem.get("ladderTime").getAsInt();
					chineseMode.setNumber(ladderTime);
					break;
				case QuestChineseOrderDefines.Depot:
					String sceneRefId1 = elem.get("sceneRefId").getAsString();
					String npcRefId = elem.get("npcRefId").getAsString();
					chineseMode.setChineseModeValue(sceneRefId1);
					chineseMode.setChineseModeTarget(npcRefId);
					break;
				default:
					break;
				}
				chineseMode.setIndex((byte) 0);
				orderItemSet.add(chineseMode);
			}
		}
		singleOrder.setQuestRefOrder(orderItemSet);
		return singleOrder;
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
			if (entry.getKey().equals("bindedGold")) {
				int bindedGold = propertyReward.get("bindedGold").getAsInt();
				propertyList.add(new ValueProperty<Integer>(MGPropertySymbolDefines.BindedGold_Id, bindedGold));
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
}
