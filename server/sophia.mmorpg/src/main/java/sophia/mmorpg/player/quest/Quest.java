/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.mmorpg.player.quest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.ValueProperty;
import sophia.game.component.GameObject;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourse;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.course.TalkQuestCourseItem;
import sophia.mmorpg.player.quest.event.QuestActionEventDefines;
import sophia.mmorpg.player.quest.ref.QuestRef;
import sophia.mmorpg.player.quest.ref.condition.QuestRefCondition;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionItem;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionType;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrder;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;
import sophia.mmorpg.player.quest.ref.order.TalkQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.reward.ItemQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.PropertyQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardType;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.logs.StatPlotQuest;
import sophia.mmorpg.utils.RuntimeResult;

public final class Quest extends GameObject {
	
	private static final Logger logger = Logger.getLogger(Quest.class);

	private QuestRef questRef;

	private final QuestCourse questCourse = new QuestCourse();

	private boolean isAdded;

	/**
	 * 参见{@link QuestState}
	 */
	private int questState;

	public Quest() {
		setProperty(null);
	}

	public final QuestRef getQuestRef() {
		return questRef;
	}

	public final void setQuestRef(QuestRef questRef) {
		this.questRef = questRef;
	}

	public final QuestCourse getQuestCourse() {
		return questCourse;
	}

	public final int getQuestState() {
		return questState;
	}

	public final void setQuestState(int questState) {
		this.questState = questState;
	}

