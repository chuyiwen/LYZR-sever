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
package newbee.morningGlory.mmorpg.player.dailyQuest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.mmorpg.player.dailyQuest.ref.MGDailyQuestRef;
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.MeritPointStat;
import newbee.morningGlory.stat.logs.StatDailyQuest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.ValueProperty;
import sophia.game.GameRoot;
import sophia.game.component.GameObject;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.Bricks;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
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
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.gson.Gson;

public final class MGDailyQuest extends GameObject {
	private static Logger logger = Logger.getLogger(MGDailyQuest.class);
	private MGDailyQuestRef dailyQuestRef;
	private MGDailyQuestCourse dailyQuestCourse = new MGDailyQuestCourse();
	private byte startLevel = 0;
	private int nowTime = 0;
	private int randomOrderNum = 0;
	private boolean isAdded;
	private int vipAddRingTime = 0;
	private int playerLevel = 0;
	private long lastRefreshTime = 0;

	/**
	 * 参见{@link QuestState}
	 */
	private int questState;

	public MGDailyQuest() {
		setProperty(null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dailyQuestRef == null) ? 0 : dailyQuestRef.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MGDailyQuest other = (MGDailyQuest) obj;
		if (dailyQuestRef == null) {
			if (other.dailyQuestRef != null)
				return false;
		} else if (!dailyQuestRef.equals(other.dailyQuestRef))
			return false;
		return true;
	}

	public MGDailyQuestRef getDailyQuestRef() {
		return dailyQuestRef;
	}

	public void setDailyQuestRef(MGDailyQuestRef dailyQuestRef) {
		this.dailyQuestRef = dailyQuestRef;
	}

	public byte getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(byte level) {
		this.startLevel = level;
	}

	public MGDailyQuestCourse getDailyQuestCourse() {
		return dailyQuestCourse;
	}

	public void setDailyQuestCourse(MGDailyQuestCourse dailyQuestCourse) {
		this.dailyQuestCourse = dailyQuestCourse;
	}

	public int getQuestState() {
		return questState;
	}

	public void setQuestState(int questState) {
		this.questState = questState;
	}

	public void randomStart() {
		startLevel = (byte) SFRandomUtils.random10();
	}

	public int getNowTime() {
		return nowTime;
	}

	public void setNowTime(int nowTime) {
		this.nowTime = nowTime;
	}

	public int getRandomOrderNum() {
		return randomOrderNum;
	}

	public void setRandomOrderNum(int randomOrderNum) {
		this.randomOrderNum = randomOrderNum;
	}

	public boolean getIsAdded() {
		return isAdded;
	}

	public void setIsAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	public int getVipAddRingTime() {
		return vipAddRingTime;
	}

	public void setVipAddRingTime(int vipAddRingTime) {
		this.vipAddRingTime = vipAddRingTime;
	}

	public Map<Integer, String> createQuestCourseItem(Player player) {
		playerLevel = player.getLevel();
		MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		vipAddRingTime = vipComponent.getAddQuestRingTime();
		int lastNum = MGPropertyAccesser.getDailyProposeRing(getDailyQuestRef().getProperty()) + vipAddRingTime;
		QuestRefOrder questRefOrder = null;
		int dailyQuestType = MGPropertyAccesser.getDailyQuestType(getDailyQuestRef().getProperty());
		if (dailyQuestType == 1 && (nowTime >= lastNum)) {
			logger.error("DailyQuest in last ring,Can't accept anymore.dailyQuestRefId：" + dailyQuestRef.getId() + "; player:" + player);
			return new LinkedHashMap<>();
		} else if (dailyQuestType == 2 && (nowTime >= lastNum)) {
			// 属于无限循环日常任务 并且做过了推荐环数
			List<QuestRefOrder> questRefOrderList = getDailyQuestRef().getOrderList();
			questRefOrder = questRefOrderList.get(1);
		} else {
			List<QuestRefOrder> questRefOrderList = getDailyQuestRef().getOrderList();
			questRefOrder = questRefOrderList.get(0);
		}
		Set<QuestRefOrderItem> orderItems = questRefOrder.getQuestRefOrder(player);
		int num = SFRandomUtils.random(1, orderItems.size() - 1);
		randomOrderNum = num;
		return actToOrderList(num, orderItems);
	}

	public boolean checkIfEnoughtLevel(String monsterRefId) {
		MonsterRef monsterRef = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
		int monsterLevel = MGPropertyAccesser.getLevel(monsterRef.getProperty());
		return playerLevel >= monsterLevel;
	}

