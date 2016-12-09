package newbee.morningGlory.checker.refObjectChecker.gameInstance;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.gameInstance.quest.MGGameInstanceQuestRef;
import newbee.morningGlory.mmorpg.gameInstance.quest.MGGameInstanceQuestRefMgr;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.ValueProperty;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.gameInstance.CompleteCondition;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRefMgr;
import sophia.mmorpg.monsterRefresh.RefreshMonsterRefData;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;
import sophia.mmorpg.player.quest.ref.order.SingleQuestRefOrder;
import sophia.mmorpg.player.quest.ref.reward.ItemQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.PlayerClassesQuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.PropertyQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardType;
import sophia.mmorpg.player.quest.ref.reward.SingleQuestRefReward;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

import com.google.common.base.Preconditions;

public class GameInstanceRefChecker extends BaseRefChecker<GameInstanceRef> {

	private static final String GAME_INSTANCE_REF_ID_PRE = "Ins_";

	@Override
	public void check(GameRefObject gameRefObject) {
		GameInstanceRef info = (GameInstanceRef) gameRefObject;
		this.checkStartsWithTheString(gameRefObject, info.getId(), GameInstanceRefChecker.GAME_INSTANCE_REF_ID_PRE, "refId");
		this.checkGameInstancePropertyData(info);// 副本数据
		this.checkGameInstanceScenePropertyData(info);// 副本层数据
		this.checkGameInstanceQuestPropertyData(info);// 副本层任务数据
	}

	@Override
	public String getDescription() {
		return "副本";
	}

