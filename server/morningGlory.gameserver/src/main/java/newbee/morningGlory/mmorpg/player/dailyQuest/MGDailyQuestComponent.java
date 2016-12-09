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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.dailyQuest.event.C2G_QST_DailyQuestAccept;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.C2G_QST_DailyQuestSubmit;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.C2G_QST_DailyStartLevel;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.C2G_QST_GetDailyQuestList;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.DailyQuestActionEventDefines;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.G2C_QST_DailyQuestAcceptedList;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.G2C_QST_DailyQuestUpdate;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.G2C_QST_DailyQuestVisibleList;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.G2C_QST_DailyStartLevel;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.G2C_QST_DailyStateUpdate;
import newbee.morningGlory.mmorpg.player.dailyQuest.ref.MGDailyQuestConfig;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatDailyQuest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.pluck.gameEvent.PluckSuccess_GE;

public final class MGDailyQuestComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(MGDailyQuestComponent.class);
	private MGDailyQuestManager dailyQuestManager = new MGDailyQuestManager();
	public static final String Tag = "MGDailyQuestComponent";
	private PersistenceObject persisteneceObject;
	private SFTimer delayPeriodClaendarChime;
	private int flashStartLevelCost = 20000;
	private int doubleExpCost = 200000;
	private Player player;
	
	public MGDailyQuestComponent() {
	}

	private void addTimer() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		delayPeriodClaendarChime = timerCreater.calendarChime(new MGDailyQuestTimer(dailyQuestManager, getConcreteParent()), SFTimeUnit.HOUR, 0);
	}

	private void removeTimer() {
		if (delayPeriodClaendarChime != null) {
			delayPeriodClaendarChime.cancel();
		}
	}

	private void checkToReset() {
		dailyQuestManager.twentyFourHourResetEvent();
		sendDailyQuestList();
	}

	public MGDailyQuestManager getDailyQuestManager() {
		return dailyQuestManager;
	}

	public void setDailyQuestManager(MGDailyQuestManager dailyQuestManager) {
		this.dailyQuestManager = dailyQuestManager;
	}

	public void setPersisteneceObject(PersistenceObject persisteneceObject) {
		this.persisteneceObject = persisteneceObject;
	}

	public PersistenceObject getPersisteneceObject() {
		return persisteneceObject;
	}

	@Override
	public void ready() {
		player = getConcreteParent();
		addActionEventListener(DailyQuestActionEventDefines.C2G_QST_GetDailyQuestList);
		addActionEventListener(DailyQuestActionEventDefines.C2G_QST_DailyQuestAccept);
		addActionEventListener(DailyQuestActionEventDefines.C2G_QST_DailyQuestSubmit);
		addActionEventListener(DailyQuestActionEventDefines.C2G_QST_DailyStartLevel);
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
		addInterGameEventListener(PluckMgrComponent.PluckSuccess_GE_ID);
		checkToReset();
		addTimer();
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(DailyQuestActionEventDefines.C2G_QST_GetDailyQuestList);
		removeActionEventListener(DailyQuestActionEventDefines.C2G_QST_DailyQuestAccept);
		removeActionEventListener(DailyQuestActionEventDefines.C2G_QST_DailyQuestSubmit);
		removeActionEventListener(DailyQuestActionEventDefines.C2G_QST_DailyStartLevel);
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
		removeInterGameEventListener(PluckMgrComponent.PluckSuccess_GE_ID);
		removeTimer();
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case DailyQuestActionEventDefines.C2G_QST_GetDailyQuestList:
			handle_QST_GetDailyQuestList((C2G_QST_GetDailyQuestList) event, actionEventId, identity);
			break;
		case DailyQuestActionEventDefines.C2G_QST_DailyQuestAccept:
			handle_QST_DailyQuestAccept((C2G_QST_DailyQuestAccept) event, actionEventId, identity);
			break;
		case DailyQuestActionEventDefines.C2G_QST_DailyQuestSubmit:
			handle_QST_DailyQuestSubmit((C2G_QST_DailyQuestSubmit) event, actionEventId, identity);
			break;
		case DailyQuestActionEventDefines.C2G_QST_DailyStartLevel:
			handle_QST_DailyStartLevel((C2G_QST_DailyStartLevel) event, actionEventId, identity);
			break;
		default:
			break;
		}
		super.handleActionEvent(event);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(Monster.MonsterDead_GE_Id)) { // 杀怪GameEvent
			MonsterDead_GE monsterDead_GE = (MonsterDead_GE) event.getData();
			Monster monster = monsterDead_GE.getMonster();
			List<MGDailyQuest> questList = dailyQuestManager.getCrtQuestList();
			for (MGDailyQuest quest : questList) {
				updataQuestState(quest, monster.getMonsterRef().getId(), 1);
			}
		}
		if (event.isId(PluckMgrComponent.PluckSuccess_GE_ID)) {
			PluckSuccess_GE pluck = (PluckSuccess_GE) event.getData();
			List<ItemPair> itemPairs = pluck.getItemPairs();
			List<MGDailyQuest> questList = dailyQuestManager.getCrtQuestList();
			for (MGDailyQuest quest : questList) {
				for (ItemPair item : itemPairs) {
					updataQuestState(quest, item.getItemRefId(), item.getNumber());
				}
			}
		}
		super.handleGameEvent(event);
	}

	private void handle_QST_GetDailyQuestList(C2G_QST_GetDailyQuestList event, short actionEventId, Identity identity) {
		List<MGDailyQuest> questList = dailyQuestManager.getCrtQuestList();
		for (MGDailyQuest quest : questList) {
			if (quest.getQuestState() != QuestState.AcceptedQuestState) {
				continue;
			}
			if (quest.getDailyQuestCourse().wasCompleted()) {
				quest.setQuestState(QuestState.SubmittableQuestState);
			}
		}
		List<MGDailyQuest> addedDailyQuest = dailyQuestManager.getQuestRefMgr().getAddedDailyQuest(player, questList);
		for (MGDailyQuest addedQuest : addedDailyQuest) {
			dailyQuestManager.addCrtQuest(addedQuest);
		}
		sendDailyQuestList();
	}

	private void recheckDailyQuest() {
		List<MGDailyQuest> crtQuestList = dailyQuestManager.getCrtQuestList();
		MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		int addRingTime = vipComponent.getAddQuestRingTime();
		for (MGDailyQuest dailyQuest : crtQuestList) {
			if (dailyQuest.getQuestState() != QuestState.AcceptedQuestState && dailyQuest.getQuestState() != QuestState.SubmittableQuestState) {
				dailyQuest.questFinish(addRingTime);
			}
		}
	}

	private void handle_QST_DailyQuestAccept(C2G_QST_DailyQuestAccept event, short actionEventId, Identity identity) {
		String acceptQuest = event.getAcceptQuest();
		List<MGDailyQuest> crtQuestList = dailyQuestManager.getAcceptBleList();
		for (MGDailyQuest quest : crtQuestList) {
			if (StringUtils.equals(acceptQuest, quest.getDailyQuestRef().getId())) {
				if (quest.getQuestState() == QuestState.AcceptedQuestState || quest.getQuestState() == QuestState.SubmittableQuestState) {
					logger.error("Quest Already accepted!!!! questRefId:" + acceptQuest + "; player:" + player);
					ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_NOT_FINISH);
					return;
				}
				createQuestCourse(quest);
			}
		}
		sendDailyQuestList();
	}

	private boolean subMoneyByRewardDoubleExp(int rewardLevel, short actionEventId, Identity identity) {
		if (rewardLevel <= 0 || rewardLevel > 2) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_WRONG_MESSAGE);
			return false;
		}
		PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
		if (rewardLevel == 2) {
			if (playMoneyCompoent.getGold() > doubleExpCost) {
				playMoneyCompoent.subGold(doubleExpCost, ItemOptSource.DailyQuest);
			} else {
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_GOLD_NOT_ENOUGHT);
				return false;
			}
		}
		return true;
	}

	private void handle_QST_DailyQuestSubmit(C2G_QST_DailyQuestSubmit event, short actionEventId, Identity identity) {
		String submitQuestId = event.getSubmitQuestId();
		int rewardLevel = event.getRewardLevel();

		List<MGDailyQuest> acceptedList = dailyQuestManager.getAcceptedList();
		for (MGDailyQuest acceptedQuest : acceptedList) {
			if (acceptedQuest.getDailyQuestRef().getId().equals(submitQuestId)) {
				if (acceptedQuest.getQuestState() == QuestState.CompletedQuestState) {
					ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_REWARDED);
					return;
				}
				if (acceptedQuest.getQuestState() != QuestState.SubmittableQuestState) {
					return;
				}
				List<QuestCourseItem> questCourseItemList = acceptedQuest.getDailyQuestCourse().getQuestCourseItemList();
				for (QuestCourseItem questCourseItem : questCourseItemList) {
					if (!questCourseItem.wasCompleted()) {
						questStateUpdate(acceptedQuest, acceptedQuest.getQuestState());
						if (logger.isDebugEnabled()) {
							logger.debug(" =======Quest NOT Finish~! ");
						}
						return;
					}
				}
				if (!subMoneyByRewardDoubleExp(rewardLevel, actionEventId, identity)) {
					return;
				}
				questStateUpdate(acceptedQuest, QuestState.CompletedQuestState);
				// 发奖之后将任务设置为未接，任务等级（星星）清零
				acceptedQuest.takeRewardTo(player, rewardLevel);
				String questRefId = acceptedQuest.getDailyQuestRef().getId();
				MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
				int addRingTime = vipComponent.getAddQuestRingTime();
				MGStatFunctions.dailyQuestStat(player, StatDailyQuest.Finish, questRefId, acceptedQuest.getNowTime(), addRingTime, acceptedQuest.getStartLevel(), "");
				acceptedQuest.questFinish(addRingTime);
				sendDailyQuestList();
			}
		}
	}

	private void handle_QST_DailyStartLevel(C2G_QST_DailyStartLevel event, short actionEventId, Identity identity) {
		String refreshQuestId = event.getQuestId();
		List<MGDailyQuest> crtQuestList = dailyQuestManager.getCrtQuestList();
		PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
		for (MGDailyQuest quest : crtQuestList) {
			if (!refreshQuestId.equals(quest.getDailyQuestRef().getId())) {
				continue;
			}
			byte level = quest.getStartLevel();
			if (level == (byte) MGDailyQuestConfig.Default_Max_DailyQuest_Level) {
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_START_MAX);
				return;
			}
			if (playMoneyCompoent.getGold() < flashStartLevelCost) {
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_GOLD_NOT_ENOUGHT);
				return;
			}
			playMoneyCompoent.subGold(flashStartLevelCost, ItemOptSource.DailyQuest);
			quest.randomStart();
			G2C_QST_DailyStartLevel startLevel = MessageFactory.getConcreteMessage(DailyQuestActionEventDefines.G2C_QST_DailyStartLevel);
			startLevel.setDailyQuest(quest);
			GameRoot.sendMessage(identity, startLevel);
		}
	}

	public void sendDailyQuestList() {
		if (dailyQuestManager.getAcceptedList().size() > 0) {
			G2C_QST_DailyQuestAcceptedList accept = MessageFactory.getConcreteMessage(DailyQuestActionEventDefines.G2C_QST_DailyQuestAcceptedList);
			accept.setAcceptDailyQuestList(dailyQuestManager.getAcceptedList());
			GameRoot.sendMessage(player.getIdentity(), accept);
		}
		if (dailyQuestManager.getAcceptBleList().size() > 0) {
			G2C_QST_DailyQuestVisibleList visible = MessageFactory.getConcreteMessage(DailyQuestActionEventDefines.G2C_QST_DailyQuestVisibleList);
			visible.setQuestList(dailyQuestManager.getAcceptBleList());
			GameRoot.sendMessage(player.getIdentity(), visible);
		}
	}

	private void createQuestCourse(MGDailyQuest quest) {
		String questRefId = quest.getDailyQuestRef().getId();
		Map<Integer, String> acceptList = quest.createQuestCourseItem(player);
		if (acceptList == null) {
			logger.info("player has Not Enought Level to accept quset,player:" + player + "  ,playerLevel:" + player.getLevel() + ",  quest:" + quest.getDailyQuestRef().getId());
			ResultEvent.sendResult(player.getIdentity(), DailyQuestActionEventDefines.C2G_QST_DailyQuestAccept, MMORPGErrorCode.CODE_QUEST_NOT_SUTE_COURSE);
			return;
		}
		if (acceptList.size() <= 0) {
			logger.info("Quset in the last Ring.Can't accept quset,player:" + player + ",  quest:" + quest.getDailyQuestRef().getId());
			ResultEvent.sendResult(player.getIdentity(), DailyQuestActionEventDefines.C2G_QST_DailyQuestAccept, MMORPGErrorCode.CODE_QUEST_LAST_RING);
			return;
		}
		
		quest.setQuestState(QuestState.AcceptedQuestState);
		quest.setNowTime(quest.getNowTime() + 1);
		MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		int addRingTime = vipComponent.getAddQuestRingTime();
		
		for (Entry<Integer, String> entry : acceptList.entrySet()) {
			String monsterRefId = entry.getValue();
			MGStatFunctions.dailyQuestStat(player, StatDailyQuest.Accept, questRefId, quest.getNowTime() + 1, addRingTime, quest.getStartLevel(), monsterRefId);
		}
	}

	private void updataQuestState(MGDailyQuest quest, String courseId, int courseNum) {
		if (quest.isAddToCourse(courseId, courseNum)) {
			if (quest.getDailyQuestCourse().wasCompleted()) {
				questStateUpdate(quest, QuestState.SubmittableQuestState);
			} else {
				G2C_QST_DailyQuestUpdate questUpdate = MessageFactory.getConcreteMessage(DailyQuestActionEventDefines.G2C_QST_DailyQuestUpdate);
				questUpdate.setQuest(quest);
				GameRoot.sendMessage(player.getIdentity(), questUpdate);
			}
		}
	}

	private void questStateUpdate(MGDailyQuest quest, int state) {
		quest.setQuestState(state);
		G2C_QST_DailyStateUpdate stateUpdate = MessageFactory.getConcreteMessage(DailyQuestActionEventDefines.G2C_QST_DailyStateUpdate);
		stateUpdate.setQuestId(quest.getDailyQuestRef().getId());
		stateUpdate.setState(quest.getQuestState());
		GameRoot.sendMessage(player.getIdentity(), stateUpdate);
	}

}
