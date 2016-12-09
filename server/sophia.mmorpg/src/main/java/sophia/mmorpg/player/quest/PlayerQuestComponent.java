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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.gameEvent.MGGameInstanceEnter_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.itemBag.PlayerItemBagComponent;
import sophia.mmorpg.player.itemBag.gameEvent.QuestItemChange_GE;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.event.C2G_QST_GetQuestList;
import sophia.mmorpg.player.quest.event.C2G_QST_QuestAccept;
import sophia.mmorpg.player.quest.event.C2G_QST_QuestInstanceTrans;
import sophia.mmorpg.player.quest.event.C2G_QST_QuestSubmit;
import sophia.mmorpg.player.quest.event.G2C_QST_QuestAcceptedList;
import sophia.mmorpg.player.quest.event.G2C_QST_QuestUpdate;
import sophia.mmorpg.player.quest.event.G2C_QST_QuestVisibleList;
import sophia.mmorpg.player.quest.event.G2C_QST_StateUpdate;
import sophia.mmorpg.player.quest.event.QuestActionEventDefines;
import sophia.mmorpg.player.quest.ref.QuestRef;
import sophia.mmorpg.player.quest.ref.condition.QuestRefCondition;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionItem;
import sophia.mmorpg.player.quest.ref.condition.QuestRefConditionType;
import sophia.mmorpg.player.quest.ref.npc.QuestRefNpc;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;
import sophia.mmorpg.pluck.gameEvent.PluckSuccess_GE;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.logs.StatPlotQuest;
import sophia.mmorpg.utils.RuntimeResult;