	private Map<Integer, QuestRefOrderItem> getRandomableList(List<QuestRefOrderItem> orderItemsList) {
		Map<Integer, QuestRefOrderItem> map = new HashMap<Integer, QuestRefOrderItem>();
		int i = 0;
		for (QuestRefOrderItem refOrderItem : orderItemsList) {
			byte courseItemType = refOrderItem.getOrderType();
			if (courseItemType == QuestRefOrderType.Kill_Monster_Order_Type) {
				KillMonsterQuestRefOrderItem killOrderItem = (KillMonsterQuestRefOrderItem) refOrderItem;
				String monsterRefId = killOrderItem.getMonsterRefId();
				if (checkIfEnoughtLevel(monsterRefId)) {
					map.put(i, refOrderItem);
				}
			}
			i++;
		}
		return map;
	}

	public QuestRefOrderItem getRandomOrderItem(int num, Set<QuestRefOrderItem> orderItems) {
		QuestRefOrderItem orderItem = null;
		Map<Integer, QuestRefOrderItem> randomableList = getRandomableList(new ArrayList<>(orderItems));
		if (randomableList.size() == 1) {
			for (Integer i : randomableList.keySet()) {
				num = i;
				orderItem = randomableList.get(i);
			}
		} else if (randomableList.size() > 1) {
			int k = SFRandomUtils.random(1, randomableList.size() - 1);
			for (Entry<Integer, QuestRefOrderItem> entry : randomableList.entrySet()) {
				if (k == 0) {
					break;
				}
				k--;
				num = entry.getKey();
				orderItem = randomableList.get(num);
			}
		} else {
			logger.info("Do not have course sute to Player .randomableList:" + randomableList);
		}
		setRandomOrderNum(num);
		return orderItem;
	}

	public Map<Integer, String> actToOrderList(int num, Set<QuestRefOrderItem> orderItems) {
		Map<Integer, String> list = new LinkedHashMap<>();
		QuestRefOrderItem orderItem = getRandomOrderItem(num, orderItems);
		List<QuestCourseItem> questCourseItemList = dailyQuestCourse.getQuestCourseItemList();
		questCourseItemList.clear();
		String targetId = null;
		QuestCourseItem courseItem = null;
		byte courseItemType = orderItem.getOrderType();
		switch (courseItemType) {
		case QuestRefOrderType.Kill_Monster_Order_Type:
			KillMonsterQuestRefOrderItem killMonsterQuestRefOrderItem = (KillMonsterQuestRefOrderItem) orderItem;
			targetId = killMonsterQuestRefOrderItem.getMonsterRefId();
			courseItem = new KillQuestCourseItem(killMonsterQuestRefOrderItem, (short) 0);
			questCourseItemList.add(courseItem);
			list.put((int) courseItemType, targetId);
			questState = QuestState.AcceptedQuestState;
			break;
		case QuestRefOrderType.Loot_Item_Order_Type:
			LootItemQuestRefOrderItem lootItemQuestRefOrderItem = (LootItemQuestRefOrderItem) orderItem;
			targetId = lootItemQuestRefOrderItem.getItemRefId();
			courseItem = new LootQuestCourseItem(lootItemQuestRefOrderItem, (short) 0);
			questCourseItemList.add(courseItem);
			list.put((int) courseItemType, targetId);
			questState = QuestState.AcceptedQuestState;
			break;
		case QuestRefOrderType.Talk_Order_Type:
			courseItem = new TalkQuestCourseItem((TalkQuestRefOrderItem) orderItem, true);
			questCourseItemList.add(courseItem);
			questState = QuestState.SubmittableQuestState;
			break;
		case QuestRefOrderType.Collect_Order_Type:
			CollectQuestRefOrderItem collectQuestRefOrderItem = (CollectQuestRefOrderItem) orderItem;
			targetId = collectQuestRefOrderItem.getCollectItemId();
			courseItem = new CollectQuestCourseItem(collectQuestRefOrderItem, (short) 0);
			questCourseItemList.add(courseItem);
			list.put((int) courseItemType, targetId);
			questState = QuestState.AcceptedQuestState;
			break;
		}
		return list;
	}

	public void setQuestCourseIteam(int orderNum, Set<QuestRefOrderItem> questRefOrderSet) {
		List<QuestRefOrderItem> questRefOrderList = new ArrayList<>(questRefOrderSet);
		QuestRefOrderItem orderItem = questRefOrderList.get(orderNum);
		byte courseItemType = orderItem.getOrderType();
		QuestCourseItem courseItem = null;
		List<QuestCourseItem> questCourseItemList = dailyQuestCourse.getQuestCourseItemList();
		questCourseItemList.clear();
		switch (courseItemType) {
		case QuestRefOrderType.Kill_Monster_Order_Type:
			KillMonsterQuestRefOrderItem killMonsterQuestRefOrderItem = (KillMonsterQuestRefOrderItem) orderItem;
			courseItem = new KillQuestCourseItem(killMonsterQuestRefOrderItem, (short) 0);
			questCourseItemList.add(courseItem);
			questState = QuestState.AcceptedQuestState;
			break;
		case QuestRefOrderType.Collect_Order_Type:
			CollectQuestRefOrderItem collectQuestRefOrderItem = (CollectQuestRefOrderItem) orderItem;
			courseItem = new CollectQuestCourseItem(collectQuestRefOrderItem, (short) 0);
			questCourseItemList.add(courseItem);
			questState = QuestState.AcceptedQuestState;
			break;
		}
	}

