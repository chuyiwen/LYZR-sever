package newbee.morningGlory.mmorpg.player.castleWar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.achievement.CastleWarRecord;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementComponent;
import newbee.morningGlory.mmorpg.player.castleWar.event.C2G_CastleWar_FactionList;
import newbee.morningGlory.mmorpg.player.castleWar.event.C2G_CastleWar_GetGift;
import newbee.morningGlory.mmorpg.player.castleWar.event.C2G_CastleWar_Instance;
import newbee.morningGlory.mmorpg.player.castleWar.event.C2G_CastleWar_JoinWar;
import newbee.morningGlory.mmorpg.player.castleWar.event.C2G_CastleWar_OpenServer;
import newbee.morningGlory.mmorpg.player.castleWar.event.C2G_CastleWar_RequestTime;
import newbee.morningGlory.mmorpg.player.castleWar.event.CastleWarActionEventDefines;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_FactionList;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_JoinWar;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_OpenServer;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_RequestTime;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityType;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarApplyMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.CastleWarEnd_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.JoinCastleWar_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.KillInCastleWar_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarRef;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;
import newbee.morningGlory.mmorpg.union.MGUnionMgr;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatCastleWar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.gameEvent.PlayerSwitchScene_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Strings;

public class MGCastleWarComponent extends ConcreteComponent<Player> {
	private final static Logger logger = Logger.getLogger(MGCastleWarComponent.class);
	public static final String Tag = "MGCastleWarComponent";
	private CastleWarMgr castleWarMgr = null;
	private MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
	private String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private String PlayerSwitchScene_GE_Id = PlayerSwitchScene_GE.class.getSimpleName();
	private String PlayerDead_GE_Id = PlayerDead_GE.class.getSimpleName();
	private String CastleWarEnd_GE_Id = CastleWarEnd_GE.class.getSimpleName();
	private Player player;
	private boolean dailyGift = false;
	private int instanceTime = 0;
	private long lastRefreshTime = 0;
	
	private SFTimer delayPeriodClaendarChime;

	public MGCastleWarComponent() {
	}

	private PersistenceObject persisteneceObject;

	public void setPersisteneceObject(PersistenceObject persisteneceObject) {
		this.persisteneceObject = persisteneceObject;
	}

	public PersistenceObject getPersisteneceObject() {
		return persisteneceObject;
	}

	public int getInstanceTime() {
		return instanceTime;
	}

	public void setInstanceTime(int instanceTime) {
		this.instanceTime = instanceTime;
	}

	public boolean getDailyGift() {
		return dailyGift;
	}

	public void setDailyGift(boolean dailyGift) {
		this.dailyGift = dailyGift;
	}

