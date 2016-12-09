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
package newbee.morningGlory.mmorpg.player.gameInstance.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.mmorpg.gameInstance.quest.MGGameInstanceQuestRef;
import sophia.foundation.property.ValueProperty;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourse;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.course.TalkQuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrder;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;
import sophia.mmorpg.player.quest.ref.order.TalkQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.reward.ItemQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.PropertyQuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardItem;
import sophia.mmorpg.player.quest.ref.reward.QuestRefRewardType;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.RuntimeResult;

public final class MGGameInstanceQuest {
	private MGGameInstanceQuestRef questRef;

	private final QuestCourse questCourse = new QuestCourse();

	private String gameInstaceId;

	public String getGameInstaceId() {
		return gameInstaceId;
	}

	public void setGameInstaceId(String gameInstaceId) {
		this.gameInstaceId = gameInstaceId;
	}

	public String getGameInstanceRefId() {
		return gameInstanceRefId;
	}

	public void setGameInstanceRefId(String gameInstanceRefId) {
		this.gameInstanceRefId = gameInstanceRefId;
	}

	public String getGameSceneRefId() {
		return gameSceneRefId;
	}

	public void setGameSceneRefId(String gameSceneRefId) {
		this.gameSceneRefId = gameSceneRefId;
	}

	private String gameInstanceRefId;

	private String gameSceneRefId;

	/**
	 * 参见{@link QuestState}
	 */
	private int questState;

	private boolean isAdded;

	public MGGameInstanceQuest() {
	}

	public MGGameInstanceQuestRef getQuestRef() {
		return questRef;
	}

	public void setQuestRef(MGGameInstanceQuestRef questRef) {
		this.questRef = questRef;
	}

	public int getQuestState() {
		return questState;
	}

	public void setQuestState(int questState) {
		this.questState = questState;
	}

	public QuestCourse getQuestCourse() {
		return questCourse;
	}

	/**
	 * 只奖励属性，道具奖励将该副本的所有道具集合起来再奖励
	 * 
	 * @param player
	 * @return
	 */
	public List<ItemPair> takeRewardTo(Player player) {
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
		if (runtimeResult.isOK()) {
			runtimeResult = ItemFacade.addItem(player, rewardPropertyList, ItemOptSource.GameInstanceQuest);
		}
		return rewardItemList;
	}

	/**
	 * 获取所有的奖励，包括道具和属性奖励
	 * 
	 * @param player
	 * @return
	 */
	public List<ItemPair> takeAllRewardTo(Player player) {
		List<ItemPair> rewardItemList = new ArrayList<>();
		Set<QuestRefRewardItem> rewardItems = this.questRef.getReward().getRewardItems(player);
		for (QuestRefRewardItem questRefRewardItem : rewardItems) {
			if (questRefRewardItem.getRewardType() == QuestRefRewardType.Item_Reward_Type) {
				rewardItemList = addRewardItemList(questRefRewardItem, rewardItemList);
			} else if (questRefRewardItem.getRewardType() == QuestRefRewardType.Property_Reward_Type) {
				rewardItemList = addRewardPropertyList(questRefRewardItem, rewardItemList);
			}
		}
		return rewardItemList;
	}