	public RuntimeResult takeRewardTo(Player player, int rewardLevel) {
		List<ItemPair> rewardItemList = new ArrayList<>();
		List<ItemPair> rewardPropertyList = new ArrayList<>();
		Set<QuestRefRewardItem> rewardItems = null;
		MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		vipAddRingTime = vipComponent.getAddQuestRingTime();
		int lastNum = MGPropertyAccesser.getDailyProposeRing(getDailyQuestRef().getProperty()) + vipAddRingTime;
		int dailyQuestType = MGPropertyAccesser.getDailyQuestType(getDailyQuestRef().getProperty());
		if (nowTime == lastNum) {
			rewardItems = this.dailyQuestRef.getFinalReward().getRewardItems(player);
		} else if (nowTime > lastNum && dailyQuestType == 2) {
			rewardItems = this.dailyQuestRef.getOverOrderReward().getRewardItems(player);
		} else {
			rewardItems = this.dailyQuestRef.getReward().getRewardItems(player);
		}
		for (QuestRefRewardItem questRefRewardItem : rewardItems) {
			if (questRefRewardItem.getRewardType() == QuestRefRewardType.Item_Reward_Type) {
				rewardItemList = addRewardItemList(questRefRewardItem, rewardItemList);
			} else if (questRefRewardItem.getRewardType() == QuestRefRewardType.Property_Reward_Type) {
				rewardPropertyList = addRewardPropertyList(questRefRewardItem, rewardPropertyList, rewardLevel);
			}
		}

		ItemPair meritItem = getMeritItem(rewardPropertyList);
		RuntimeResult runtimeResult = ItemFacade.addItem(player, rewardPropertyList, ItemOptSource.DailyQuest);
		if (runtimeResult.isOK()) {
			MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent) player.getTagged(MGPlayerPeerageComponent.Tag);
			peerageComponent.getMeritManager().addMerit(meritItem.getNumber());
			MGStatFunctions.meritPointStat(player, MeritPointStat.Add, MeritPointStat.Quest, meritItem.getNumber());
		}
		if (rewardItemList.size() > 0) {
			runtimeResult = ItemFacade.addItem(player, rewardItemList, ItemOptSource.DailyQuest);
			if (!runtimeResult.isOK()) {
				String questName = MGPropertyAccesser.getName(dailyQuestRef.getProperty());
				String content = Bricks.getContents("system_prompt_config_11", questName);
				String json = (new Gson()).toJson(rewardItemList);
				MailMgr.sendMailById(player.getId(), content, Mail.gonggao, json, 0, 0, 0);
			}
		}