	private void addTimer() {
		delayPeriodClaendarChime = MMORPGContext.getTimerCreater().calendarChime(new SFTimeChimeListener() {
			@Override
			public void handleServiceShutdown() {
			}

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				dailyReset();
			}

		}, SFTimeUnit.HOUR, 0);
	}
	
	private void removeTimer() {
		if (delayPeriodClaendarChime != null) {
			delayPeriodClaendarChime.cancel();
		}
	}

	private void dailyReset() {
		Date date1 = new Date(lastRefreshTime);
		Date date2 = new Date(System.currentTimeMillis());
		if (!org.apache.commons.lang3.time.DateUtils.isSameDay(date1, date2)) {
			setDailyGift(false);
			instanceTime = 0;
			setLastRefreshTime(System.currentTimeMillis());
		}
	}

	@Override
	public void ready() {
		addInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
		addInterGameEventListener(PlayerSwitchScene_GE_Id);
		addInterGameEventListener(PlayerDead_GE_Id);
		addInterGameEventListener(CastleWarEnd_GE_Id);
		addActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_GetGift);
		addActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_Instance);
		addActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_JoinWar);
		addActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_FactionList);
		addActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_RequestTime);
		addActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_OpenServer);
		player = getConcreteParent();
		dailyReset();
		addTimer();
		castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.CastleWarMgr);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
		removeInterGameEventListener(PlayerSwitchScene_GE_Id);
		removeInterGameEventListener(PlayerDead_GE_Id);
		removeInterGameEventListener(CastleWarEnd_GE_Id);
		removeActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_GetGift);
		removeActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_Instance);
		removeActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_JoinWar);
		removeActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_FactionList);
		removeActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_RequestTime);
		removeActionEventListener(CastleWarActionEventDefines.C2G_CastleWar_OpenServer);
		removeTimer();
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case CastleWarActionEventDefines.C2G_CastleWar_GetGift:
			handle_CastleWar_GetGift((C2G_CastleWar_GetGift) event, actionEventId, identity);
			break;
		case CastleWarActionEventDefines.C2G_CastleWar_Instance:
			handle_CastleWar_Instance((C2G_CastleWar_Instance) event, actionEventId, identity);
			break;
		case CastleWarActionEventDefines.C2G_CastleWar_JoinWar:
			handle_CastleWar_JoinWar((C2G_CastleWar_JoinWar) event, actionEventId, identity);
			break;
		case CastleWarActionEventDefines.C2G_CastleWar_FactionList:
			handle_CastleWar_FactionList((C2G_CastleWar_FactionList) event, actionEventId, identity);
			break;
		case CastleWarActionEventDefines.C2G_CastleWar_RequestTime:
			handle_CastleWar_RequestTime((C2G_CastleWar_RequestTime) event, actionEventId, identity);
			break;
		case CastleWarActionEventDefines.C2G_CastleWar_OpenServer:
			handle_C2G_CastleWar_OpenServer((C2G_CastleWar_OpenServer) event, actionEventId, identity);
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
			String crtSceneId = player.getCrtScene().getRef().getId();
			String targetScene = castleWarMgr.getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
			if (!StringUtils.equals(crtSceneId, targetScene)) {
				return;
			}
			PropertyDictionary monsterPd = castleWarMgr.getCastleWarRef().getProperty();
			if (!monster.getMonsterRef().getId().equals(MGPropertyAccesser.getMonsterRefId(monsterPd))) {
				return;
			}
			FightSprite attacker = monsterDead_GE.getAttacker();
			if (!castleWarMgr.isCastleWarStart()) {
				return;
			} else if (!(attacker instanceof Player)) {
				return;
			} else if ((attacker instanceof Player) && !((Player) attacker).getId().equals(player.getId())) {
				return;
			} else {
				PropertyDictionary pd = ((Player) attacker).getProperty();
				String unionName = MGPropertyAccesser.getUnionName(pd);
				if (StringUtils.isEmpty(unionName)) {
					return;
				}
			}
			
			String monsterOwner = castleWarMgr.getMonsterOwner();
			if (StringUtils.equals(monsterOwner, MGPropertyAccesser.getUnionName(player.getProperty()))) {
				//CastleWarRecordMgr.addKillCastleWarBossCount(castleWarRecord);
				sendKillInCastleWarGameEventAndRecord(KillInCastleWar_GE.Kill_Boss);
			}
			
			castleWarMgr.refreshMonster(player);
		}
		if (event.isId(PlayerManager.LeaveWorld_GE_Id)) {
			if (logger.isDebugEnabled()) {
				logger.debug("收到玩家离开世界事件");
			}
			String sceneRefId = player.getSceneRefId();
			String targetScene = castleWarMgr.getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
			if (StringUtils.equals(sceneRefId,targetScene)) {
				checkSceneToSendWithOutMessage();
			}
			String instanceScene = castleWarMgr.getCastleWarRef().getCastleWarInstanceTransfer().getTargetScene();
			if (StringUtils.equals(sceneRefId,instanceScene)) {
				String warScene = castleWarMgr.getCastleWarRef().getCastleWarOutSceneTransfer().getTargetScene();
				int tranferInId = castleWarMgr.getCastleWarRef().getCastleWarOutSceneTransfer().getTranferInId();
				PlayerSceneComponent playerSceneComponent = player.getPlayerSceneComponent();
				playerSceneComponent.switchToByTransId(warScene, tranferInId);
			}
		}
		if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			if (logger.isDebugEnabled()) {
				logger.debug("收到玩家进入世界事件");
			}
			String targetScene = castleWarMgr.getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
			String sceneRefId = MGPropertyAccesser.getSceneRefId(player.getProperty());
			if (StringUtils.equals(sceneRefId, targetScene)) {
				checkSceneToSendWithOutMessage();
				isJoinCastleWarScene();
			}
			
			PropertyDictionary pd = player.getProperty();
			String unionName = MGPropertyAccesser.getUnionName(pd);
			String kingCityUnionName = unionMgr.getKingCityUnionName();
			if (StringUtils.equals(unionName, kingCityUnionName) && MGUnionHelper.isUnionCreater(player)) {
				SystemPromptFacade.broadCastKingCityCreaterLogin(getConcreteParent().getName());
			}
			
			
		}
		
		if (event.isId(PlayerSwitchScene_GE_Id)) {
			PlayerSwitchScene_GE ge = (PlayerSwitchScene_GE)event.getData();
			Player switchPlayer = ge.getPlayer();
			if (!StringUtils.equals(switchPlayer.getId(), player.getId())) {
				logger.error("switchPlayer is not self! switchPlayerId = " + switchPlayer.getId());
				return;
			}
			
			GameScene dstScene = ge.getDstScene();
			if (!isInPalace(dstScene)) {
				if (logger.isDebugEnabled()) {
 					logger.debug("enter detScene sceneRefId = " + dstScene.getRef().getId());
				}
				return;
			}
			
			isJoinCastleWarScene();
			
		} 
		
		if (event.isId(PlayerDead_GE_Id)) {
			PlayerDead_GE ge = (PlayerDead_GE)event.getData();
			FightSprite attacker = ge.getAttacker();
			if (attacker instanceof Monster) {
				Monster baobao = (Monster) attacker;
				if (baobao.getMonsterRef().isRegularMonster()) {
					return;
				}
				
				if (baobao.getOwner() != null) {
					attacker = baobao.getOwner();
				}
			}
			
			if (!StringUtils.equals(attacker.getId(), getConcreteParent().getId())) {
				return;
			}
			
			Player victim = ge.getPlayer();
			
			if (victim == null || !isInPalace(victim.getCrtScene())) {
				return;
			}
			
			if (!castleWarMgr.isCastleWarStart()) {
				return;
			}
			
			sendKillInCastleWarGameEventAndRecord(KillInCastleWar_GE.Kill_Enemy);
			
		}
		
		super.handleGameEvent(event);
	}
	
	private boolean isInPalace(GameScene gameScene) {
		if (player == null) {
			return false;
		}
		
		String sceneRefId = gameScene.getRef().getId();
		boolean result = StringUtils.equals(sceneRefId, "S012");
		
		return result;
	}

	private void checkSceneToSendWithOutMessage() {
		byte crtActivityState = castleWarMgr.getCrtActivityState();
		byte preActivityState = castleWarMgr.getPreActivityState();
		if (crtActivityState == 2 || preActivityState == 3) {
			castleWarMgr.transferOut(player);
		}
	}

	private void handle_CastleWar_JoinWar(C2G_CastleWar_JoinWar event, short actionEventId, Identity identity) {
		G2C_CastleWar_JoinWar joinWar = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_JoinWar);
		int isSucceed = 0;
		PropertyDictionary pd = player.getProperty();
		String unionName = MGPropertyAccesser.getUnionName(pd);
		String kingCityUnionName = unionMgr.getKingCityUnionName();

		if (StringUtils.isEmpty(unionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_NO_UNION);
		} else if (unionName.equals(kingCityUnionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_KINGCITY);
		} else if (CastleWarApplyMgr.getInstance().isAlreadySignupWar(unionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_APPLY_TWICE);
		} else if (!MGUnionHelper.isUnionCreater(player)) {
			// 判断是不是公会会长
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_APPLY_TWICE);
		} else if (checkIfEnoughtItem(player, actionEventId)) {
			RuntimeResult signupWar = CastleWarApplyMgr.getInstance().signupWar(unionName);
			if (signupWar.isOK()) {
				subItemAndGold(actionEventId);
				isSucceed = 1;
				MGStatFunctions.castleWarStat(getConcreteParent(), StatCastleWar.JoinWar, unionName, (byte) 0);
			} else {
				ResultEvent.sendResult(identity, actionEventId, signupWar.getApplicationCode());
			}
		}
		joinWar.setResult((byte) isSucceed);
		GameRoot.sendMessage(identity, joinWar);
	}

	private boolean checkIfEnoughtItem(Player player, short actionEventId) {
		if (castleWarMgr == null) {
			logger.error("castleWarMgr == null");
			return false;
		}
		PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
		int goldMoney = playMoneyCompoent.getGold();
		PropertyDictionary property = castleWarMgr.getCastleWarRef().getProperty();
		int gold = MGPropertyAccesser.getGold(property);
		if (goldMoney < gold) {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, (MGErrorCode.CODE_CASTLEWAR_NO_MONEY));
			return false;
		}
		PropertyDictionary property2 = castleWarMgr.getCastleWarRef().getProperty();
		String itemId = MGPropertyAccesser.getItemId(property2);
		if (ItemFacade.getNumber(player, itemId) < 1) {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, (MGErrorCode.CODE_CASTLEWAR_NO_ITEM));
			return false;
		}
		return true;

	}
	
	private void subItemAndGold(short actionEventId) {
		PropertyDictionary property2 = castleWarMgr.getCastleWarRef().getProperty();
		PropertyDictionary property = castleWarMgr.getCastleWarRef().getProperty();
		int gold = MGPropertyAccesser.getGold(property);
		String itemId = MGPropertyAccesser.getItemId(property2);
		if (!ItemFacade.removeItem(getConcreteParent(), itemId, 1, true,ItemOptSource.CastleWar)) {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, (MGErrorCode.CODE_CASTLEWAR_NO_ITEM));
		} else {
			PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
			playMoneyCompoent.subGold(gold,ItemOptSource.CastleWar);
		}
	}

	private void handle_CastleWar_Instance(C2G_CastleWar_Instance event, short actionEventId, Identity identity) {
		PropertyDictionary playerPd = player.getProperty();
		String unionName = MGPropertyAccesser.getUnionName(playerPd);
		String kingCityUnionName = unionMgr.getKingCityUnionName();
		if (StringUtils.isEmpty(unionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_NO_UNION);
			return;
		}
		if (!unionName.equals(kingCityUnionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_NOTKINGCITY);
			return;
		}

		PropertyDictionary pd = castleWarMgr.getCastleWarRef().getProperty();
		int timesADay = MGPropertyAccesser.getTimesADay(pd);
		if (instanceTime >= timesADay) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_INSTANCE_OT);
			return;
		}
		if (!player.isSceneReady()) {
			return;
		}
		CastleWarRef castleWarRef = castleWarMgr.getCastleWarRef();
		String sceneId = castleWarRef.getCastleWarInstanceTransfer().getTargetScene();
		int transferInId = castleWarRef.getCastleWarInstanceTransfer().getTranferInId();
		PlayerSceneComponent playerSceneComponent = player.getPlayerSceneComponent();
		if (playerSceneComponent.switchToByTransId(sceneId, transferInId).isOK()) {
			instanceTime += 1;
			PlayerImmediateDaoFacade.update(player);
			MGStatFunctions.castleWarStat(player, StatCastleWar.Instance, unionName, (byte) 0);
		}
	}

	private void handle_CastleWar_GetGift(C2G_CastleWar_GetGift event, short actionEventId, Identity identity) {
		byte crtActivityState = castleWarMgr.getCrtActivityState();
		byte preActivityState = castleWarMgr.getPreActivityState();
		if (crtActivityState == 2 || preActivityState == 3) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_CANT_GET_GIFT);
			return;
		}
		String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
		String kingCityUnionName = unionMgr.getKingCityUnionName();
		if (StringUtils.isEmpty(unionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_NO_UNION);
			return;
		}
		if (StringUtils.isEmpty(kingCityUnionName) || !unionName.equals(kingCityUnionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_NOT_KINGCITY);
			return;
		}
		if (dailyGift) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_GIFT_TWICE);
			return;
		}
		List<ItemPair> rewardItemList = new ArrayList<>();
		ItemPair item = new ItemPair(castleWarMgr.getCastleWarRef().getGiftRefID(), 1, false);
		rewardItemList.add(item);
		if (ItemFacade.addItemCompareSlot(player, rewardItemList,ItemOptSource.CastleWar).isOK()) {
			setDailyGift(true);
			PlayerImmediateDaoFacade.update(player);
			MGStatFunctions.castleWarStat(player, StatCastleWar.Gift, unionName, (byte) 0);
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_CASTLEWAR_GIFT_SUCCEED);
		} else {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
		}
	}

	private void handle_CastleWar_FactionList(C2G_CastleWar_FactionList event, short actionEventId, Identity identity) {
		List<String> castleWarList = CastleWarApplyMgr.getInstance().getSightUpUnionList();
		String kingCityUnionName = unionMgr.getKingCityUnionName();
		G2C_CastleWar_FactionList factionList = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_FactionList);
		factionList.setCastleWarList(castleWarList);
		factionList.setKingCityUnion(kingCityUnionName);
		GameRoot.sendMessage(identity, factionList);
	}
	
	private void handle_CastleWar_RequestTime(C2G_CastleWar_RequestTime event, short actionEventId, Identity identity) {
		G2C_CastleWar_RequestTime requestTime = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_RequestTime);
		requestTime.setTimeToStart(castleWarMgr.getActivityRemainingStartTime());
		requestTime.setTimeToEnd(castleWarMgr.getActivityRemainingEndTime());
		GameRoot.sendMessage(identity, requestTime);
	}
	
	private void handle_C2G_CastleWar_OpenServer(C2G_CastleWar_OpenServer event, short actionEventId, Identity identity) {
		G2C_CastleWar_OpenServer requestTime = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_OpenServer);
		GameRoot.sendMessage(identity, requestTime);
	}

	public boolean isDailyGift() {
		return dailyGift;
	}

	public long getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(long lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}
	
	/**
	 * 判断本次进入皇宫是否属于参加攻城战
	 */
	public void isJoinCastleWarScene() {
		MGPlayerAchievementComponent playerAchievementComponent = (MGPlayerAchievementComponent)player.getTagged(MGPlayerAchievementComponent.Tag);
		CastleWarRecord record = playerAchievementComponent.getRecord();
		long lastInPalaceStamp = record.getLastInPalaceStamp();
		long now = System.currentTimeMillis();
		boolean isLastInPeriod = castleWarMgr.isInPeriod(lastInPalaceStamp);
		boolean isNowInPeriod = castleWarMgr.isInPeriod(now);
		
		if (isLastInPeriod) {
			return;
		}

		if (isNowInPeriod) {
			record.recordEnterCastleWar(now);
			sendJoinCastleWarGameEvent();
		}
	}
	
	private void sendJoinCastleWarGameEvent() {
		JoinCastleWar_GE joinCastleWarGE = new JoinCastleWar_GE();
		GameEvent<JoinCastleWar_GE> event = GameEvent.getInstance(JoinCastleWar_GE.class.getSimpleName(), joinCastleWarGE);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}
	
	/**
	 * 攻城战结束，统计相关结果
	 */
	public void recordCastleWarResult() {
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		String unionName = unionMgr.getKingCityUnionName();
		String ownerUnionName = MGPropertyAccesser.getUnionName(player.getProperty());
		CastleWarRecord castleWarRecord = getCastleWarRecord();
		
		
		if (!isInPalace(player.getCrtScene())) {
			return;
		}
		
		if (Strings.isNullOrEmpty(ownerUnionName)) {
			return;
		}
		
		// 失败
		if (!StringUtils.equals(unionName, ownerUnionName)) { 
			castleWarRecord.resetConsecutiveCastelWarCount();
			return;
		}
		
		// 胜利
		boolean isCreater = MGUnionHelper.isUnionCreater(player);
		if (isCreater) {
			castleWarRecord.addWinAsChairmanCastleWarCount();
		}
		
		castleWarRecord.addConsecutiveCastelWarCount();
		
	}
	
	/**
	 * 攻城战中击杀BOSS 或者 击杀其他公会成员 记录 并发送事件 
	 */
	private void sendKillInCastleWarGameEventAndRecord(byte killType) {
		CastleWarRecord record = getCastleWarRecord();
		if (killType == KillInCastleWar_GE.Kill_Boss) {
			record.addKillCastleWarBossCount();
		} else if (killType == KillInCastleWar_GE.Kill_Enemy) {
			record.addKillEnmeyCount();
		}
		
		KillInCastleWar_GE killInCastleWarGe = new KillInCastleWar_GE(killType, player);
		GameEvent<KillInCastleWar_GE> event = GameEvent.getInstance(KillInCastleWar_GE.class.getSimpleName(), killInCastleWarGe);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}
	
	
	
	private CastleWarRecord getCastleWarRecord() {
		MGPlayerAchievementComponent playerAchievementComponent = (MGPlayerAchievementComponent)player.getTagged(MGPlayerAchievementComponent.Tag);
	
		return playerAchievementComponent.getRecord();
	}
}
