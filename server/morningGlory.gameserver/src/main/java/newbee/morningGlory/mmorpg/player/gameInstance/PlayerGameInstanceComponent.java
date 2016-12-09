/**
 * 
 */
package newbee.morningGlory.mmorpg.player.gameInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstance;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceOpeningTimeType;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceRestore;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceSystemComponent;
import newbee.morningGlory.mmorpg.gameInstance.event.C2G_GameInstanceEnter;
import newbee.morningGlory.mmorpg.gameInstance.event.C2G_GameInstanceEnterNextLayer;
import newbee.morningGlory.mmorpg.gameInstance.event.C2G_GameInstanceLeave;
import newbee.morningGlory.mmorpg.gameInstance.event.C2G_GameInstanceList;
import newbee.morningGlory.mmorpg.gameInstance.event.G2C_GameInstanceLeave;
import newbee.morningGlory.mmorpg.gameInstance.event.G2C_GameInstanceList;
import newbee.morningGlory.mmorpg.gameInstance.event.G2C_Instance_LayerFinish;
import newbee.morningGlory.mmorpg.gameInstance.event.G2C_Instance_QuestReward;
import newbee.morningGlory.mmorpg.gameInstance.event.GameInstanceEventDefines;
import newbee.morningGlory.mmorpg.gameInstance.quest.MGGameInstanceQuestRef;
import newbee.morningGlory.mmorpg.gameInstance.quest.MGGameInstanceQuestRefMgr;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.MGGameInstanceQuest;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.C2G_Reward_GameInstanceQuest;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.C2G_Show_GameInstanceQuestReward;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.G2C_Instance_QuestAccepted;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.G2C_Instance_QuestFinish;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.G2C_Instance_QuestUpdate;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatGameInstance;
import newbee.morningGlory.stat.logs.StatGameInstanceQuest;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.core.linyuesheng.LinYueShengModeCondition;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRefMgr;
import sophia.mmorpg.gameInstance.GameInstanceState;
import sophia.mmorpg.gameInstance.OpenTimeData;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.Bricks;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.MGGameInstanceEnter_GE;
import sophia.mmorpg.player.gameEvent.PlayerSwitchScene_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;