public class PlayerQuestComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(PlayerQuestComponent.class);

	private static final String type = "主线任务";

	private final PlayerQuestManager questManager = new PlayerQuestManager();

	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	public static final String MGGameInstanceEnter_GE_Id = MGGameInstanceEnter_GE.class.getSimpleName();

	public static short gameInstanceEnter = QuestChineseOrderDefines.GameInstanceEnter;
	public static short peerageLevelUp = QuestChineseOrderDefines.PeerageLevelUp;
	public static short mountLevelUp = QuestChineseOrderDefines.MountLevelUp;
	public static short gameInstanceFinish = QuestChineseOrderDefines.GameInstanceFinish;
	public static short gameInstanceEverEnter = QuestChineseOrderDefines.GameInstanceEverEnter;
	public static short wingLevelUp = QuestChineseOrderDefines.WingLevelUp;
	public static short buyStoreItem = QuestChineseOrderDefines.BuyStoreItem;
	public static short hasBuffer = QuestChineseOrderDefines.HasBuffer;
	public static short ladder = QuestChineseOrderDefines.Ladder;
	public static short depot = QuestChineseOrderDefines.Depot;

	private PersistenceObject persisteneceObject;

	public PlayerQuestComponent() {
	}

	private Player player;

	public final String getType() {
		return type;
	}

	public final Quest createQuest(String questRefId) {
		Quest ret = GameObjectFactory.getQuest(questRefId);
		if (ret.getQuestRef() == null) {
			logger.error("    create wrong questId.questRefId:" + questRefId);
			return null;
		}
		ret.setQuestState(QuestState.AcceptedQuestState);
		Map<Integer, String> list = ret.createQuestCourseItem(player);
		for (int single : list.keySet()) {
			if (single == QuestRefOrderType.Collect_Order_Type || single == QuestRefOrderType.Loot_Item_Order_Type) {
				int number = ItemFacade.getNumber(player, list.get(single));
				ret.setQuestCourseNum(list.get(single), number, player);
			}
		}
		return ret;
	}

	public PlayerQuestManager getQuestManager() {
		return questManager;
	}

	@Override
	public void ready() {
		player = getConcreteParent();
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
		// addInterGameEventListener(PluckMgrComponent.PluckSuccess_GE_ID);
		addInterGameEventListener(PlayerItemBagComponent.QuestItemChange_GE_Id);
		addInterGameEventListener(ChineseModeQuest_GE_Id);
		addActionEventListener(QuestActionEventDefines.C2G_QST_GetQuestList);
		addActionEventListener(QuestActionEventDefines.C2G_QST_QuestAccept);
		addActionEventListener(QuestActionEventDefines.C2G_QST_QuestSubmit);
		addActionEventListener(QuestActionEventDefines.C2G_QST_QuestInstanceTrans);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
		// removeInterGameEventListener(PluckMgrComponent.PluckSuccess_GE_ID);
		removeInterGameEventListener(PlayerItemBagComponent.QuestItemChange_GE_Id);
		removeInterGameEventListener(ChineseModeQuest_GE_Id);
		removeActionEventListener(QuestActionEventDefines.C2G_QST_GetQuestList);
		removeActionEventListener(QuestActionEventDefines.C2G_QST_QuestAccept);
		removeActionEventListener(QuestActionEventDefines.C2G_QST_QuestSubmit);
		removeActionEventListener(QuestActionEventDefines.C2G_QST_QuestInstanceTrans);
		super.suspend();
	}

	public void setPersisteneceObject(PersistenceObject persisteneceObject) {
		this.persisteneceObject = persisteneceObject;
	}

	public PersistenceObject getPersisteneceObject() {
		return persisteneceObject;
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case QuestActionEventDefines.C2G_QST_GetQuestList:
			handle_QST_GetQuestList((C2G_QST_GetQuestList) event, actionEventId, identity);
			break;
		case QuestActionEventDefines.C2G_QST_QuestAccept:
			handle_QST_QuestAccept((C2G_QST_QuestAccept) event, actionEventId, identity);
			break;
		case QuestActionEventDefines.C2G_QST_QuestSubmit:
			handle_QST_QuestSubmit((C2G_QST_QuestSubmit) event, actionEventId, identity);
			break;
		case QuestActionEventDefines.C2G_QST_QuestInstanceTrans:
			handle_QST_QuestInstanceTrans((C2G_QST_QuestInstanceTrans) event, actionEventId, identity);
			break;
		default:
			break;
		}
		super.handleActionEvent(event);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		Quest quest = questManager.getCrtQuest();
		if (quest.getQuestState() == QuestState.CompletedQuestState) {
			return;
		}
		if (event.isId(PluckMgrComponent.PluckSuccess_GE_ID)) {
			PluckSuccess_GE pluck = (PluckSuccess_GE) event.getData();
			List<ItemPair> itemPairs = pluck.getItemPairs();
			CollectQuestRefOrderItem collectQuest = (CollectQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.Collect_Order_Type);
			if (collectQuest != null) {
				for (ItemPair item : itemPairs) {
					updataQuestState(quest, item.getItemRefId(), item.getNumber(), player);
				}
			}
		}
		if (event.isId(PlayerItemBagComponent.QuestItemChange_GE_Id)) {
			QuestItemChange_GE questItem = (QuestItemChange_GE) event.getData();
			String questItemRefId = questItem.getQuestItemRefId();
			byte optType = questItem.getOptType();
			CollectQuestRefOrderItem pluckRefItem = (CollectQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.Collect_Order_Type);
			if (pluckRefItem != null) {
				String target = pluckRefItem.getCollectItemId();
				if (optType == 0) {
					updateQuestState(quest, target, questItemRefId, 0, player);
				}
			}
			LootItemQuestRefOrderItem lootRefItem = (LootItemQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.Loot_Item_Order_Type);
			if (lootRefItem != null) {
				String target = lootRefItem.getItemRefId();
				if (optType == 0) {
					updateQuestState(quest, target, questItemRefId, 0, player);
				}
			}

			if (optType == 1 && quest.getQuestState() != QuestState.SubmittableQuestState) {
				updataQuestState(quest, questItemRefId, 0, player);
			}
		}
		if (quest.getQuestState() != QuestState.AcceptedQuestState) {
			if (quest.getQuestState() == QuestState.SubmittableQuestState && quest.getQuestCourse().wasCompleted()) {
				questStateUpdate(quest, QuestState.SubmittableQuestState);
			}
			return;
		}
		if (event.isId(Monster.MonsterDead_GE_Id)) { // 杀怪GameEvent
			MonsterDead_GE monsterDead_GE = (MonsterDead_GE) event.getData();
			String monsterRefId = monsterDead_GE.getMonster().getMonsterRef().getId();
			KillMonsterQuestRefOrderItem killQuest = (KillMonsterQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.Kill_Monster_Order_Type);
			if (killQuest != null) {
				updataQuestState(quest, monsterRefId, quest.getCourseNum() + 1, player);
			}
		}
		if (event.isId(ChineseModeQuest_GE_Id)) {
			ChineseModeQuest_GE chineseModeQuest_GE = (ChineseModeQuest_GE) event.getData();
			ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.ChineseMode_String_Value_Order_Type);
			if (chineseMode != null && chineseModeQuest_GE.getType() == ChineseModeQuest_GE.CourseType) {
				if (chineseMode.getOrderEventId() == gameInstanceEnter && chineseModeQuest_GE.getOrderEventId() == gameInstanceEnter) {
					String chineseModeValue = chineseModeQuest_GE.getChineseModeValue();
					String dstSceneRefId = chineseMode.getChineseModeTarget();
					if (StringUtils.equals(chineseModeValue, dstSceneRefId)) {
						List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
						for (QuestCourseItem item : questCourseItemList) {
							ChineseModeStringQuestCourseItem chineseCourse = (ChineseModeStringQuestCourseItem) item;
							chineseCourse.setCompleted(true);
						}
						questStateUpdate(quest, QuestState.SubmittableQuestState);
					}
				}
				if (chineseMode.getOrderEventId() == peerageLevelUp && chineseModeQuest_GE.getOrderEventId() == peerageLevelUp) {
					int number = chineseModeQuest_GE.getNumber();
					updataQuestState(quest, "", number, player);
				}
				if (chineseMode.getOrderEventId() == mountLevelUp && chineseModeQuest_GE.getOrderEventId() == mountLevelUp) {
					int number = chineseModeQuest_GE.getNumber();
					long count = chineseModeQuest_GE.getCount();
					int courseNum = (int) (number * 10 + count);
					updataQuestState(quest, "", courseNum, player);
				}
				if (chineseMode.getOrderEventId() == gameInstanceFinish && chineseModeQuest_GE.getOrderEventId() == gameInstanceFinish) {
					String chineseModeTarget = chineseMode.getChineseModeTarget();
					String dstSceneRefId = chineseModeQuest_GE.getChineseModeTarget();
					if (StringUtils.equals(chineseModeTarget, dstSceneRefId)) {
						List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
						for (QuestCourseItem item : questCourseItemList) {
							ChineseModeStringQuestCourseItem chineseCourse = (ChineseModeStringQuestCourseItem) item;
							chineseCourse.setCompleted(true);
						}
						questStateUpdate(quest, QuestState.SubmittableQuestState);
					}
				}
				if (chineseMode.getOrderEventId() == gameInstanceEverEnter && chineseModeQuest_GE.getOrderEventId() == gameInstanceEverEnter) {
					String chineseModeTarget = chineseMode.getChineseModeTarget();
					String dstSceneRefId = chineseModeQuest_GE.getChineseModeTarget();
					if (StringUtils.equals(chineseModeTarget, dstSceneRefId)) {
						List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
						for (QuestCourseItem item : questCourseItemList) {
							ChineseModeStringQuestCourseItem chineseCourse = (ChineseModeStringQuestCourseItem) item;
							chineseCourse.setCompleted(true);
						}
						questStateUpdate(quest, QuestState.SubmittableQuestState);
					}
				}
				if (chineseMode.getOrderEventId() == wingLevelUp && chineseModeQuest_GE.getOrderEventId() == wingLevelUp) {
					int number = chineseModeQuest_GE.getNumber();
					long count = chineseModeQuest_GE.getCount();
					int courseNum = (int) (number * 10 + count);
					updataQuestState(quest, "", courseNum, player);
				}
				if (chineseMode.getOrderEventId() == buyStoreItem && chineseModeQuest_GE.getOrderEventId() == buyStoreItem) {
					int number = chineseModeQuest_GE.getNumber();
					String itemRefId = chineseModeQuest_GE.getChineseModeTarget();
					updataQuestState(quest, itemRefId, number, player);
				}
				if (chineseMode.getOrderEventId() == hasBuffer && chineseModeQuest_GE.getOrderEventId() == hasBuffer) {
					String chineseModeTarget = chineseMode.getChineseModeTarget();
					String dstSceneRefId = chineseModeQuest_GE.getChineseModeTarget();
					if (StringUtils.equals(chineseModeTarget, dstSceneRefId)) {
						List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
						for (QuestCourseItem item : questCourseItemList) {
							ChineseModeStringQuestCourseItem chineseCourse = (ChineseModeStringQuestCourseItem) item;
							chineseCourse.setCompleted(true);
						}
						questStateUpdate(quest, QuestState.SubmittableQuestState);
					}
				}
				if (chineseMode.getOrderEventId() == ladder && chineseModeQuest_GE.getOrderEventId() == ladder) {
					int number = chineseModeQuest_GE.getNumber();
					updataQuestState(quest, "", number, player);
				}
				if (chineseMode.getOrderEventId() == depot && chineseModeQuest_GE.getOrderEventId() == depot) {
					String chineseModeValue = chineseMode.getChineseModeValue();
					String chineseModeValue2 = chineseModeQuest_GE.getChineseModeValue();
					if (StringUtils.equals(chineseModeValue, chineseModeValue2)) {
						List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
						for (QuestCourseItem item : questCourseItemList) {
							ChineseModeStringQuestCourseItem chineseCourse = (ChineseModeStringQuestCourseItem) item;
							chineseCourse.setCompleted(true);
						}
						questStateUpdate(quest, QuestState.SubmittableQuestState);
					}
				}
			}
		}
		super.handleGameEvent(event);
	}

	private void handle_QST_GetQuestList(C2G_QST_GetQuestList event, short actionEventId, Identity identity) {
		Quest quest = questManager.getCrtQuest();
		if (quest == null) {
			return;
		}
		if (quest.getQuestState() == QuestState.CompletedQuestState) {
			if (!StringUtils.isEmpty(quest.getQuestRef().getNextQuestId())) {
				if (!checkIfEnoughtLevel(quest, identity, actionEventId)) {
					return;
				}
				G2C_QST_QuestVisibleList visible = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestVisibleList);
				visible.setNextQuestID(quest.getQuestRef().getNextQuestId());
				GameRoot.sendMessage(identity, visible);
			}
		} else {
			G2C_QST_QuestAcceptedList accept = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestAcceptedList);
			accept.setCrtQuest(quest);
			GameRoot.sendMessage(identity, accept);
		}
		checkIfHasAcceptNpc(quest);
	}

	private void handle_QST_QuestAccept(C2G_QST_QuestAccept event, short actionEventId, Identity identity) {
		Quest crtQuest = questManager.getCrtQuest();
		if (crtQuest.getQuestState() != QuestState.CompletedQuestState) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_NOT_FINISH);
			return;
		}
		String acceptQuest = event.getAcceptQuest();
		String nextQuestId = questManager.getCrtQuest().getQuestRef().getNextQuestId();
		if (StringUtils.equals(acceptQuest, nextQuestId) && acceptQuest.startsWith("quest_")) {
			Quest quest = createQuest(acceptQuest);
			if (!quest.canAcceptQuest(player)) {
				return;
			}
			G2C_QST_QuestAcceptedList accept = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestAcceptedList);
			accept = acceptQuest(quest);
			GameRoot.sendMessage(identity, accept);
			chineseQuestGEAction(quest);
		} else {
			logger.error("WRONG Accept QuestID !!!!  QuestID : " + acceptQuest + "; crtQuestID:" + questManager.getCrtQuest().getQuestRef().getId() + "; player:" + player);
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_WRONG_ID);
			return;
		}

	}

	private void handle_QST_QuestSubmit(C2G_QST_QuestSubmit event, short actionEventId, Identity identity) {
		String questId = event.getQuestId();
		Quest quest = questManager.getCrtQuest();
		if (!StringUtils.equals(questId, quest.getQuestRef().getId())) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_WRONG_QUEST);
			return;
		}
		if (quest.getQuestState() == QuestState.CompletedQuestState) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_REWARDED);
			return;
		}
		if (quest.getQuestState() != QuestState.SubmittableQuestState) {
			return;
		}
		if (!questWasCompleted(quest)) {
			questStateUpdate(quest, quest.getQuestState());
			logger.debug("This quest Hasn't finish!");
			return;
		}
		if (getQuestRefOrderItem(quest, QuestRefOrderType.Collect_Order_Type) != null) {
			if (!hasEnoughtPluckItem(quest, actionEventId, identity)) {
				return;
			}

		}
		RuntimeResult runtimeResult = quest.takeRewardTo(player);
		if (runtimeResult.isError()) {
			int code = runtimeResult.getApplicationCode();
			ResultEvent.sendResult(player.getIdentity(), actionEventId, code);
			return;
		}
		questStateUpdate(quest, QuestState.CompletedQuestState);
		// sendQuestRewardGE(quest); 完成任务发送爵位获取事件，暂时屏蔽
		removePluckItem(quest, actionEventId, identity);

		QuestRef questRef = questManager.nextQuest().getQuestRef();
		if (questRef == null) {
			logger.debug("Do not has next PlotQuest.");
			return;
		}
		G2C_QST_QuestVisibleList visibleList = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestVisibleList);
		visibleList.setNextQuestID(questManager.nextQuest().getQuestRef().getId());
		GameRoot.sendMessage(identity, visibleList);
		checkIfHasAcceptNpc(quest);
	}

	/**
	 * 完成任务发送爵位获取事件，暂时屏蔽 public void sendQuestRewardGE(Quest quest) {
	 * ChineseModeStringQuestRefOrderItem chineseMode =
	 * (ChineseModeStringQuestRefOrderItem) getQuestRefOrderItem(quest,
	 * QuestRefOrderType.ChineseMode_String_Value_Order_Type); if (chineseMode
	 * != null) { if (chineseMode.getOrderEventId() ==
	 * QuestChineseOrderDefines.PeerageLevelUp) { ChineseModeQuestReward_GE
	 * chineseModeQuestReward_GE = new ChineseModeQuestReward_GE();
	 * chineseModeQuestReward_GE
	 * .setOrderEventId(QuestChineseOrderDefines.PeerageLevelUp);
	 * GameEvent<ChineseModeQuestReward_GE> chinese =
	 * GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuestReward_GE);
	 * sendGameEvent(chinese, player.getId()); } } }
	 */

	private void handle_QST_QuestInstanceTrans(C2G_QST_QuestInstanceTrans event, short actionEventId, Identity identity) {
		Quest quest = questManager.getCrtQuest();
		String questId = event.getQuestId();
		if (!StringUtils.equals(questId, quest.getQuestRef().getId())) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_WRONG_ID);
			return;
		}
		if (quest.getQuestState() != QuestState.AcceptedQuestState) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_WRONG_QUEST);
			return;
		}
		chineseQuestGEAction(quest);
	}

	public void chineseQuestGEAction(Quest quest) {
		ChineseModeStringQuestRefOrderItem chineseMode = (ChineseModeStringQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.ChineseMode_String_Value_Order_Type);
		if (chineseMode != null) {
			short orderEventId = chineseMode.getOrderEventId();
			if (orderEventId == gameInstanceEnter) {
				String sceneRefId = chineseMode.getChineseModeValue();
				String gameInstanceRefId = chineseMode.getChineseModeTarget();
				MGGameInstanceEnter_GE instanceLeave_GE = new MGGameInstanceEnter_GE(gameInstanceRefId, sceneRefId);
				GameEvent<MGGameInstanceEnter_GE> gameInstanceEnter = GameEvent.getInstance(MGGameInstanceEnter_GE_Id, instanceLeave_GE);
				player.handleGameEvent(gameInstanceEnter);
				GameEvent.pool(gameInstanceEnter);
			} else if (orderEventId == gameInstanceEverEnter) {
				ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
				chineseModeQuest_GE.setType(ChineseModeQuest_GE.AcceptType);
				chineseModeQuest_GE.setOrderEventId(orderEventId);
				chineseModeQuest_GE.setChineseModeTarget(chineseMode.getChineseModeTarget());
				GameEvent<ChineseModeQuest_GE> chineseModeGE = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
				player.handleGameEvent(chineseModeGE);
				GameEvent.pool(chineseModeGE);
			} else if (orderEventId == hasBuffer) {
				ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
				chineseModeQuest_GE.setType(ChineseModeQuest_GE.AcceptType);
				chineseModeQuest_GE.setOrderEventId(orderEventId);
				chineseModeQuest_GE.setChineseModeTarget(chineseMode.getChineseModeTarget());
				GameEvent<ChineseModeQuest_GE> chineseModeGE = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
				player.handleGameEvent(chineseModeGE);
				GameEvent.pool(chineseModeGE);
			} else if (orderEventId == peerageLevelUp || orderEventId == mountLevelUp || orderEventId == wingLevelUp || orderEventId == ladder || orderEventId == depot) {
				ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
				chineseModeQuest_GE.setType(ChineseModeQuest_GE.AcceptType);
				chineseModeQuest_GE.setOrderEventId(orderEventId);
				GameEvent<ChineseModeQuest_GE> chineseModeGE = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
				player.handleGameEvent(chineseModeGE);
				GameEvent.pool(chineseModeGE);
			}
		}
	}

	public QuestRefOrderItem getQuestRefOrderItem(Quest quest, byte type) {
		Set<QuestRefOrderItem> orderSet = quest.getQuestRef().getOrder().getQuestRefOrder(player);
		for (QuestRefOrderItem questRefOrderItem : orderSet) {
			byte courseItemType = questRefOrderItem.getOrderType();
			if (courseItemType == type) {
				return questRefOrderItem;
			}
		}
		return null;
	}

	public boolean hasEnoughtPluckItem(Quest quest, short actionEventId, Identity identity) {
		CollectQuestRefOrderItem collect = (CollectQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.Collect_Order_Type);
		if (collect == null) {
			return false;
		}
		String target = collect.getCollectItemId();
		int num = collect.getNumber();
		if (ItemFacade.getNumber(player, target) < num) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_PLUCK_NOT_ENOUGHT);
			return false;
		}
		return true;
	}

	public void removePluckItem(Quest quest, short actionEventId, Identity identity) {
		CollectQuestRefOrderItem collect = (CollectQuestRefOrderItem) getQuestRefOrderItem(quest, QuestRefOrderType.Collect_Order_Type);
		if (collect != null) {
			String target = collect.getCollectItemId();
			int num = collect.getNumber();
			if (ItemFacade.getNumber(player, target) >= num) {
				boolean ret = ItemFacade.removeItem(player, target, num, true, ItemOptSource.Quest);
				if (ret == false) {
					logger.error("remove Item error!");
				}
			} else {
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_PLUCK_NOT_ENOUGHT);
			}
		}
	}

	public boolean questWasCompleted(Quest quest) {
		boolean ret = false;
		List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
		for (QuestCourseItem questCourseItem : questCourseItemList) {
			if (!questCourseItem.wasCompleted()) {
				return ret;
			}
		}
		ret = true;
		return ret;
	}

	private void updataQuestState(Quest quest, String courseId, int courseNum, Player player) {
		if (quest.setQuestCourseNum(courseId, courseNum, player)) {
			if (quest.getQuestCourse().wasCompleted()) {
				questStateUpdate(quest, QuestState.SubmittableQuestState);
			} else {
				questCourseUpdata(quest);
			}
		}
	}

	private void updateQuestState(Quest quest, String target, String questItemRefId, int num, Player player) {
		if (quest.getQuestState() == QuestState.CompletedQuestState) {
			return;
		}
		if (!StringUtils.equals(target, questItemRefId)) {
			return;
		}
		quest.setQuestCourseNum(questItemRefId, num, player);
		if (quest.getQuestCourse().wasCompleted()) {
			questStateUpdate(quest, QuestState.SubmittableQuestState);
		} else {
			questStateUpdate(quest, QuestState.AcceptedQuestState);

		}
		questCourseUpdata(quest);
	}

	public void questStateUpdate(Quest quest, int state) {
		quest.setQuestState(state);
		G2C_QST_StateUpdate stateUpdate = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_StateUpdate);
		stateUpdate.setQuestId(quest.getQuestRef().getId());
		stateUpdate.setState(quest.getQuestState());
		GameRoot.sendMessage(player.getIdentity(), stateUpdate);
		questManager.setCrtQuest(quest);
	}

	public void questCourseUpdata(Quest quest) {
		G2C_QST_QuestUpdate questUpdate = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestUpdate);
		questUpdate.setQuest(quest);
		GameRoot.sendMessage(player.getIdentity(), questUpdate);
	}

	public void checkIfHasAcceptNpc(Quest quest) {
		// 检测下一个任务是否有接受任务npc，如果没有，当前任务移除，下一任务直接塞到玩家身上
		String nextQuestId = quest.getQuestRef().getNextQuestId();
		if (StringUtils.isEmpty(nextQuestId) || !nextQuestId.startsWith("quest_")) {
			logger.info("Do not has next PlotQuest. now questRefId:" + quest.getQuestRef().getId() + "; player:" + player);
			return;
		}
		Quest ret = GameObjectFactory.getQuest(nextQuestId);
		QuestRefNpc refNpcData = ret.getQuestRef().getNpc();
		if (refNpcData == null || (StringUtils.isEmpty(refNpcData.getQuestRefNpcData(player).getAcceptedNpcRefId()))) {
			G2C_QST_QuestAcceptedList accept = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestAcceptedList);
			Quest nextQuest = createQuest(questManager.nextQuest().getQuestRef().getId());
			for (QuestRefCondition condition : nextQuest.getQuestRef().getConditionSet()) {
				if (condition.getConditionType() == QuestRefConditionType.Accept_Condition_Type) {
					for (QuestRefConditionItem item : condition.getConditionItems(player)) {
						if (player.getExpComponent().getLevel() >= item.getNumber()) {
							accept = acceptQuest(nextQuest);
							GameRoot.sendMessage(player.getIdentity(), accept);
						}
					}
				}
			}
		}
	}

	private G2C_QST_QuestAcceptedList acceptQuest(Quest quest) {
		G2C_QST_QuestAcceptedList accept = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestAcceptedList);
		quest.setQuestState(quest.getQuestState());
		accept.setCrtQuest(quest);
		questManager.setCrtQuest(quest);
		StatFunctions.plotQuest(player, StatPlotQuest.Accept, quest.getQuestRef().getId());
		return accept;
	}

	private boolean checkIfEnoughtLevel(Quest quest, Identity identity, short actionEventId) {
		boolean ret = true;
		for (QuestRefCondition condition : quest.getQuestRef().getConditionSet()) {
			if (condition.getConditionType() == QuestRefConditionType.Visiable_Condition_Type) {
				for (QuestRefConditionItem item : condition.getConditionItems(player)) {
					if (player.getExpComponent().getLevel() < item.getNumber()) {
						logger.debug("== Player is not enought Level to Visible Quest ! ==  ");
						ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_QUEST_NO_LEVEL_VISIT);
						ret = false;
					}
				}
			}
		}
		return ret;
	}
}