	public RuntimeResult takeRewardTo(Player player) {
		List<ItemPair> rewardItemList = new ArrayList<>();
		List<ItemPair> rewardPropertyList = new ArrayList<>();
		Set<QuestRefRewardItem> rewardItems = this.questRef.getReward().getRewardItems(player);
		for (QuestRefRewardItem questRefRewardItem : rewardItems) {
			if (questRefRewardItem.getRewardType() == QuestRefRewardType.Item_Reward_Type) {
				rewardItemList = addRewardItemList(questRefRewardItem, rewardItemList);
			} else if (questRefRewardItem.getRewardType() == QuestRefRewardType.Property_Reward_Type) {
				rewardPropertyList = addRewardPropertyList(questRefRewardItem, rewardPropertyList);
			}
		}
		RuntimeResult runtimeResult = RuntimeResult.OK();

		if (!ItemFacade.addItemCompareSlot(player, rewardItemList, ItemOptSource.Quest).isOK()) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_QUEST_NOT_ENOUGHT_SPACE);
		}
		runtimeResult = ItemFacade.addItem(player, rewardPropertyList, ItemOptSource.Quest);
		StatFunctions.plotQuest(player, StatPlotQuest.Reward, questRef.getId());
		/**
		 * 策划需求更改，不发邮件，提示背包满 String questName =
		 * MGPropertyAccesser.getName(questRef.getProperty()); String content =
		 * new String("由于背包满，主线任务：“"+questName+"”奖励改为邮件发送。"); String json = (new
		 * Gson()).toJson(rewardItemList);
		 * MailMgr.sendMailByName(player.getId(), content, Mail.gonggao, json,
		 * 0, 0, 0);
		 */

		return runtimeResult;
	}

	public List<ItemPair> addRewardItemList(QuestRefRewardItem questRefRewardItem, List<ItemPair> rewardItemList) {
		ItemQuestRefRewardItem itemQuestRefRewardItem = (ItemQuestRefRewardItem) questRefRewardItem;
		String itemRefId = itemQuestRefRewardItem.getItemRefId();
		int itemNumber = itemQuestRefRewardItem.getNumber();
		boolean itemIsBinded = itemQuestRefRewardItem.isBinded();
		ItemPair item = new ItemPair(itemRefId, itemNumber, itemIsBinded);
		rewardItemList.add(item);
		return rewardItemList;
	}

	public List<ItemPair> addRewardPropertyList(QuestRefRewardItem questRefRewardItem, List<ItemPair> rewardPropertyList) {
		PropertyQuestRefRewardItem propertyQuestRefRewardItem = (PropertyQuestRefRewardItem) questRefRewardItem;
		List<ValueProperty<?>> propertyRewardList = propertyQuestRefRewardItem.getPropertyRewardList();
		if (propertyRewardList.size() > 0) {
			for (int i = 0; i < propertyRewardList.size(); i++) {
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Exp_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					ItemPair item = new ItemPair("exp", itemNumber, false);
					rewardPropertyList.add(item);
				}
				// 金币
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Gold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					ItemPair item = new ItemPair("gold", itemNumber, false);
					rewardPropertyList.add(item);
				}
				// 绑定元宝
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.BindedGold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					ItemPair item = new ItemPair("bindedGold", itemNumber, false);
					rewardPropertyList.add(item);
				}
				// 非绑元宝
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.UnbindedGold_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					ItemPair item = new ItemPair("unbindedGold", itemNumber, false);
					rewardPropertyList.add(item);
				}
				// 功勋
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Merit_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					ItemPair item = new ItemPair("merit", itemNumber, false);
					rewardPropertyList.add(item);
				}
				// 成就
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Achievement_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					ItemPair item = new ItemPair("achievement", itemNumber, false);
					rewardPropertyList.add(item);
				}
			}
		}
		return rewardPropertyList;
	}

	public Map<Integer, String> createQuestCourseItem(Player player) {
		QuestRefOrder questRefOrder = getQuestRef().getOrder();
		Set<QuestRefOrderItem> questRefOrderSet = questRefOrder.getQuestRefOrder(player);
		int orderNumber = questRefOrderSet.size();
		questCourse.setQuestCourseItemList(new ArrayList<QuestCourseItem>(orderNumber));
		Map<Integer, String> list = actToOrderList(questRefOrderSet);
		return list;
	}

	public Map<Integer, String> actToOrderList(Set<QuestRefOrderItem> questRefOrderSet) {
		Map<Integer, String> list = new LinkedHashMap<>();
		for (QuestRefOrderItem questRefOrderItem : questRefOrderSet) {
			byte courseItemType = questRefOrderItem.getOrderType();
			QuestCourseItem courseItem = null;
			String targetId = null;
			short number = 0;
			switch (courseItemType) {
			case QuestRefOrderType.Collect_Order_Type:
				CollectQuestRefOrderItem collectQuestRefOrderItem = (CollectQuestRefOrderItem) questRefOrderItem;
				targetId = collectQuestRefOrderItem.getCollectItemId();
				courseItem = new CollectQuestCourseItem(collectQuestRefOrderItem, number);
				questCourse.addQuestCourseItem(courseItem);
				list.put((int) courseItemType, targetId);
				questState = QuestState.AcceptedQuestState;
				break;
			case QuestRefOrderType.Kill_Monster_Order_Type:
				KillMonsterQuestRefOrderItem killMonsterQuestRefOrderItem = (KillMonsterQuestRefOrderItem) questRefOrderItem;
				targetId = killMonsterQuestRefOrderItem.getMonsterRefId();
				courseItem = new KillQuestCourseItem(killMonsterQuestRefOrderItem, number);
				questCourse.addQuestCourseItem(courseItem);
				list.put((int) courseItemType, targetId);
				questState = QuestState.AcceptedQuestState;
				break;
			case QuestRefOrderType.Loot_Item_Order_Type:
				LootItemQuestRefOrderItem lootItemQuestRefOrderItem = (LootItemQuestRefOrderItem) questRefOrderItem;
				targetId = lootItemQuestRefOrderItem.getItemRefId();
				courseItem = new LootQuestCourseItem(lootItemQuestRefOrderItem, number);
				questCourse.addQuestCourseItem(courseItem);
				list.put((int) courseItemType, targetId);
				questState = QuestState.AcceptedQuestState;
				break;
			case QuestRefOrderType.Talk_Order_Type:
				courseItem = new TalkQuestCourseItem((TalkQuestRefOrderItem) questRefOrderItem, true);
				questCourse.addQuestCourseItem(courseItem);
				questState = QuestState.SubmittableQuestState;
				break;
			case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
				ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) questRefOrderItem;
				courseItem = new ChineseModeStringQuestCourseItem();
				if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.GameInstanceEnter) {
					String chineseModeValue = chineseMode.getChineseModeValue();
					((ChineseModeStringQuestCourseItem) courseItem).setModeValue(chineseModeValue);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.PeerageLevelUp) {
					((ChineseModeStringQuestCourseItem) courseItem).setCourseNumber(number);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.MountLevelUp) {
					((ChineseModeStringQuestCourseItem) courseItem).setCourseNumber(number);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.GameInstanceFinish) {
					String chineseModeTarget = chineseMode.getChineseModeTarget();
					((ChineseModeStringQuestCourseItem) courseItem).setModeTarget(chineseModeTarget);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.GameInstanceEverEnter) {
					String chineseModeTarget = chineseMode.getChineseModeTarget();
					((ChineseModeStringQuestCourseItem) courseItem).setModeTarget(chineseModeTarget);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.WingLevelUp) {
					((ChineseModeStringQuestCourseItem) courseItem).setCourseNumber(number);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.BuyStoreItem) {
					((ChineseModeStringQuestCourseItem) courseItem).setCourseNumber(number);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.HasBuffer) {
					String chineseModeTarget = chineseMode.getChineseModeTarget();
					((ChineseModeStringQuestCourseItem) courseItem).setModeTarget(chineseModeTarget);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.Ladder) {
					((ChineseModeStringQuestCourseItem) courseItem).setCourseNumber(number);
				} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.Depot) {
					String chineseModeTarget = chineseMode.getChineseModeTarget();
					String chineseModeValue = chineseMode.getChineseModeValue();
					((ChineseModeStringQuestCourseItem) courseItem).setModeTarget(chineseModeTarget);
					((ChineseModeStringQuestCourseItem) courseItem).setModeValue(chineseModeValue);
				}
				((ChineseModeStringQuestCourseItem) courseItem).setQuestRefOrderItem(chineseMode);
				questCourse.addQuestCourseItem(courseItem);
				list.put((int) courseItemType, "");
				questState = QuestState.AcceptedQuestState;
				break;
			}
		}
		return list;
	}

	public void readQuestCourseInChineseModeString(short orderEventId, int number) {
		QuestRefOrder questRefOrder = getQuestRef().getOrder();
		Set<QuestRefOrderItem> questRefOrderSet = questRefOrder.getQuestRefOrder(null);
		actToOrderList(questRefOrderSet);
		List<QuestCourseItem> questCourseItemList = questCourse.getQuestCourseItemList();
		
		for (QuestRefOrderItem questRefOrderItem : questRefOrderSet) {
			ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) questRefOrderItem;
			if (orderEventId == QuestChineseOrderDefines.PeerageLevelUp && chineseMode.getOrderEventId() == orderEventId) {
				for (QuestCourseItem courseItem : questCourseItemList) {
					((ChineseModeStringQuestCourseItem)courseItem).setCourseNumber(number);
				}
			} else if (orderEventId == QuestChineseOrderDefines.MountLevelUp && chineseMode.getOrderEventId() == orderEventId) {
				for (QuestCourseItem courseItem : questCourseItemList) {
					((ChineseModeStringQuestCourseItem)courseItem).setCourseNumber(number);
				}
			} else if (orderEventId == QuestChineseOrderDefines.WingLevelUp && chineseMode.getOrderEventId() == orderEventId) {
				for (QuestCourseItem courseItem : questCourseItemList) {
					((ChineseModeStringQuestCourseItem)courseItem).setCourseNumber(number);
				}
			} else if (orderEventId == QuestChineseOrderDefines.BuyStoreItem && chineseMode.getOrderEventId() == orderEventId) {
				for (QuestCourseItem courseItem : questCourseItemList) {
					((ChineseModeStringQuestCourseItem)courseItem).setCourseNumber(number);
				}
			}
		}
	}
	
	public void readQuestCourseItem(int courseItemNumber, byte orderType, short number) {
		QuestRefOrder questRefOrder = getQuestRef().getOrder();
		Set<QuestRefOrderItem> questRefOrderSet = questRefOrder.getQuestRefOrder(null);
		actToOrderList(questRefOrderSet);
		for (int index = 0; index < courseItemNumber; index++) {
			switch (orderType) {
			case QuestRefOrderType.Collect_Order_Type:
				List<QuestCourseItem> collTemp = questCourse.getQuestCourseItemList();
				for (QuestCourseItem item : collTemp) {
					CollectQuestCourseItem collitem = (CollectQuestCourseItem) item;
					collitem.setCourseNumber(number);
				}
				break;
			case QuestRefOrderType.Kill_Monster_Order_Type:
				List<QuestCourseItem> killTemp = questCourse.getQuestCourseItemList();
				for (QuestCourseItem item : killTemp) {
					KillQuestCourseItem killitem = (KillQuestCourseItem) item;
					killitem.setCourseNumber(number);
				}
				break;
			case QuestRefOrderType.Loot_Item_Order_Type:
				LootQuestCourseItem lootQuestCourseItem = new LootQuestCourseItem();
				lootQuestCourseItem.setCourseNumber(number);
				break;
			case QuestRefOrderType.Talk_Order_Type:
				TalkQuestCourseItem talkQuestCourseItem = new TalkQuestCourseItem();
				questCourse.addQuestCourseItem(talkQuestCourseItem);
				break;
			}
		}
	}

	private String getCourseTarget(QuestCourseItem item) {
		byte courseItemType = item.getQuestRefOrderItem().getOrderType();
		switch (courseItemType) {
		case QuestRefOrderType.Collect_Order_Type:
			CollectQuestRefOrderItem collectTarget = (CollectQuestRefOrderItem) item.getQuestRefOrderItem();
			return collectTarget.getCollectItemId();
		case QuestRefOrderType.Kill_Monster_Order_Type:
			KillMonsterQuestRefOrderItem killTarget = (KillMonsterQuestRefOrderItem) item.getQuestRefOrderItem();
			return killTarget.getMonsterRefId();
		case QuestRefOrderType.Loot_Item_Order_Type:
			LootItemQuestRefOrderItem lootTarget = (LootItemQuestRefOrderItem) item.getQuestRefOrderItem();
			return lootTarget.getItemRefId();
		case QuestRefOrderType.Talk_Order_Type:
			break;
		case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
			ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) item.getQuestRefOrderItem();
			return chineseMode.getChineseModeValue();
		case QuestRefOrderType.ChineseMode_Int_Value_Order_Type:
			break;
		}
		return null;
	}

	public short getCourseNum() {
		List<QuestCourseItem> questCourseList = questCourse.getQuestCourseItemList();
		for (QuestCourseItem item : questCourseList) {
			byte courseItemType = item.getQuestRefOrderItem().getOrderType();
			switch (courseItemType) {
			case QuestRefOrderType.Collect_Order_Type:
				CollectQuestCourseItem collectTarget = (CollectQuestCourseItem) item;
				return collectTarget.getCourseNumber();
			case QuestRefOrderType.Kill_Monster_Order_Type:
				KillQuestCourseItem killTarget = (KillQuestCourseItem) item;
				return killTarget.getCourseNumber();
			case QuestRefOrderType.Talk_Order_Type:
				break;
			case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
				ChineseModeStringQuestCourseItem chineseMode = (ChineseModeStringQuestCourseItem) item;
				return (short) chineseMode.getCourseNumber();
			}
		}
		return 0;
	}

	// 任务进度更新接口
	public boolean setQuestCourseNum(String courseId, int num, Player player) {
		isAdded = false;
		List<QuestCourseItem> questCourseList = questCourse.getQuestCourseItemList();
		for (QuestCourseItem item : questCourseList) {
			if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
				addKillCourseNumber(item, courseId, num);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Collect_Order_Type) {
				addCollectCourseNumber(item, courseId, num, player);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
				ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) item.getQuestRefOrderItem();
				chineseModeQuestCourse(chineseMode, num, item, courseId);
			}
		}
		return isAdded;
	}

	private void chineseModeQuestCourse(ChineseModeStringQuestRefOrderItem chineseMode, int num, QuestCourseItem item, String courseId) {
		if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.GameInstanceEnter) {
			isAdded = true;
		} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.PeerageLevelUp) {
			ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
			chineseOrder.setCourseNumber(num);
			isAdded = true;
		} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.MountLevelUp) {
			ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
			chineseOrder.setCourseNumber(num);
			isAdded = true;
		} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.WingLevelUp) {
			ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
			chineseOrder.setCourseNumber(num);
			isAdded = true;
		} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.BuyStoreItem) {
			if (StringUtils.equals(chineseMode.getChineseModeTarget(), courseId)) {
				ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
				chineseOrder.setCourseNumber(chineseOrder.getCourseNumber() + num);
				isAdded = true;
			}
		} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.HasBuffer) {
			isAdded = true;
		} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.Ladder) {
			ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
			chineseOrder.setCourseNumber(num);
			isAdded = true;
		} else if (chineseMode.getOrderEventId() == QuestChineseOrderDefines.Depot) {
			isAdded = true;
		}
	}

	private KillQuestCourseItem addKillCourseNumber(QuestCourseItem item, String MonsterId, int num) {
		KillQuestCourseItem kmOrder = (KillQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, MonsterId)) {
			kmOrder.addCourseNumber();
			isAdded = true;
		}
		return kmOrder;
	}

	private CollectQuestCourseItem addCollectCourseNumber(QuestCourseItem item, String CollectId, int num, Player player) {
		CollectQuestCourseItem collectOrder = (CollectQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, CollectId)) {
			collectOrder.setCourseNumber((short) ItemFacade.getNumber(player, target));
			isAdded = true;
		}
		return collectOrder;
	}
	
	public boolean canAcceptQuest(Player player) {
		for (QuestRefCondition condition : getQuestRef().getConditionSet()) {
			if (condition.getConditionType() == QuestRefConditionType.Accept_Condition_Type) {
				for (QuestRefConditionItem item : condition.getConditionItems(player)) {
					if (!item.eligibleTo(player)) {
						logger.debug("Player is not enought Level to Accept Quest !");
						ResultEvent.sendResult(player.getIdentity(), QuestActionEventDefines.C2G_QST_QuestAccept, MMORPGErrorCode.CODE_QUEST_NO_LEVEL_ACCEPT);
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean getIsAdded() {
		return isAdded;
	}

	public void setIsAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}
}