public final class PlayerGameInstanceComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerGameInstanceComponent.class);

	public static final String Tag = "PlayerGameInstanceComponent";

	private static final String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private static final String PlayerSwitchScene_GE_Id = PlayerSwitchScene_GE.class.getSimpleName();
	private static final String MGGameInstanceEnter_GE_Id = MGGameInstanceEnter_GE.class.getSimpleName();
	private static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();
	private static final String EnterWorld_GE_Id = EnterWorld_GE.class.getSimpleName();
	private long questCompletedTime = System.currentTimeMillis();
	/** 以下几个数据不需要清除，副本掉线需要用到 */
	private String crtGameInstanceId;
	// gameInstanceRefId, GameInstanceId
	private Map<String, String> refIdToGameInstanceId = new HashMap<>();
	// 副本任务
	private List<MGGameInstanceQuest> acceptQuest = new ArrayList<>();

	@Override
	public void ready() {
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
		addInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		addInterGameEventListener(PlayerSwitchScene_GE_Id);
		addInterGameEventListener(MGGameInstanceEnter_GE_Id);
		addInterGameEventListener(ChineseModeQuest_GE_Id);
		addInterGameEventListener(EnterWorld_GE_Id);
		addActionEventListener(GameInstanceEventDefines.C2G_GameInstanceList);
		addActionEventListener(GameInstanceEventDefines.C2G_GameInstanceEnter);
		addActionEventListener(GameInstanceEventDefines.C2G_GameInstanceLeave);
		addActionEventListener(GameInstanceEventDefines.C2G_GameInstanceEnterNextLayer);
		addActionEventListener(GameInstanceEventDefines.C2G_Reward_GameInstanceQuest);
		addActionEventListener(GameInstanceEventDefines.C2G_Show_GameInstanceQuestReward);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
		removeInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		removeInterGameEventListener(PlayerSwitchScene_GE_Id);
		removeInterGameEventListener(MGGameInstanceEnter_GE_Id);
		removeInterGameEventListener(ChineseModeQuest_GE_Id);
		removeInterGameEventListener(EnterWorld_GE_Id);
		removeActionEventListener(GameInstanceEventDefines.C2G_GameInstanceList);
		removeActionEventListener(GameInstanceEventDefines.C2G_GameInstanceEnter);
		removeActionEventListener(GameInstanceEventDefines.C2G_GameInstanceLeave);
		removeActionEventListener(GameInstanceEventDefines.C2G_GameInstanceEnterNextLayer);
		removeActionEventListener(GameInstanceEventDefines.C2G_Reward_GameInstanceQuest);
		removeActionEventListener(GameInstanceEventDefines.C2G_Show_GameInstanceQuestReward);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("MGGameInstanceSystemComponent handleEvent " + actionEvent);
		}

		switch (actionEvent.getActionEventId()) {
		case GameInstanceEventDefines.C2G_GameInstanceList: {
			handle_C2G_GameInstanceList((C2G_GameInstanceList) actionEvent);
			break;
		}
		case GameInstanceEventDefines.C2G_GameInstanceEnter: {
			handle_C2G_GameInstanceEnter((C2G_GameInstanceEnter) actionEvent);
			break;
		}
		case GameInstanceEventDefines.C2G_GameInstanceEnterNextLayer: {
			handle_C2G_GameInstanceEnterNextLayer((C2G_GameInstanceEnterNextLayer) actionEvent);
			break;
		}
		case GameInstanceEventDefines.C2G_GameInstanceLeave: {
			handle_C2G_GameInstanceLeave((C2G_GameInstanceLeave) actionEvent);
			break;
		}
		case GameInstanceEventDefines.C2G_Reward_GameInstanceQuest: {
			handle_Reward_GameInstanceQuest((C2G_Reward_GameInstanceQuest) actionEvent);
			break;
		}
		case GameInstanceEventDefines.C2G_Show_GameInstanceQuestReward: {
			handle_Show_GameInstanceQuestReward((C2G_Show_GameInstanceQuestReward) actionEvent);
			break;
		}
		default:
			break;
		}
	}

	private void handle_Show_GameInstanceQuestReward(C2G_Show_GameInstanceQuestReward actionEvent) {
		showRewardData();		
	}

	private void handle_Reward_GameInstanceQuest(C2G_Reward_GameInstanceQuest actionEvent) {
		rewardAndClearData();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (logger.isDebugEnabled()) {
			logger.debug("handleGameEvent");
		}

		if (event.isId(Monster.MonsterDead_GE_Id)) {
			monsterDeadGE(event);
		} else if (event.isId(PlayerManager.LeaveWorld_GE_Id)) {
			leaveWorldGE(event);
		} else if (event.isId(EnterWorld_GE_Id)) {
			enterWorldGE(event);
		} else if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			String sceneRefId = MGPropertyAccesser.getSceneRefId(getConcreteParent().getProperty());
			AbstractGameSceneRef sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
			if (sceneRef.getType() == SceneRef.FuBen) {
				// String
				if (getCrtGameInstanceId() == null) {
					return;
				}
				MGGameInstance gameInstance = (MGGameInstance) getGameInstanceMgr().getGameInstace(getCrtGameInstanceId());
				G2C_Instance_QuestAccepted acceptedQuest = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_Instance_QuestAccepted);
				acceptedQuest.setAcceptQuest(this.acceptQuest);
				acceptedQuest.setGameInstanceId(gameInstance.getGameInstanceRef().getId());
				GameRoot.sendMessage(getConcreteParent().getIdentity(), acceptedQuest);
			}
		} else if (event.isId(PlayerSwitchScene_GE_Id)) {
			PlayerSwitchScene_GE playerSwitchScene_GE = (PlayerSwitchScene_GE) event.getData();
			AbstractGameSceneRef fromeSceneRef = playerSwitchScene_GE.getFromScene().getRef();
			AbstractGameSceneRef dstSceneRef = playerSwitchScene_GE.getDstScene().getRef();
			if (fromeSceneRef.getType() == SceneRef.FuBen && dstSceneRef.getType() != SceneRef.FuBen) {
				GameInstance gameInstance = getGameInstanceMgr().getGameInstace(getCrtGameInstanceId());
				leaveGameInstance(gameInstance);
				sendLeaveGameInstanceMsgToClient();
			}
		} else if (event.isId(MGGameInstanceEnter_GE_Id)) {
			MGGameInstanceEnter_GE gameInstanceEnter_GE = (MGGameInstanceEnter_GE) event.getData();
			String gameInstanceRefId = gameInstanceEnter_GE.getGameInstanceRefId();
			checkGameInstanceOpen();
			String instanceId = refIdToGameInstanceId.get(gameInstanceRefId);
			gameInstanceEnter(instanceId, getConcreteParent().getIdentity(), GameInstanceEventDefines.C2G_GameInstanceEnter);

			sendChineseModeQuest_GE(QuestChineseOrderDefines.GameInstanceEnter, "", gameInstanceRefId);
		} else if (event.isId(ChineseModeQuest_GE_Id)) {
			ChineseModeQuest_GE chineseModeQuest_GE = (ChineseModeQuest_GE) event.getData();
			if (chineseModeQuest_GE.getType() == ChineseModeQuest_GE.AcceptType && chineseModeQuest_GE.getOrderEventId() == QuestChineseOrderDefines.GameInstanceEverEnter) {
				checkEverEnterGameInstance(chineseModeQuest_GE.getChineseModeTarget());
			}
		}
		super.handleGameEvent(event);
	}

	private void monsterDeadGE(GameEvent<?> event) {
		// 只处理副本的怪
		MonsterDead_GE monsterDead_GE = (MonsterDead_GE) event.getData();
		Monster monster = monsterDead_GE.getMonster();
		if (monster == null) {
			return;
		}

		if (monster.getCrtScene().getRef().getType() != SceneRef.FuBen) {
			return;
		}

		Player player = getConcreteParent();
		if (player.getCrtScene().getRef().getType() != SceneRef.FuBen) {
			return;
		}

		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(this.crtGameInstanceId);
		if (gameInstance == null) {
			logger.error("monsterDead error gameInstance=" + gameInstance + ", player=" + player);
			return;
		}

		logger.debug("副本的怪物死亡");

		String monsterRefId = monster.getMonsterRef().getId();
		gameInstance.addKillRecord(player.getId(), monsterRefId, (short) 1);
		sendQuestInfo(monsterRefId);
		checkIfOutOfMonster(gameInstance);
		if (checkLayerFinish()) {
			// rewardAndClearData(FINISH_ALL_QUEST);
			G2C_Instance_LayerFinish layerFinish = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_Instance_LayerFinish);
			GameRoot.sendMessage(player.getIdentity(), layerFinish);
			sendChineseModeQuest_GE(QuestChineseOrderDefines.GameInstanceFinish, gameInstance.getGameInstanceRef().getId(), "");
		}
	}

	private void leaveWorldGE(GameEvent<?> event) {
		Player player = getConcreteParent();
		if (logger.isDebugEnabled()) {
			logger.debug("收到玩家离开世界事件, player=" + player);
		}

		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(getCrtGameInstanceId());
		if (gameInstance != null && gameInstance.isMultiPlayerGameInstance()) {
			return;
		}
		if (gameInstance == null || player.getCrtScene().getRef().getType() != SceneRef.FuBen) {
			leaveWorldAndClearGameInstance();
			return;
		}
		// 把自己从副本玩家列表中移除
		gameInstance.getPlayerCollection().remove(getConcreteParent());
		// 玩家在副本中掉线，则走副本掉线流程
		MGGameInstanceRestore mgGameInstanceRestore = new MGGameInstanceRestore();
		mgGameInstanceRestore.setAcceptQuest(acceptQuest);
		mgGameInstanceRestore.setCurrentGameInstanceId(crtGameInstanceId);
		mgGameInstanceRestore.setRefIdToGameInstanceId(refIdToGameInstanceId);
		getGameInstanceMgr().addGameInstanceCache(player, mgGameInstanceRestore);
		if (logger.isDebugEnabled()) {
			logger.debug("玩家在副本中下线，将副本实例移入缓存 currentGameInstanceId=" + getCrtGameInstanceId() + ", player=" + player);
		}
	}

	private void enterWorldGE(GameEvent<?> event) {
		Player player = getConcreteParent();
		String sceneRefId = MGPropertyAccesser.getSceneRefId(player.getProperty());
		AbstractGameSceneRef sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		if (sceneRef.getType() != SceneRef.FuBen) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("玩家上线,恢复缓存中的gameinstane实例,player=" + player);
		}

		GameInstanceMgr gameInstanceMgr = getGameInstanceMgr();
		MGGameInstanceRestore mgGameInstanceCache = gameInstanceMgr.removeGameInstanceCache(player);
		if (mgGameInstanceCache == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("副本实例过时，已被消除， player=" + player);
			}

			return;
		}

		String currentGameInstanceId = mgGameInstanceCache.getCurrentGameInstanceId();
		GameInstance gameInstance = gameInstanceMgr.getGameInstace(currentGameInstanceId);
		gameInstance.getPlayerCollection().add(player);
		setCrtGameInstanceId(currentGameInstanceId);
		setAcceptQuest(mgGameInstanceCache.getAcceptQuest());
		refIdToGameInstanceId = mgGameInstanceCache.getRefIdToGameInstanceId();

		if (null == getCrtGameInstanceId()) {
			player.goHome();
		}
	}

	private void sendQuestInfo(String monsterRefId) {
		for (MGGameInstanceQuest quest : acceptQuest) {
			if (quest.isAddToCourse(monsterRefId)) {
				if (quest.getQuestCourse().wasCompleted() && QuestState.SubmittableQuestState != quest.getQuestState()) {
					if (logger.isDebugEnabled()) {
						logger.debug("通知客户端副本任务完成");
					}
					quest.setQuestState(QuestState.SubmittableQuestState);
					questCompletedTime = System.currentTimeMillis();
					G2C_Instance_QuestFinish stateUpdate = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_Instance_QuestFinish);
					stateUpdate.setQuestId(quest.getQuestRef().getId());
					stateUpdate.setRewardType(quest.getQuestRef().getRewardType());
					GameRoot.sendMessage(getConcreteParent().getIdentity(), stateUpdate);
					MGStatFunctions.gameInstanceQuestStat(getConcreteParent(), StatGameInstanceQuest.Finish, quest.getQuestRef().getId(), quest.getGameInstaceId(),
							quest.getGameSceneRefId());
					if (quest.getQuestRef().getRewardType() == 1) {
						List<ItemPair> rewardItemList = new ArrayList<>();
						rewardItemList.addAll(quest.takeRewardTo(getConcreteParent()));
						if (rewardItemList.size() > 0) {
							RuntimeResult runtimeResult = RuntimeResult.OK();
							runtimeResult = ItemFacade.addItem(getConcreteParent(), rewardItemList, ItemOptSource.GameInstanceQuest);
							if (runtimeResult.getCode() != 1) {
								String content = Bricks.getContents("system_prompt_config_10");
								String json = (new Gson()).toJson(rewardItemList);
								MailMgr.sendMailById(getConcreteParent().getId(), content, Mail.gonggao, json, 0, 0, 0);
							}
						}
						MGStatFunctions.gameInstanceQuestStat(getConcreteParent(), StatGameInstanceQuest.Reward, quest.getQuestRef().getId(), quest.getGameInstanceRefId(),
								quest.getGameSceneRefId());
					}
				} else {
					G2C_Instance_QuestUpdate questUpdate = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_Instance_QuestUpdate);
					questUpdate.setQuest(quest);
					GameRoot.sendMessage(getConcreteParent().getIdentity(), questUpdate);
				}
			}
		}
	}

	private void sendChineseModeQuest_GE(short orderEventId, String target, String value) {
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setOrderEventId(orderEventId);
		chineseModeQuest_GE.setChineseModeTarget(target);
		chineseModeQuest_GE.setChineseModeValue(value);
		GameEvent<ChineseModeQuest_GE> chinese = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		getConcreteParent().handleGameEvent(chinese);
		GameEvent.pool(chinese);
	}

	private void handle_C2G_GameInstanceEnterNextLayer(C2G_GameInstanceEnterNextLayer event) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_GameInstanceLeave");
		}

		String gameInstanceId = this.crtGameInstanceId;
		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(gameInstanceId);
		if (gameInstance == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("gameInstanceId=" + gameInstanceId + " gameInstance is Null");
			}
			return;
		}

		// 检查玩家是否可以进入
		int errorCode = gameInstance.nextLayer();
		if (errorCode != (int) MGGameInstance.OK) {
			// 不能进入副本下一层
			if (logger.isDebugEnabled()) {
				logger.debug("不能进入副本下一层:" + event.getIdentity().getId());
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), errorCode);
			return;
		}

	}

	private void handle_C2G_GameInstanceLeave(C2G_GameInstanceLeave event) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_GameInstanceLeave");
		}

		String gameInstanceId = getCrtGameInstanceId();
		if (Strings.isNullOrEmpty(gameInstanceId)) {
			Player player = getConcreteParent();
			logger.error("handle_C2G_GameInstanceLeave error, not in gameInstance " + player);
			// 容错处理，对于客户端已不在副本中，但是发送离开副本的，则给客户端返回场景切换协议
			String sceneRefId = player.getCrtScene().getRef().getId();
			int x = player.getCrtPosition().getX();
			int y = player.getCrtPosition().getY();
			player.getPlayerSceneComponent().sendSceneSwitchMessageToClient(sceneRefId, x, y);
			return;
		}

		// rewardAndClearData(FINISH_ALL_QUEST);
		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(gameInstanceId);
		goBackComeFromScene(gameInstance);
	}

	private void handle_C2G_GameInstanceEnter(C2G_GameInstanceEnter event) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_GameInstanceEnter");
		}

		String gameInstanceId = event.getGameInstanceId();
		gameInstanceEnter(gameInstanceId, event.getIdentity(), event.getActionEventId());
	}

	private void gameInstanceEnter(String gameInstanceId, Identity identity, short eventId) {
		if (this.refIdToGameInstanceId == null || !this.refIdToGameInstanceId.containsValue(gameInstanceId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("不能进入副本:" + identity.getId());
			}
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_GAME_INSTANCE_NOT_OPEN);
			return;
		}

		// FIXME, 临时代码，校验玩家是否实际在副本里
		if (getConcreteParent().getCrtScene().getRef().getType() != SceneRef.FuBen && !Strings.isNullOrEmpty(this.crtGameInstanceId)) {
			GameInstance gameInstance = getGameInstanceMgr().getGameInstace(gameInstanceId);
			getGameInstanceMgr().removeGameInstance(gameInstance);
			setCrtGameInstanceId("");
		}

		if (!Strings.isNullOrEmpty(this.crtGameInstanceId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("已经进入副本:" + identity.getId() + ",crtGameInstanceId:" + crtGameInstanceId);
			}
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_GAME_INSTANCE_ALREAD_IN);
			return;
		}

		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(gameInstanceId);
		// 检查玩家是否可以进入
		if (gameInstance == null) {
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_GAME_INSTANCE_NOT_EXIST);
			return;
		}
		RuntimeResult open = gameInstance.open();
		if (open.isError()) {
			ResultEvent.sendResult(identity, eventId, open.getApplicationCode());
			return;
		}
	}

	public boolean gameMultiInstanceEnter(Player player, String gameInstanceId, Identity identity, short eventId) {
		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(gameInstanceId);
		// 检查玩家是否可以进入
		if (gameInstance == null) {
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_GAME_INSTANCE_NOT_EXIST);
			return false;
		}
		if (!gameInstance.getPlayerCollection().contains(player)) {
			gameInstance.addPlayer(player);
		}
		if (this.refIdToGameInstanceId == null || !this.refIdToGameInstanceId.containsValue(gameInstanceId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("不能进入副本:" + identity.getId());
			}
			ResultEvent.sendResult(identity, eventId, MGErrorCode.CODE_GAME_INSTANCE_NOT_OPEN);
			return false;
		}
		// FIXME, 临时代码，校验玩家是否实际在副本里
		if (getConcreteParent().getCrtScene().getRef().getType() == SceneRef.FuBen) {
			return false;
		}

		RuntimeResult open = gameInstance.enter(player);
		if (open.isError()) {
			ResultEvent.sendResult(identity, eventId, open.getApplicationCode());
			return false;
		}
		return true;
	}

	private void handle_C2G_GameInstanceList(C2G_GameInstanceList event) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_GameInstanceList");
		}

		Player player = getConcreteParent();

		checkGameInstanceOpen();

		G2C_GameInstanceList response = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_GameInstanceList);

		List<String> gameInstanceRefIdList = GameInstanceRef.getGameInstanceRefIdList();
		String[] crtGameInstanceRefIds = new String[gameInstanceRefIdList.size()];
		String[] crtGameInstanceIds = new String[gameInstanceRefIdList.size()];
		int[] countsInDays = new int[gameInstanceRefIdList.size()];
		int[] countsInWeeks = new int[gameInstanceRefIdList.size()];
		int i = 0;
		for (String gameInstanceRefId : gameInstanceRefIdList) {
			GameInstance gameInstance = getGameInstanceMgr().getGameInstace(refIdToGameInstanceId.get(gameInstanceRefId));
			GameInstanceRef gameInstanceRef = (GameInstanceRef) GameRoot.getGameRefObjectManager().getManagedObject(gameInstanceRefId);
			if (gameInstance == null) {
				crtGameInstanceRefIds[i] = gameInstanceRef.getId();
				crtGameInstanceIds[i] = "";
				countsInDays[i] = 0;
				countsInWeeks[i] = 0;
			} else {
				int countsInDay = getGameInstanceMgr().getScheduleManager().getInstanceRecordInDay(player, gameInstanceRef.getId());// 返回当天完成副本次数
				int countsInWeek = getGameInstanceMgr().getScheduleManager().getInstanceRecordInWeek(player, gameInstanceRef.getId());// 返回当周完成副本次数
				crtGameInstanceRefIds[i] = gameInstanceRef.getId();
				crtGameInstanceIds[i] = refIdToGameInstanceId.get(gameInstanceRefId);
				MGPlayerVipComponent playerVipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
				int enterGameInstanceCount = playerVipComponent.getEnterGameInstanceCount();
				countsInDays[i] = gameInstanceRef.getCountsADay(player, enterGameInstanceCount) - countsInDay;
				countsInWeeks[i] = gameInstanceRef.getCountsAWeek(player) - countsInWeek;
			}
			i++;
		}
		response.setCountsInDays(countsInDays);
		response.setCountsInWeeks(countsInWeeks);
		response.setCrtGameInstanceIds(crtGameInstanceIds);
		response.setCrtGameInstanceRefIds(crtGameInstanceRefIds);
		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}

		GameRoot.sendMessage(player.getIdentity(), response);
	}

	private void checkEverEnterGameInstance(String gameInstanceRefId) {
		Player player = getConcreteParent();
		GameInstanceRef gameInstanceRef = (GameInstanceRef) GameRoot.getGameRefObjectManager().getManagedObject(gameInstanceRefId);
		int countsInDay = getGameInstanceMgr().getScheduleManager().getInstanceRecordInDay(player, gameInstanceRef.getId());
		if (countsInDay > 0) {
			sendChineseModeQuest_GE(QuestChineseOrderDefines.GameInstanceEverEnter, gameInstanceRefId, "");
		}
	}

	public void leaveWorldAndClearGameInstance() {

		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(getCrtGameInstanceId());
		leaveGameInstance(gameInstance);

		for (String gameInstanceRefId : refIdToGameInstanceId.keySet()) {
			String gameInstanceId = refIdToGameInstanceId.get(gameInstanceRefId);
			gameInstance = getGameInstanceMgr().getGameInstace(gameInstanceId);
			if (gameInstance == null) {
				continue;
			}
			if (gameInstance.isMultiPlayerGameInstance()) {
				continue;
			}
			Collection<Player> playerCollection = gameInstance.getPlayerCollection();
			if (playerCollection != null) {
				// 把自己从副本玩家列表中移除
				gameInstance.getPlayerCollection().remove(getConcreteParent());
			}

			if (playerCollection == null || playerCollection.isEmpty()) {
				MorningGloryContext.getGameInstanceSystemComponent().clearGameInstanceResource(gameInstance);
			}

		}

		refIdToGameInstanceId.clear();
	}

	public void leaveGameInstance(GameInstance gameInstance) {
		if (null == gameInstance) {
			return;
		}

		String gameInstanceId = gameInstance.getGameInstanceRef().getId();
		GameScene crtGameScene = gameInstance.getCrtGameScene();
		MGStatFunctions.gameInstanceStat(getConcreteParent(), StatGameInstance.Leave, gameInstanceId, crtGameScene.getRef().getId());
		if (gameInstance.isMultiPlayerGameInstance()) {
			setCrtGameInstanceId(null);
			return;
		}
		clearGameInstanceResource(gameInstance);
	}

	public void closeMultiGameInstance(GameInstance gameInstance) {
		if (null == gameInstance) {
			return;
		}

		String gameInstanceId = gameInstance.getGameInstanceRef().getId();
		GameScene crtGameScene = gameInstance.getCrtGameScene();
		String sceneRefId = "场景还未创建";
		if (crtGameScene != null) {
			sceneRefId = crtGameScene.getRef().getId();
		}
		MGStatFunctions.gameInstanceStat(getConcreteParent(), StatGameInstance.Leave, gameInstanceId, sceneRefId);

		clearQuestData();

		// 清空当前副本
		if (gameInstance.getGameInstanceRef() != null) {
			removeGameInstanceId(gameInstance.getGameInstanceRef().getId());
		}
		setCrtGameInstanceId(null);
	}

	private void sendLeaveGameInstanceMsgToClient() {
		// 通知客户端离开副本
		G2C_GameInstanceLeave response = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_GameInstanceLeave);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), response);
	}

	private void clearGameInstanceResource(GameInstance gameInstance) {
		if (gameInstance == null) {
			return;
		}
		clearQuestData();
		GameScene crtGameScene = gameInstance.getCrtGameScene();
		Preconditions.checkNotNull(crtGameScene);

		// 清空当前副本
		if (gameInstance.getGameInstanceRef() != null) {
			removeGameInstanceId(gameInstance.getGameInstanceRef().getId());
		}

		// 把自己从副本玩家列表中移除
		gameInstance.getPlayerCollection().remove(getConcreteParent());

		Collection<Player> playerCollection = gameInstance.getPlayerCollection();
		if (playerCollection == null || playerCollection.isEmpty()) {
			MorningGloryContext.getGameInstanceSystemComponent().clearGameInstanceResource(gameInstance);
		}

		setCrtGameInstanceId(null);
	}

	public void goBackComeFromScene(GameInstance gameInstance) {
//		Player player = getConcreteParent();
//		Preconditions.checkNotNull(gameInstance, "gameInstance is null");
//		ComeFromScene comeFromScene = gameInstance.getComeFromSceneRefId().get(player.getId());
//		Preconditions.checkNotNull(comeFromScene, "comeFromScene is null");
//
//		String dstSceneRefId = comeFromScene.getComeFromSceneRefId();
//		int x = comeFromScene.getX();
//		int y = comeFromScene.getY();
//		AbstractGameSceneRef ref = player.getCrtScene().getRef();
//		if (ref.getType() != SceneRef.FuBen) {
//			logger.error("goBackComeFromScene error, player current not in gameInstance, player=" + player);
//			return;
//		}
//		if (Strings.isNullOrEmpty(dstSceneRefId) || StringUtils.equals(dstSceneRefId, ref.getId())) {
//			logger.error("goBackComeFromScene error, dstSceneRefId is invalid, dstSceneRefId=" + dstSceneRefId);
//			player.goHome();
//			return;
//		}
//		if (!PlayerEnterSceneCheckFacade.isValidPosition(dstSceneRefId, x, y)) {
//			logger.error("goBackComeFromScene error, invalid position");
//			player.goHome();
//			return;
//		}
//
//		player.getPlayerSceneComponent().switchTo(dstSceneRefId, x, y);
		Player player = getConcreteParent();
		player.getPlayerSceneComponent().goBackComeFromSceneOrGoHome();
	}

	/**
	 * 检查是否有向玩家开放的副本
	 */
	private void checkGameInstanceOpen() {
		// 检查是否有开放的副本
		List<String> gameInstanceRefIdList = GameInstanceRef.getGameInstanceRefIdList();
		if (gameInstanceRefIdList == null || gameInstanceRefIdList.size() == 0) {
			return;
		}

		long now = System.currentTimeMillis();
		for (String gameInstanceRefId : gameInstanceRefIdList) {
			GameInstanceRef gameInstanceRef = (GameInstanceRef) GameRoot.getGameRefObjectManager().getManagedObject(gameInstanceRefId);
			if (refIdToGameInstanceId.containsKey(gameInstanceRefId)) {
				continue;
			}

			OpenTimeData openTimeData = gameInstanceRef.getOpen().getOpenTime(now);
			// openTimeData不为空证明有副本开放
			if (openTimeData != null) {
				long duringTime = openTimeData.getLastTime();
				long openingTime = gameInstanceRef.getOpen().getTimestamp(openTimeData) / 1000;
				if (duringTime > 0) {
					opening(getConcreteParent(), gameInstanceRef, openingTime, duringTime, MGGameInstanceOpeningTimeType.Daily_OpeningTime);
				}
			}

		}
	}

	private MGGameInstanceSystemComponent getGameInstanceSystemComponent() {
		return MorningGloryContext.getGameInstanceSystemComponent();
	}

	private GameInstanceMgr getGameInstanceMgr() {
		return getGameInstanceSystemComponent().getGameInstanceMgr();
	}

	// =====================================================副本任务======================================================================
	public void showRewardData() { // 弹出副本任务奖励

		int count = 0;
		if (acceptQuest == null || acceptQuest.size() == 0) {
			return;
		}
		for (MGGameInstanceQuest quest : acceptQuest) {
			if (QuestState.SubmittableQuestState == quest.getQuestState() && quest.getQuestCourse().wasCompleted() && quest.getQuestRef().getRewardType() == 0) {
				count++;
			}
		}
		G2C_Instance_QuestReward questReward = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_Instance_QuestReward);
		questReward.setAbsent(true);
		questReward.setTime(questCompletedTime);
		questReward.setCount(count);
		questReward.setQuestList(acceptQuest);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), questReward);

	}

	public void rewardAndClearData() { // 任务发奖并清除副本任务数据
		if (logger.isDebugEnabled()) {
			logger.debug("通知客户端离开副本");
		}

		if (acceptQuest == null || acceptQuest.size() == 0) {
			return;
		}

		List<ItemPair> rewardItemList = new ArrayList<>();

		for (MGGameInstanceQuest quest : acceptQuest) {
			if (QuestState.SubmittableQuestState == quest.getQuestState() && quest.getQuestCourse().wasCompleted() && quest.getQuestRef().getRewardType() == 0) {
				rewardItemList.addAll(quest.takeAllRewardTo(getConcreteParent()));
			}
		}
		RuntimeResult runtimeResult = RuntimeResult.OK();
		if (rewardItemList.size() > 0) {
			runtimeResult = ItemFacade.addItem(getConcreteParent(), rewardItemList, ItemOptSource.GameInstanceQuest);
			if (runtimeResult.getCode() != 1) {
				String content = Bricks.getContents("system_prompt_config_10");
				String json = (new Gson()).toJson(rewardItemList);
				MailMgr.sendMailById(getConcreteParent().getId(), content, Mail.gonggao, json, 0, 0, 0);
			}
		}

		for (MGGameInstanceQuest quest : acceptQuest) {
			MGStatFunctions.gameInstanceQuestStat(getConcreteParent(), StatGameInstanceQuest.Reward, quest.getQuestRef().getId(), quest.getGameInstaceId(),
					quest.getGameSceneRefId());
		}

	}

	// 清除副本任务数据
	public void clearQuestData() {
		if (acceptQuest != null && acceptQuest.size() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("clear acceptQuest.... ");
			}
			acceptQuest.clear();
		}
	}

	public void setAcceptQuest(List<MGGameInstanceQuest> acceptQuest) {
		this.acceptQuest = acceptQuest;
	}

	public List<MGGameInstanceQuest> getAcceptQuest() {
		return acceptQuest;
	}

	public void createQuest(String gameInstanceRefId, String gameInstanceSceneId) {
		checkEverEnterGameInstance(gameInstanceRefId);
		createAllQuest(gameInstanceRefId, gameInstanceSceneId);
		G2C_Instance_QuestAccepted acceptedQuest = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_Instance_QuestAccepted);
		acceptedQuest.setAcceptQuest(this.acceptQuest);
		acceptedQuest.setGameInstanceId(gameInstanceRefId);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), acceptedQuest);
	}

	public final void createAllQuest(String gameInstanceRefId, String gameInstanceSenceId) {
		List<String> questList = getMGGameInstanceSceneQuestList(gameInstanceRefId, gameInstanceSenceId);
		if (questList == null)
			return;

		acceptQuest.clear();
		for (String questId : questList) {
			MGGameInstanceQuest quest = createGameInstanceQuest(gameInstanceRefId, questId);
			if (quest == null)
				continue;

			quest.setGameInstaceId(gameInstanceRefId);
			quest.setGameSceneRefId(gameInstanceSenceId);
			acceptQuest.add(quest);
			MGStatFunctions.gameInstanceQuestStat(getConcreteParent(), StatGameInstanceQuest.Accept, questId, gameInstanceRefId, gameInstanceSenceId);
		}
	}

	public List<String> getMGGameInstanceSceneQuestList(String gameInstanceRefId, String gameInstanceSenceId) {
		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(this.refIdToGameInstanceId.get(gameInstanceRefId));
		GameInstanceSceneRefMgr scenceMgr = gameInstance.getGameInstanceRef().getGameInstanceDataObject(GameInstanceSceneRefMgr.class);
		List<GameInstanceSceneRef> scenceList = scenceMgr.getInstanceSceneList();
		for (GameInstanceSceneRef scene : scenceList) {
			if (scene.getId().equals(gameInstanceSenceId)) {
				return scene.getConditionField();
			}
		}
		return null;
	}

	public MGGameInstanceQuest createGameInstanceQuest(String gameInstanceRefId, String questId) {
		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(this.refIdToGameInstanceId.get(gameInstanceRefId));
		MGGameInstanceQuestRefMgr questMgr = gameInstance.getGameInstanceRef().getGameInstanceDataObject(MGGameInstanceQuestRefMgr.class);
		List<MGGameInstanceQuestRef> allQuest = questMgr.getGameInstanceQuestRefList();
		MGGameInstanceQuest sceneQuest = null;
		for (MGGameInstanceQuestRef questRef : allQuest) {
			if (questRef.getId().trim().equals(questId.trim())) {
				MGGameInstanceQuest quest = new MGGameInstanceQuest();
				quest.setQuestState(QuestState.AcceptedQuestState);
				quest.setQuestRef(questRef);
				quest.createQuestCourseItem(getConcreteParent());
				sceneQuest = quest;
			}
		}
		return sceneQuest;
	}

	// public对外开放的接口=====================================================================================================================
	/**
	 * 返回true（删除成功）
	 * 
	 * @param gameInstanceRefId
	 */
	public boolean removeGameInstanceId(String gameInstanceRefId) {
		if (Strings.isNullOrEmpty(gameInstanceRefId)) {
			return false;
		}
		return refIdToGameInstanceId.remove(gameInstanceRefId) != null;
	}

	public String getGameInstanceId(String gameInstanceRefId) {
		return refIdToGameInstanceId.get(gameInstanceRefId);
	}

	/**
	 * 将对玩家创建的一个副本实体id放进gameInstanceRefIdToGameInstanceId
	 * 
	 * @param gameInstanceRefId
	 * @param gameInstanceId
	 * @return
	 */
	public boolean addGameInstanceIdIfAbsent(String gameInstanceRefId, String gameInstanceId) {
		if (Strings.isNullOrEmpty(gameInstanceRefId) || Strings.isNullOrEmpty(gameInstanceId)) {
			return false;
		}

		if (!refIdToGameInstanceId.containsKey(gameInstanceRefId)) {
			refIdToGameInstanceId.put(gameInstanceRefId, gameInstanceId);
		}

		return true;
	}

	public void opening(Player player, GameInstanceRef gameInstanceRef, long openingTime, long duringTime, byte type) {
		// 1.获取一个新的副本实例
		MGGameInstance gameInstance = new MGGameInstance();
		gameInstance.setId(UUID.randomUUID().toString());
		gameInstance.setGameInstanceRef(gameInstanceRef);

		byte state = gameInstance.create(player, gameInstanceRef);
		if (state != MGGameInstance.OK) {
			return;
		}

		// 2.挂到玩家的PlayerGameInstanceComponent上
		boolean ret = addGameInstanceIdIfAbsent(gameInstanceRef.getId(), gameInstance.getId());
		if (!ret) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("player=" + getConcreteParent().getIdentity().getId() + " open gameInstanceId=" + gameInstance.getId());
		}
		if (gameInstance.isMultiPlayerGameInstance()) {
			gameInstance.setState(GameInstanceState.Opened_State);
		}
		GameInstanceMgr gameInstanceMgr = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr();
		gameInstanceMgr.addGameInstance(gameInstance);

	}

	public GameArea getGameArea() {
		return MMORPGContext.getGameAreaComponent().getGameArea();
	}

	public String getCrtGameInstanceId() {
		return crtGameInstanceId;
	}

	public void setCrtGameInstanceId(String crtGameInstanceId) {
		this.crtGameInstanceId = crtGameInstanceId;
	}

	public boolean checkLayerFinish() {
		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(this.crtGameInstanceId);
		GameInstanceSceneRefMgr gameInstanceSceneRefMgr = gameInstance.getGameInstanceRef().getGameInstanceDataObject(GameInstanceSceneRefMgr.class);
		GameInstanceSceneRef gameInstanceSceneRef = gameInstanceSceneRefMgr.getInstanceSceneList().get(gameInstance.getLayerId());
		List<LinYueShengModeCondition<Player>> gameInstanceSceneFinishConditionList = gameInstanceSceneRef.getSceneFinishConditionList();
		if (gameInstanceSceneFinishConditionList != null) {
			for (LinYueShengModeCondition<Player> linYueShengModeCondition : gameInstanceSceneFinishConditionList) {
				RuntimeResult result = linYueShengModeCondition.eligible(getConcreteParent());
				if (!result.isOK()) {
					return false;
				}
			}
		}
		return true;
	}

	public void checkIfOutOfMonster(GameInstance gameInstance) {
		Collection<Monster> allMonsters = gameInstance.getCrtGameScene().getMonsterMgrComponent().getAllMonsters();
		if (allMonsters.size() == 0) {
			for (MGGameInstanceQuest quest : acceptQuest) {
				if (quest.getQuestState() != QuestState.SubmittableQuestState) {
					quest.setQuestState(QuestState.SubmittableQuestState);
					questCompletedTime = System.currentTimeMillis();
					G2C_Instance_QuestFinish stateUpdate = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_Instance_QuestFinish);
					stateUpdate.setQuestId(quest.getQuestRef().getId());
					stateUpdate.setRewardType(quest.getQuestRef().getRewardType());
					GameRoot.sendMessage(getConcreteParent().getIdentity(), stateUpdate);
					MGStatFunctions.gameInstanceQuestStat(getConcreteParent(), StatGameInstanceQuest.Finish, quest.getQuestRef().getId(), quest.getGameInstaceId(),
							quest.getGameSceneRefId());
					if (quest.getQuestRef().getRewardType() == 1) {
						List<ItemPair> rewardItemList = new ArrayList<>();
						rewardItemList.addAll(quest.takeRewardTo(getConcreteParent()));
						if (rewardItemList.size() > 0) {
							RuntimeResult runtimeResult = RuntimeResult.OK();
							runtimeResult = ItemFacade.addItem(getConcreteParent(), rewardItemList, ItemOptSource.GameInstanceQuest);
							if (runtimeResult.getCode() != 1) {
								String content = Bricks.getContents("system_prompt_config_10");
								String json = (new Gson()).toJson(rewardItemList);
								MailMgr.sendMailById(getConcreteParent().getId(), content, Mail.gonggao, json, 0, 0, 0);
							}
						}
						MGStatFunctions.gameInstanceQuestStat(getConcreteParent(), StatGameInstanceQuest.Reward, quest.getQuestRef().getId(), quest.getGameInstanceRefId(),
								quest.getGameSceneRefId());
					}
				}
			}
		}

	}
}