	// ============================================================================================================================
	private void checkGameInstancePropertyData(GameInstanceRef ref) {
		PropertyDictionary pd = ref.getProperty();

		this.checkBetweenTheNumber(ref, MGPropertyAccesser.getIsTeam(pd), 0, 1, "isTeam");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getLevel(pd), 0, "level");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getTimesADay(pd), 0, "timesADay");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getTimesAWeek(pd), 0, "timesAWeek");
		if (!StringUtils.isEmpty(MGPropertyAccesser.getQuestRefId(pd))) {
			this.checkInGameRefObjectManager(ref, MGPropertyAccesser.getQuestRefId(pd), "questRefId");
		}
	}

	private void checkGameInstanceScenePropertyData(GameInstanceRef ref) {
		GameInstanceSceneRefMgr gameInstanceSceneRefMgr = ref.getGameInstanceDataObject(GameInstanceSceneRefMgr.class);
		List<GameInstanceSceneRef> allScene = gameInstanceSceneRefMgr.getInstanceSceneList();
		Preconditions.checkNotNull(allScene);
		for (GameInstanceSceneRef gameInstanceSceneRef : allScene) {
			PropertyDictionary pd = gameInstanceSceneRef.getGameInstanceScenePD();
			if (pd.contains(MGPropertySymbolDefines.QuestRefId_Id)) {
				this.checkInGameRefObjectManager(ref, MGPropertyAccesser.getSceneRefId(pd), "sceneRefId");
			}
			
			this.checkBetweenTheNumber(ref, MGPropertyAccesser.getSucceedType(pd), 0, 1, "succeedType");

			// 副本任务
			List<String> conditionField = gameInstanceSceneRef.getConditionField();
			Preconditions.checkNotNull(conditionField);
			for (String questId : conditionField) {
				this.checkGameInstanceQuestRefId(ref, questId, "conditionField", gameInstanceSceneRef);
			}

			// 通关条件
			CompleteCondition completeCondition = gameInstanceSceneRef.getCompleteCondition();
			Preconditions.checkNotNull(completeCondition);
			Map<String, Short> killMonsters = completeCondition.getKillMonsters();
			Preconditions.checkNotNull(killMonsters);
			for (String monsterRefId : killMonsters.keySet()) {
				if (!StringUtils.isEmpty(monsterRefId)) {
					this.checkInGameRefObjectManager(ref, monsterRefId, "succeedConditionData");
				}
				Short num = killMonsters.get(monsterRefId);
				this.checkMoreThanOrEqualToTheNumber(ref, num, 0, "succeedConditionData");
			}

			// 道具消耗
			Map<String, Integer> consumptionItems = gameInstanceSceneRef.getConsumptionItems();
			Preconditions.checkNotNull(consumptionItems);
			for (Entry<String, Integer> itemAndNum : consumptionItems.entrySet()) {
				String itemRefId = itemAndNum.getKey();
				Integer num = itemAndNum.getValue();
				this.checkMoreThanOrEqualToTheNumber(ref, num, 0, "consumptionItems");
				Preconditions.checkArgument(StringUtils.isNotEmpty(itemRefId));
				this.checkInGameRefObjectManager(ref, itemRefId, "succeedConditionData");
			}
		}
	}

	private void checkIfRefreshEnoughtMonster(GameRefObject object, GameInstanceSceneRef ref, MGGameInstanceQuestRef questRefOrderItem) {
		SingleQuestRefOrder singleOrder = (SingleQuestRefOrder) questRefOrderItem.getOrder();
		Set<QuestRefOrderItem> questRefOrder = singleOrder.getQuestRefOrder(null);
		if (questRefOrder == null) {
			error(object, "副本任务目标不存在，QuestRefId=" + questRefOrderItem.getId());
		}
		for (QuestRefOrderItem orderItem : questRefOrder) {
			if (orderItem.getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
				KillMonsterQuestRefOrderItem killQuest = (KillMonsterQuestRefOrderItem) orderItem;
				String sceneRefId = ref.getSceneRefId();
				SceneRef sceneRef = (SceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
				if(sceneRef == null){
					error(object, "场景Ref对象为null:"+sceneRefId);
				}
				List<RefreshMonsterRefData> refreshMonsterRefData = sceneRef.getRefreshMonsterRefDatas();
				if (refreshMonsterRefData == null) {
					error(object, "副本任务目标怪物刷怪规则不存在，MonsterRefId=" + killQuest.getMonsterRefId() + "   sceneRefId: " + ref.getSceneRefId());
				}
				int totleNum = 0;
				for (RefreshMonsterRefData refData : refreshMonsterRefData) {
					if (StringUtils.equals(refData.getMonsterGroup().getMonsterRefId(), killQuest.getMonsterRefId())) {
						totleNum += refData.getMonsterGroup().getNumber();
					}
				}
				if (killQuest.getNumber() > totleNum) {
					error(object, "副本任务目标怪物刷怪规则错误，怪物刷新数量少于任务杀怪完成条件， MonsterRefId=" + killQuest.getMonsterRefId() + "   sceneRefId: " + ref.getSceneRefId());
				}
			}
		}
	}

	private void checkGameInstanceQuestPropertyData(GameInstanceRef ref) {
		MGGameInstanceQuestRefMgr questMgr = ref.getGameInstanceDataObject(MGGameInstanceQuestRefMgr.class);
		List<MGGameInstanceQuestRef> allQuest = questMgr.getGameInstanceQuestRefList();
		if (allQuest != null) {
			for (MGGameInstanceQuestRef mgGameInstanceQuestRef : allQuest) {
				SingleQuestRefOrder singleOrder = (SingleQuestRefOrder) mgGameInstanceQuestRef.getOrder();
				Set<QuestRefOrderItem> questRefOrder = singleOrder.getQuestRefOrder(null);
				if (questRefOrder != null) {
					for (QuestRefOrderItem questRefOrderItem : questRefOrder) {
						if (questRefOrderItem.getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
							KillMonsterQuestRefOrderItem killQuest = (KillMonsterQuestRefOrderItem) questRefOrderItem;
							this.checkInGameRefObjectManager(ref, killQuest.getMonsterRefId(), "orderField");
							this.checkMoreThanOrEqualToTheNumber(ref, killQuest.getNumber(), 0, "orderField");
						} else if (questRefOrderItem.getOrderType() == QuestRefOrderType.Loot_Item_Order_Type) {
							LootItemQuestRefOrderItem lootQuest = (LootItemQuestRefOrderItem) questRefOrderItem;
							this.checkInGameRefObjectManager(ref, lootQuest.getItemRefId(), "orderField");
							this.checkMoreThanOrEqualToTheNumber(ref, lootQuest.getNumber(), 0, "orderField");
						} else if (questRefOrderItem.getOrderType() == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
							ChineseModeStringQuestRefOrderItem timeRelateQuest = (ChineseModeStringQuestRefOrderItem) questRefOrderItem;
							this.checkMoreThanOrEqualToTheNumber(ref, timeRelateQuest.getCount(), 0, "orderField");

							if (StringUtils.isNotEmpty(timeRelateQuest.getChineseModeValue())) {
								if (!timeRelateQuest.getChineseModeValue().equals("timeLimiteQuest") && !timeRelateQuest.getChineseModeValue().equals("survivalQuest")) {
									this.checkMoreThanOrEqualToTheNumber(ref, timeRelateQuest.getNumber(), 0, "orderField");
									String monsterRefId = timeRelateQuest.getChineseModeValue();
									this.checkInGameRefObjectManager(ref, monsterRefId, "orderField");
								}
							}

						}
					}
				}

				QuestRefReward reward = mgGameInstanceQuestRef.getReward();
				if (reward != null) {
					checkQuestRefReward(ref, reward);
				}
			}
		}
	}

	private void checkQuestRefReward(GameRefObject gameRefObject, QuestRefReward rewardRef) {
		if (rewardRef.getRelatedType() == 0) {
			SingleQuestRefReward reward = (SingleQuestRefReward) rewardRef;
			Set<QuestRefRewardItem> rewardItems = reward.getRewardItems(null);
			rewardItemsChecker(gameRefObject, rewardItems);
		} else if (rewardRef.getRelatedType() == 1) {
			PlayerClassesQuestRefReward reward = (PlayerClassesQuestRefReward) rewardRef;
			for (int i = 1; i <= 3; i++) { // 三种职业
				String profersion = Integer.toString(i);
				for (int j = 1; j <= 2; j++) { // 两种性别
					Set<QuestRefRewardItem> rewardItems = reward.checkRewardItems(profersion, (byte) (j));
					rewardItemsChecker(gameRefObject, rewardItems);
				}
			}
		}
	}

	private void rewardItemsChecker(GameRefObject gameRefObject, Set<QuestRefRewardItem> rewardItems) {
		for (QuestRefRewardItem rewardItem : rewardItems) {
			if (rewardItem.getRewardType() == QuestRefRewardType.Property_Reward_Type) {
				questRefWardPropertyChecker(gameRefObject, rewardItem);
			}
			if (rewardItem.getRewardType() == QuestRefRewardType.Item_Reward_Type) {
				questRefWardItemChecker(gameRefObject, rewardItem);
			}
		}
	}

	private void checkGameInstanceQuestRefId(GameRefObject gameRefObject, String refId, String fieldName, GameInstanceSceneRef gameInstanceSceneRef) {
		if (!StringUtils.isEmpty(refId)) {
			this.checkStartsWithTheString(gameRefObject, refId, "quest_", fieldName);
			GameInstanceRef info = (GameInstanceRef) gameRefObject;
			MGGameInstanceQuestRefMgr questMgr = info.getGameInstanceDataObject(MGGameInstanceQuestRefMgr.class);
			List<MGGameInstanceQuestRef> allQuest = questMgr.getGameInstanceQuestRefList();
			if (allQuest != null) {
				for (MGGameInstanceQuestRef mgGameInstanceQuestRef : allQuest) {
					if (refId.equals(mgGameInstanceQuestRef.getId())) {
						checkIfRefreshEnoughtMonster(gameRefObject, gameInstanceSceneRef, mgGameInstanceQuestRef);
						return;
					}
				}
				error(gameRefObject, getDescription() + fieldName + "不存在 !!! 错误的" + fieldName + "为: " + refId);
			}
		}
	}

	private void questRefWardPropertyChecker(GameRefObject gameRefObject, QuestRefRewardItem rewardItem) {
		PropertyQuestRefRewardItem propertyQuestRefRewardItem = (PropertyQuestRefRewardItem) rewardItem;
		List<ValueProperty<?>> propertyRewardList = propertyQuestRefRewardItem.getPropertyRewardList();
		if (propertyRewardList.size() > 0) {
			for (int i = 0; i < propertyRewardList.size(); i++) {
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Exp_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "副本任务<rewardField>错误 , Exp 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 金币
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Gold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "副本任务<rewardField>错误 , Gold 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 绑定元宝
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.BindedGold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "副本任务<rewardField>错误 , BindedGold 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 非绑元宝
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.UnbindedGold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "副本任务<rewardField>错误 , UnbindedGold 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 功勋
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Merit_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "副本任务<rewardField>错误 , Merit 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 成就
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Achievement_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "副本任务<rewardField>错误 , Achievement 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
			}
		}
	}

	private void questRefWardItemChecker(GameRefObject gameRefObject, QuestRefRewardItem rewardItem) {
		ItemQuestRefRewardItem itemQuestRefRewardItem = (ItemQuestRefRewardItem) rewardItem;
		String itemRefId = itemQuestRefRewardItem.getItemRefId();
		if (GameRoot.getGameRefObjectManager().getManagedObject(itemRefId) == null) {
			error(gameRefObject, "副本任务<rewardField>错误 , itemRefId不存在 !!! 错误的itemCount为: " + itemRefId);
		}
		this.checkMoreThanOrEqualToTheNumber(gameRefObject, itemQuestRefRewardItem.getNumber(), 0, "rewardField");
	}

}