	public List<ItemPair> addRewardItemList(QuestRefRewardItem questRefRewardItem, List<ItemPair> rewardItemList) {
		ItemQuestRefRewardItem itemQuestRefRewardItem = (ItemQuestRefRewardItem) questRefRewardItem;
		String itemRefId = itemQuestRefRewardItem.getItemRefId();
		int itemNumber = itemQuestRefRewardItem.getNumber();
		boolean status = itemQuestRefRewardItem.isBinded();
		ItemPair item = new ItemPair(itemRefId, itemNumber, status);
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

	public List<Integer> createQuestCourseItem(Player player) {
		QuestRefOrder questRefOrder = getQuestRef().getOrder();
		Set<QuestRefOrderItem> questRefOrderSet = questRefOrder.getQuestRefOrder(player);
		int orderNumber = questRefOrderSet.size();
		questCourse.setQuestCourseItemList(new ArrayList<QuestCourseItem>(orderNumber));

		List<Integer> list = new ArrayList<>();
		for (QuestRefOrderItem questRefOrderItem : questRefOrderSet) {
			byte courseItemType = questRefOrderItem.getOrderType();
			QuestCourseItem courseItem = null;
			short number = 0;
			switch (courseItemType) {
			case QuestRefOrderType.Collect_Order_Type:
				CollectQuestRefOrderItem collectQuestRefOrderItem = (CollectQuestRefOrderItem) questRefOrderItem;
				courseItem = new CollectQuestCourseItem(collectQuestRefOrderItem, number);
				questCourse.addQuestCourseItem(courseItem);
				list.add((int) courseItemType);
				break;
			case QuestRefOrderType.Kill_Monster_Order_Type:
				KillMonsterQuestRefOrderItem killMonsterQuestRefOrderItem = (KillMonsterQuestRefOrderItem) questRefOrderItem;
				courseItem = new KillQuestCourseItem(killMonsterQuestRefOrderItem, number);
				questCourse.addQuestCourseItem(courseItem);
				list.add((int) courseItemType);
				break;
			case QuestRefOrderType.Loot_Item_Order_Type:
				LootItemQuestRefOrderItem lootItemQuestRefOrderItem = (LootItemQuestRefOrderItem) questRefOrderItem;
				courseItem = new LootQuestCourseItem(lootItemQuestRefOrderItem, number);
				questCourse.addQuestCourseItem(courseItem);
				list.add((int) courseItemType);
				break;
			case QuestRefOrderType.Talk_Order_Type:
				courseItem = new TalkQuestCourseItem((TalkQuestRefOrderItem) questRefOrderItem, true);
				questCourse.addQuestCourseItem(courseItem);
				break;
			// 限时类型
			case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
				ChineseModeStringQuestRefOrderItem chineseModeItem = (ChineseModeStringQuestRefOrderItem) questRefOrderItem;
				String modeValue = chineseModeItem.getChineseModeValue();
				courseItem = new ChineseModeStringQuestCourseItem(chineseModeItem, modeValue, number);
				questCourse.addQuestCourseItem(courseItem);
				if (modeValue != "survivalQuest" && modeValue != "timeLimiteQuest") {
					list.add((int) courseItemType);
				}
				break;
			case QuestRefOrderType.ChineseMode_Int_Value_Order_Type:
				break;
			}
		}
		return list;
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

	// 任务进度更新接口
	public boolean isAddToCourse(String courseId) {
		isAdded = false;
		List<QuestCourseItem> questCourseList = questCourse.getQuestCourseItemList();
		List<QuestCourseItem> newCourseList = new ArrayList<>();
		for (QuestCourseItem item : questCourseList) {
			if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
				newCourseList.add(setKillCourseNumber(item, courseId));
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Collect_Order_Type) {
				newCourseList.add(setCollectCourseNumber(item, courseId));
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
				ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) item.getQuestRefOrderItem();
				if (chineseMode.getChineseModeValue() != "survivalQuest" && chineseMode.getChineseModeValue() != "timeLimiteQuest") {
					newCourseList.add(setChineseCourseNumber(item, courseId));
				} else {
					newCourseList.add(item);
				}
			} else {
				newCourseList.add(item);
			}
		}
		questCourse.setQuestCourseItemList(newCourseList);
		return isAdded;
	}

	private KillQuestCourseItem setKillCourseNumber(QuestCourseItem item, String MonsterId) {
		KillQuestCourseItem kmOrder = (KillQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, MonsterId)) {
			kmOrder.addCourseNumber();
			isAdded = true;
		}
		return kmOrder;
	}

	private CollectQuestCourseItem setCollectCourseNumber(QuestCourseItem item, String CollectId) {
		CollectQuestCourseItem collectOrder = (CollectQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, CollectId)) {
			collectOrder.addCourseNumber();
			isAdded = true;
		}
		return collectOrder;
	}

	private ChineseModeStringQuestCourseItem setChineseCourseNumber(QuestCourseItem item, String MonsterId) {
		ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, MonsterId)) {
			chineseOrder.addCourseNumber();
			isAdded = true;
		}
		return chineseOrder;
	}

	public boolean getIsAdded() {
		return isAdded;
	}

	public void setIsAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}
}