		MGStatFunctions.dailyQuestStat(player, StatDailyQuest.Reward, dailyQuestRef.getId(), nowTime, vipAddRingTime, startLevel, "");
		return runtimeResult;
	}

	public ItemPair getMeritItem(List<ItemPair> rewardPropertyList) {
		ItemPair item = new ItemPair();
		for (int i = 0; i < rewardPropertyList.size(); i++) {
			ItemPair itemPair = rewardPropertyList.get(i);
			String itemRefId = itemPair.getItemRefId();
			if (StringUtils.equals("merit", itemRefId)) {
				item = itemPair;
				rewardPropertyList.remove(i);
			}
		}
		return item;
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

	public List<ItemPair> addRewardPropertyList(QuestRefRewardItem questRefRewardItem, List<ItemPair> rewardPropertyList, int rewardLevel) {
		PropertyQuestRefRewardItem propertyQuestRefRewardItem = (PropertyQuestRefRewardItem) questRefRewardItem;
		List<ValueProperty<?>> propertyRewardList = propertyQuestRefRewardItem.getPropertyRewardList();
		if (propertyRewardList.size() > 0) {
			for (int i = 0; i < propertyRewardList.size(); i++) {
				if (propertyRewardList.get(i).getId() == MGPropertySymbolDefines.Exp_Id) {
					int itemNumber = (Integer) propertyRewardList.get(i).getValue();
					itemNumber = (itemNumber + (int) (itemNumber * (0.2 * startLevel))) * rewardLevel;
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

	public void readQuestCourseItem(int courseItemSize, byte orderType, short number) {
		int dailyQuestType = MGPropertyAccesser.getDailyQuestType(getDailyQuestRef().getProperty());
		int lastNum = MGPropertyAccesser.getDailyProposeRing(getDailyQuestRef().getProperty());
		Set<QuestRefOrderItem> questRefOrderSet = null;
		if (dailyQuestType == 2 && nowTime > lastNum) {
			questRefOrderSet = getDailyQuestRef().getOrderList().get(1).getQuestRefOrder(null);
		} else {
			questRefOrderSet = getDailyQuestRef().getOrderList().get(0).getQuestRefOrder(null);
		}
		List<QuestRefOrderItem> orderItemsList = new ArrayList<>(questRefOrderSet);
		QuestRefOrderItem orderItem = orderItemsList.get(randomOrderNum);
		switch (orderItem.getOrderType()) {
		case QuestRefOrderType.Kill_Monster_Order_Type:
			KillMonsterQuestRefOrderItem killMonsterQuestRefOrderItem = (KillMonsterQuestRefOrderItem) orderItem;
			QuestCourseItem courseItem1 = new KillQuestCourseItem(killMonsterQuestRefOrderItem, number);
			List<QuestCourseItem> collTemp = dailyQuestCourse.getQuestCourseItemList();
			collTemp.add(courseItem1);
			questState = QuestState.AcceptedQuestState;
			break;
		case QuestRefOrderType.Collect_Order_Type:
			CollectQuestRefOrderItem collectQuestRefOrderItem = (CollectQuestRefOrderItem) orderItem;
			QuestCourseItem courseItem2 = new CollectQuestCourseItem(collectQuestRefOrderItem, number);
			List<QuestCourseItem> killTemp = dailyQuestCourse.getQuestCourseItemList();
			killTemp.add(courseItem2);
			questState = QuestState.AcceptedQuestState;
			break;
		}
	}

	public void questFinish(int addRingTime) {
		int dailyQuestType = MGPropertyAccesser.getDailyQuestType(getDailyQuestRef().getProperty());
		switch (dailyQuestType) {
		case 1: {
			// 有限任务类型到达限制时不能再接
			if (nowTime < (MGPropertyAccesser.getDailyProposeRing(getDailyQuestRef().getProperty()) + addRingTime)) {
				setQuestState(QuestState.AcceptableQuestState);
			}
			setStartLevel((byte) 0);
			break;
		}
		case 2: {
			// 无限任务均设为未接
			setQuestState(QuestState.AcceptableQuestState);
			setStartLevel((byte) 0);
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

	// 任务进度更新接口
	public boolean isAddToCourse(String courseId, int num) {
		isAdded = false;
		if (questState != QuestState.AcceptedQuestState) {
			return isAdded;
		}
		List<QuestCourseItem> questCourseList = dailyQuestCourse.getQuestCourseItemList();
		int succeedCourse = 0;
		for (QuestCourseItem item : questCourseList) {
			if (item.wasCompleted()) {
				succeedCourse += 1;
				continue;
			}
			if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
				item = addKillCourseNumber(item, courseId, num);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Collect_Order_Type) {
				item = addCollectCourseNumber(item, courseId, num);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
				ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) item.getQuestRefOrderItem();
				if (chineseMode.getChineseModeValue() != "survivalQuest" && chineseMode.getChineseModeValue() != "timeLimiteQuest") {
					item = addChineseCourseNumber(item, chineseMode.getChineseModeValue(), num);
				}
			}
		}
		if (questCourseList.size() > 0 && succeedCourse == questCourseList.size()) {
			return true;
		}
		return isAdded;
	}

	private KillQuestCourseItem addKillCourseNumber(QuestCourseItem item, String MonsterId, int num) {
		KillQuestCourseItem kmOrder = (KillQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, MonsterId)) {
			short courseNum = kmOrder.getCourseNumber();
			if (courseNum < 0) {
				courseNum = 0;
			}
			kmOrder.addCourseNumber();
			isAdded = true;
		}
		return kmOrder;
	}

	private CollectQuestCourseItem addCollectCourseNumber(QuestCourseItem item, String CollectId, int num) {
		CollectQuestCourseItem collectOrder = (CollectQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, CollectId)) {
			collectOrder.addCourseNumber();
			isAdded = true;
		}
		return collectOrder;
	}

	private ChineseModeStringQuestCourseItem addChineseCourseNumber(QuestCourseItem item, String MonsterId, int num) {
		ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
		String target = getCourseTarget(item);
		if (StringUtils.equals(target, MonsterId)) {
			chineseOrder.addCourseNumber();
			isAdded = true;
		}
		return chineseOrder;
	}

	public long getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(long lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}
}
