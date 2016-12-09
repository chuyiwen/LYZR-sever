package newbee.morningGlory.checker.refObjectChecker.dailyQuest;

import java.util.List;
import java.util.Set;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.dailyQuest.ref.MGDailyQuestRef;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.ValueProperty;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionItem;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionType;
import sophia.mmorpg.player.quest.ref.condition.SingleQuestCondition;
import sophia.mmorpg.player.quest.ref.npc.QuestRefNpc;
import sophia.mmorpg.player.quest.ref.npc.QuestRefNpcData;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrder;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;
import sophia.mmorpg.player.quest.ref.order.TalkQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.reward.ItemQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.PlayerClassesQuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.PropertyQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefReward;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardType;
import sophia.mmorpg.player.quest.ref.reward.SingleQuestRefReward;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public class DailyQuestRefChecker extends BaseRefChecker<MGDailyQuestRef> {

	@Override
	public String getDescription() {
		return "日常任务";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGDailyQuestRef info = (MGDailyQuestRef) gameRefObject;
		if (!info.getId().startsWith("quest_daily_")) {
			error(gameRefObject, "日常任务<refId>错误 , 请以quest_daily_开头!!! 错误的refId为: " + info.getId());
		}
		checkQuestPropertyData(gameRefObject);
		checkQuestCondition(gameRefObject);
		checkQuestRefOrder(gameRefObject);
		checkQuestRefReward(gameRefObject, 1);
		checkQuestRefReward(gameRefObject, 2);
		checkQuestRefNpc(gameRefObject);
	}

	private void checkQuestPropertyData(GameRefObject gameRefObject) {
		MGDailyQuestRef ref = (MGDailyQuestRef) gameRefObject;
		PropertyDictionary pd = ref.getProperty();
		int questType = MGPropertyAccesser.getQuestType(pd);
		if (questType != 2) {
			error(gameRefObject, "日常任务<questType>错误 , 请将 questType 修改为 2 !!! 错误的questType为: " + questType);
		}

		int dailyQuestType = MGPropertyAccesser.getDailyQuestType(pd);
		if (dailyQuestType < 1 || dailyQuestType > 2) {
			error(gameRefObject, "日常任务<dailyQuestType>错误 , dailyQuestType 只能为1或2 !!! 错误的dailyQuestType为: " + dailyQuestType);
		}

		int dailyProposeRing = MGPropertyAccesser.getDailyProposeRing(pd);
		if (dailyProposeRing < 0) {
			error(gameRefObject, "日常任务<dailyQuestType>错误 , dailyProposeRing 不小于 0 !!! 错误的dailyProposeRing为: " + dailyProposeRing);
		}
	}

	private void checkQuestRefOrder(GameRefObject gameRefObject) {
		MGDailyQuestRef ref = (MGDailyQuestRef) gameRefObject;
		List<QuestRefOrder> orderList = ref.getOrderList();
		for (QuestRefOrder questOrder : orderList) {
			Set<QuestRefOrderItem> orders = questOrder.getQuestRefOrder(null);
			for (QuestRefOrderItem order : orders) {
				byte orderType = order.getOrderType();
				if (orderType == QuestRefOrderType.Kill_Monster_Order_Type) {
					KillMonsterQuestRefOrderItem kmOrder = (KillMonsterQuestRefOrderItem) order;
					String killMonster = kmOrder.getMonsterRefId();
					if (!killMonster.startsWith("monster_")) {
						error(gameRefObject, "日常任务<orderField>错误 , monsterRefId错误，请修改为monster_开头 !!! 错误的monsterRefId为: " + killMonster);
					}
					if (GameRoot.getGameRefObjectManager().getManagedObject(killMonster) == null) {
						error(gameRefObject, "日常任务<orderField>错误, 怪物RefId不存在！！！错误的怪物RefId为" + killMonster);
					}
					int killCount = kmOrder.getNumber();
					if (killCount <= 0) {
						error(gameRefObject, "日常任务<orderField>错误 , killCount错误，killCount不能小于等于0 !!! 错误的killCount为: " + killCount);
					}
				}
				if (orderType == QuestRefOrderType.Collect_Order_Type) {
					CollectQuestRefOrderItem collectOrder = (CollectQuestRefOrderItem) order;
					String itemId = collectOrder.getCollectItemId();
					if (GameRoot.getGameRefObjectManager().getManagedObject(itemId) == null) {
						error(gameRefObject, "日常任务<orderField>错误, 采集物产生道具RefID不存在！！！错误的itemRefId为" + itemId);
					}
					String npcRefId = collectOrder.getCollectTargetObjectId();
					if (!npcRefId.startsWith("npc_collect_")) {
						error(gameRefObject, "日常任务<orderField>错误, 采集物产生 NPC 请以 npc_collect_ 开头！！！错误的npcRefId为" + npcRefId);
					}
					int itemCount = collectOrder.getNumber();
					if (itemCount <= 0) {
						error(gameRefObject, "日常任务<orderField>错误 , itemCount错误，itemCount不能小于等于0 !!! 错误的itemCount为: " + itemCount);
					}
				}
				if (orderType == QuestRefOrderType.Talk_Order_Type) {
					TalkQuestRefOrderItem talkOrder = (TalkQuestRefOrderItem) order;
					String npcId = talkOrder.getNpcRefId();
					if (GameRoot.getGameRefObjectManager().getManagedObject(npcId) == null) {
						error(gameRefObject, "日常任务<orderField>错误, NpcRefID不存在！！！错误的NpcRefID为" + npcId);
					}
				}
			}
		}
	}

	private void checkQuestRefReward(GameRefObject gameRefObject, int type) {
		MGDailyQuestRef ref = (MGDailyQuestRef) gameRefObject;
		QuestRefReward rewardRef = null;
		if (type == 1) {
			rewardRef = ref.getReward();
		} else if (type == 2) {
			rewardRef = ref.getFinalReward();
		}
		if (rewardRef == null) {
			error(gameRefObject, "日常任务<rewardField>错误 , rewardField 没有进行检测  !!! 请查看程序是否错误: ");
			return;
		}
		checkReward(gameRefObject, ref, rewardRef);
	}

	private void checkReward(GameRefObject gameRefObject, MGDailyQuestRef ref, QuestRefReward rewardRef) {
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

	private void questRefWardItemChecker(GameRefObject gameRefObject, QuestRefRewardItem rewardItem) {
		ItemQuestRefRewardItem itemQuestRefRewardItem = (ItemQuestRefRewardItem) rewardItem;
		String itemRefId = itemQuestRefRewardItem.getItemRefId();
		if (GameRoot.getGameRefObjectManager().getManagedObject(itemRefId) == null) {
			error(gameRefObject, "日常任务<rewardField>错误 , itemRefId不存在 !!! 错误的itemCount为: " + itemRefId);
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
						error(gameRefObject, "日常任务<rewardField>错误 , Exp 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 金币
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Gold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "日常任务<rewardField>错误 , Gold 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 绑定元宝
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.BindedGold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "日常任务<rewardField>错误 , BindedGold 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 非绑元宝
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.UnbindedGold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "日常任务<rewardField>错误 , UnbindedGold 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 功勋
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Merit_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "日常任务<rewardField>错误 , Merit 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
				// 成就
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Achievement_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					if (itemNumber < 0) {
						error(gameRefObject, "日常任务<rewardField>错误 , Achievement 数量 !!! 错误的数量为: " + itemNumber);
					}
				}
			}
		}
	}

	private void checkQuestRefNpc(GameRefObject gameRefObject) {
		MGDailyQuestRef ref = (MGDailyQuestRef) gameRefObject;
		QuestRefNpc npc = ref.getNpc();
		QuestRefNpcData npcData = npc.getQuestRefNpcData(null);
		String acceeptNpc = npcData.getAcceptedNpcRefId();
		String submitNpc = npcData.getTakeRewardNpcRefId();
		if (!StringUtils.isEmpty(acceeptNpc)) {
			if (GameRoot.getGameRefObjectManager().getManagedObject(acceeptNpc) == null) {
				error(gameRefObject, "日常任务<npcField>错误 , acceeptNpc不存在 !!! 错误的acceeptNpc为: " + acceeptNpc);
			}
		}
		if (!StringUtils.isEmpty(submitNpc)) {
			if (GameRoot.getGameRefObjectManager().getManagedObject(submitNpc) == null) {
				error(gameRefObject, "日常任务<npcField>错误 , submitNpc不存在 !!! 错误的submitNpc为: " + submitNpc);
			}
		}
	}

	private void checkQuestCondition(GameRefObject gameRefObject) {
		MGDailyQuestRef ref = (MGDailyQuestRef) gameRefObject;
		SingleQuestCondition singleCondition = (SingleQuestCondition) ref.getConditionSet();
		Set<QuestRefConditionItem> itemSet = singleCondition.getConditionItems(null);
		for (QuestRefConditionItem item : itemSet) {
			byte visibleType = QuestRefConditionType.Visiable_Condition_Type;
			byte acceptType = QuestRefConditionType.Accept_Condition_Type;
			int type = item.getType();
			if (type != visibleType && type != acceptType) {
				error(gameRefObject, "日常任务<conditionField>错误 , conditionType 只有 0 和 1 两种类型!!! 错误的conditionType为: " + item.getType());
			}
		}
	}
}
