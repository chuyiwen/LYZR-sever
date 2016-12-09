package newbee.morningGlory.ref.loader.gameInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.mmorpg.gameInstance.condition.MGGameInstanceConditionMgr;
import newbee.morningGlory.mmorpg.gameInstance.condition.MGGameInstanceEnterCondition;
import newbee.morningGlory.mmorpg.gameInstance.condition.MGGameInstanceOpenCondition;
import newbee.morningGlory.mmorpg.gameInstance.condition.MGGameInstanceSceneFinishCondtion;
import newbee.morningGlory.mmorpg.gameInstance.condition.MGGameInstanceSceneFinishCondtionType;
import newbee.morningGlory.mmorpg.gameInstance.quest.MGGameInstanceQuestRef;
import newbee.morningGlory.mmorpg.gameInstance.quest.MGGameInstanceQuestRefMgr;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.ValueProperty;
import sophia.mmorpg.core.linyuesheng.LinYueShengModeCondition;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRefMgr;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.SingleQuestRefOrder;
import sophia.mmorpg.player.quest.ref.reward.ItemQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.PlayerClassesQuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.PropertyQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.SingleQuestRefReward;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GameInstanceRefLoader extends AbstractGameRefObjectLoader<GameInstanceRef> {

	private static final Logger logger = Logger.getLogger(GameInstanceRefLoader.class);

	private static final String REF_ID = "refId";
	private static final String CONFIG_DATA = "configData";
	private static final String PROPERTY = "property";
	/** 副本怪物刷新 **/
	/** 副本层 **/
	private static final String GAME_INSTANCE_SCENE_DATA = "gameInstanceSceneData";
	/** 副本 **/
	private static final String GAME_INSTANCE_DATA = "gameInstanceData";
	/** 副本任务 **/
	public static final String GAME_INSTANCE_QUEST = "gameInstanceQuest";

	/** 出生条件类型具体数据 **/
	/** 怪物数组 **/
	/** 重生条件类型具体数据 **/
	/** 任务ID **/
	private static final String CONDITION_FIELD = "conditionField";
	/** 类型具体数据 **/
	private static final String SUCCEED_CONDITION_DATA = "succeedConditionData";
	/** 开启时间 **/
	private static final String OPEN_DETAILS_DATA = "openDetailsData";
	/** 进入副本下一层的消耗道具 **/
	private static final String CONSUMPTION_ITEMS = "consumptionItems";

	public GameInstanceRefLoader(String instanceKey) {
		super(instanceKey);
	}

	@Override
	protected GameInstanceRef create() {
		return new GameInstanceRef();
	}

	@Override
	protected void fillNonPropertyDictionary(GameInstanceRef ref, JsonObject refDataRes) {
		String refId = ref.getId();
		if (logger.isDebugEnabled()) {
			logger.debug("loading GameInstanceRef refId: " + refId);
		}
		JsonObject configData = refDataRes.get(CONFIG_DATA).getAsJsonObject();
		MGGameInstanceConditionMgr gameInstanceConditionMgr = new MGGameInstanceConditionMgr();
		ref.addGameInstanceDataObject(gameInstanceConditionMgr);
		GameInstanceRef.addGameInstanceRefId(refId);

		for (Map.Entry<String, JsonElement> entry : configData.entrySet()) {
			if (GameInstanceRef.GAME_INSTANCE_QUEST.equals(entry.getKey())) {// 加载副本任务数据
				JsonObject gameInstanceQuestData = configData.get(GameInstanceRef.GAME_INSTANCE_QUEST).getAsJsonObject();
				ref.addGameInstanceDataObject(getMGGameInstanceQuestRefMgr(gameInstanceQuestData, ref));

			} else if (GameInstanceRef.GAME_INSTANCE_SCENE.equals(entry.getKey())) {// 加载副本层数据
				JsonObject gameInstanceSceneData = configData.get(GameInstanceRef.GAME_INSTANCE_SCENE).getAsJsonObject();
				ref.addGameInstanceDataObject(getMGGameInstanceSceneRefMgr(gameInstanceSceneData, ref, gameInstanceConditionMgr));
			} else if (GameInstanceRef.GAME_INSTANCE.equals(entry.getKey())) {// 加载副本数据
				JsonObject gameInstanceData = configData.get(GameInstanceRef.GAME_INSTANCE).getAsJsonObject();
				getGameInstanceRef(gameInstanceData, ref, gameInstanceConditionMgr);
				super.fillNonPropertyDictionary(ref, gameInstanceData);
			}
		}

	}

	/**
	 * 加载副本数据
	 * 
	 * @param refDataRes
	 * @param gameInstanceRef
	 * @throws Exception
	 */
	private void getGameInstanceRef(JsonObject refDataRes, GameInstanceRef gameInstanceRef, MGGameInstanceConditionMgr gameInstanceConditionMgr) {
		if (logger.isDebugEnabled()) {
			logger.debug("gameInstanceData: " + refDataRes.get(CONFIG_DATA));
		}
		JsonObject gameInstanceData = refDataRes.get(CONFIG_DATA).getAsJsonObject();
		List<LinYueShengModeCondition<Player>> gameInstanceEnterConditionList = new ArrayList<>();
		List<LinYueShengModeCondition<Player>> gameInstanceOpenConditionList = new ArrayList<>();

		for (Map.Entry<String, JsonElement> entry : gameInstanceData.entrySet()) {
			JsonObject data = entry.getValue().getAsJsonObject();
			JsonObject gameInstanceRefData = data.getAsJsonObject(GAME_INSTANCE_DATA);
			String openDetailsData = gameInstanceRefData.get(OPEN_DETAILS_DATA).getAsString();
			gameInstanceRef.setOpenDetailsData(openDetailsData);

			MGGameInstanceEnterCondition enterCondition = new MGGameInstanceEnterCondition();
			enterCondition.setGameInstanceRef(gameInstanceRef);
			gameInstanceEnterConditionList.add(enterCondition);

			MGGameInstanceOpenCondition openCondition = new MGGameInstanceOpenCondition();
			openCondition.setGameInstanceRef(gameInstanceRef);
			gameInstanceOpenConditionList.add(openCondition);

			JsonElement propertyJson = data.getAsJsonObject().get("property");
			if (propertyJson != null) {
				fillPropertyDictionary(gameInstanceRef.getProperty(), propertyJson.getAsJsonObject());
			}
		}
		gameInstanceConditionMgr.setGameInstanceEnterConditionList(gameInstanceRef.getId(), gameInstanceEnterConditionList);
		gameInstanceConditionMgr.setGameInstanceOpenConditionList(gameInstanceRef.getId(), gameInstanceOpenConditionList);
	}

	/**
	 * 副本层
	 * 
	 * @param refDataRes
	 * @param ref
	 * @return
	 */
	private GameInstanceSceneRefMgr getMGGameInstanceSceneRefMgr(JsonObject refDataRes, GameInstanceRef gameInstanceRef, MGGameInstanceConditionMgr gameInstanceConditionMgr) {
		JsonObject gameInstanceSceneAllData = refDataRes.get(CONFIG_DATA).getAsJsonObject();
		if (logger.isDebugEnabled()) {
			logger.debug("副本层 configData: " + gameInstanceSceneAllData);
		}
		GameInstanceSceneRefMgr sceneRefMgr = new GameInstanceSceneRefMgr();
		List<GameInstanceSceneRef> instanceSceneList = new ArrayList<GameInstanceSceneRef>();

		for (Map.Entry<String, JsonElement> entry : gameInstanceSceneAllData.entrySet()) {
			List<LinYueShengModeCondition<Player>> gameInstanceSceneFinishConditionList = new ArrayList<>();
			GameInstanceSceneRef gameInstanceSceneRef = new GameInstanceSceneRef();
			Map<String, Integer> consumptionItems = gameInstanceSceneRef.getConsumptionItems();
			JsonObject floorData = entry.getValue().getAsJsonObject();
			String refId = floorData.get(REF_ID).getAsString();
			gameInstanceSceneRef.setId(refId);
			JsonObject gameInstanceFloorData = floorData.getAsJsonObject(GAME_INSTANCE_SCENE_DATA);
			List<String> list = new ArrayList<>();
			Iterator<JsonElement> condition = gameInstanceFloorData.get(CONDITION_FIELD).getAsJsonArray().iterator();
			while (condition.hasNext()) {
				list.add(condition.next().getAsString());
			}
			String succeedConditionData = gameInstanceFloorData.get(SUCCEED_CONDITION_DATA).getAsString();
			JsonObject property = floorData.getAsJsonObject(PROPERTY);
			PropertyDictionary gameInstanceScenerPD = new PropertyDictionary();
			fillPropertyDictionary(gameInstanceScenerPD, property);

			MGGameInstanceSceneFinishCondtion finishCondition = new MGGameInstanceSceneFinishCondtion();
			finishCondition.setGameInstanceRef(gameInstanceSceneRef);
			gameInstanceSceneRef.setGameInstanceScenePD(gameInstanceScenerPD);
			if (MGGameInstanceSceneFinishCondtionType.Kill_NoMonsters_Type != gameInstanceSceneRef.getSucceedType()) {
				gameInstanceSceneFinishConditionList.add(finishCondition);
			}

			if (!gameInstanceFloorData.get(CONSUMPTION_ITEMS).isJsonNull() && gameInstanceFloorData.get(CONSUMPTION_ITEMS).isJsonObject()) {
				JsonObject consumptionItemsJsonObject = gameInstanceFloorData.get(CONSUMPTION_ITEMS).getAsJsonObject().get("item").getAsJsonObject();
				Integer itemCount = consumptionItemsJsonObject.get("itemCount").getAsInt();
				String itemRefId = consumptionItemsJsonObject.get("itemRefId").getAsString();
				Preconditions.checkNotNull(itemRefId);
				consumptionItems.put(itemRefId, itemCount);
			}

			gameInstanceSceneRef.setId(refId);
			gameInstanceSceneRef.setConditionField(list);
			gameInstanceSceneRef.setSucceedConditionData(succeedConditionData);
			gameInstanceSceneRef.setGameInstanceRef(gameInstanceRef);
			instanceSceneList.add(gameInstanceSceneRef);
			gameInstanceSceneRef.setSceneFinishConditionList(gameInstanceSceneFinishConditionList);
		}
		sceneRefMgr.setInstanceSceneList(instanceSceneList);
		return sceneRefMgr;
	}

	/**
	 * 副本任务
	 * 
	 * @param refDataRes
	 * @param ref
	 * @return
	 */
	private MGGameInstanceQuestRefMgr getMGGameInstanceQuestRefMgr(JsonObject refDataRes, GameInstanceRef gameInstanceRef) {
		JsonObject questAllData = refDataRes.get(CONFIG_DATA).getAsJsonObject();
		if (logger.isDebugEnabled()) {
			logger.debug("副本任务 configData: " + questAllData);
		}
		MGGameInstanceQuestRefMgr questRefMgr = new MGGameInstanceQuestRefMgr();
		List<MGGameInstanceQuestRef> instanceQuestList = new ArrayList<>();
		for (Map.Entry<String, JsonElement> entry : questAllData.entrySet()) {
			MGGameInstanceQuestRef questRef = new MGGameInstanceQuestRef();
			String questId = entry.getKey();
			questRef.setId(questId);
			JsonObject quest = entry.getValue().getAsJsonObject();
			JsonObject property = quest.get("property").getAsJsonObject();
			int rewardType = property.get("rewardImmediately").getAsInt();
			questRef.setRewardType(rewardType);
			JsonObject questData = quest.get("questData").getAsJsonObject();
			// 存放orderField
			if (questData.has("orderField")) {
				JsonElement orderField = questData.get("orderField");
				if (orderField.isJsonArray()) {
					Iterator<JsonElement> orderFieldData = orderField.getAsJsonArray().iterator();
					SingleQuestRefOrder singleOrder = new SingleQuestRefOrder();
					Set<QuestRefOrderItem> orderItemSet = new LinkedHashSet<QuestRefOrderItem>();
					singleOrder.setQuestRefOrder(orderItemSet);
					questRef.setOrder(singleOrder);
					while (orderFieldData.hasNext()) {
						JsonObject elem = orderFieldData.next().getAsJsonObject();
						int orderType = elem.get("orderType").getAsInt();
						if (orderType == 1) {
							KillMonsterQuestRefOrderItem killQuest = new KillMonsterQuestRefOrderItem();
							String monsterRefId = elem.get("monsterRefId").getAsString();
							int killCount = elem.get("killCount").getAsInt();
							killQuest.setMonsterRefId(monsterRefId);
							killQuest.setNumber(killCount);
							orderItemSet.add(killQuest);
						} else if (orderType == 2) {
							LootItemQuestRefOrderItem lootQuest = new LootItemQuestRefOrderItem();
							String itemRefId = elem.get("itemRefId").getAsString();
							int itemCount = elem.get("itemCount").getAsInt();
							lootQuest.setItemRefId(itemRefId);
							lootQuest.setNumber(itemCount);
							orderItemSet.add(lootQuest);
						}
						// 存活类型 timeCount
						// 限时通关类型 timeCount + killCount
						// 限时杀怪类型 （限定时间内杀死指定怪物） timeCount + killCount +
						// monsterRefId
						else if (orderType == 9) {
							ChineseModeStringQuestRefOrderItem timeRelateQuest = new ChineseModeStringQuestRefOrderItem();
							int timeCount = elem.get("timeCount").getAsInt();
							timeRelateQuest.setCount(timeCount);

							if (elem.has("killCount")) {
								int killCount = elem.get("killCount").getAsInt();
								timeRelateQuest.setNumber(killCount);
								if (elem.has("monsterRefId")) {
									String monsterRefId = elem.get("monsterRefId").getAsString();
									timeRelateQuest.setChineseModeValue(monsterRefId);
								} else {
									timeRelateQuest.setChineseModeValue("timeLimiteQuest");
								}
							} else {
								timeRelateQuest.setChineseModeValue("survivalQuest");
							}
							orderItemSet.add(timeRelateQuest);
						}
					}
				}
			} else {
				SingleQuestRefOrder singleOrder = new SingleQuestRefOrder();
				Set<QuestRefOrderItem> orderItemSet = new LinkedHashSet<QuestRefOrderItem>();
				singleOrder.setQuestRefOrder(orderItemSet);
				questRef.setOrder(singleOrder);
			}

			// 存放rewardField
			if (questData.has("rewardField") && questData.get("rewardField") != null) {
				JsonObject rewardField = questData.get("rewardField").getAsJsonObject();
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
								ItemQuestRefRewardItem item = getRewardItem(elem, relatedType);
								item.setRelateType(relatedType);
								itemRewardSet.add(item);
							}
							itemRewardSet.add(propertyRewardItem);
							singleReward.setRewardItemSet(itemRewardSet);
							questRef.setReward(singleReward);
						}

						// 区分职业
						else if (relatedType == 1) {
							PlayerClassesQuestRefReward classReward = new PlayerClassesQuestRefReward();
							Iterator<JsonElement> professionList = itemReward.get("professionList").getAsJsonArray().iterator();
							while (professionList.hasNext()) {
								JsonObject elem = professionList.next().getAsJsonObject();
								String proffessionRefId = elem.get("proffessionRefId").getAsString();
								System.out.println("proffessionRefId : " + proffessionRefId);
								Iterator<JsonElement> itemList = elem.get("itemList").getAsJsonArray().iterator();
								while (itemList.hasNext()) {
									Set<QuestRefRewardItem> itemRewardSet2 = new HashSet<>();
									JsonObject temp = itemList.next().getAsJsonObject();
									ItemQuestRefRewardItem item = getRewardItem(temp, relatedType);
									itemRewardSet2.add(item);
									itemRewardSet2.add(propertyRewardItem);
									classReward = setGenderAndProffession(classReward, proffessionRefId, itemRewardSet2);
								}
							}
							questRef.setReward(classReward);
						}
					} else {
						SingleQuestRefReward singleReward = new SingleQuestRefReward();
						Set<QuestRefRewardItem> rewardItemSet = new HashSet<QuestRefRewardItem>();
						rewardItemSet.add(propertyRewardItem);
						singleReward.setRewardItemSet(rewardItemSet);
						questRef.setReward(singleReward);
					}
				}
			}
			questRef.setGameInstanceRef(gameInstanceRef);
			instanceQuestList.add(questRef);
		}
		questRefMgr.setGameInstanceQuestRefList(instanceQuestList);
		return questRefMgr;
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
