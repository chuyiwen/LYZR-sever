package newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityType;
import newbee.morningGlory.mmorpg.sceneActivities.event.C2G_MonsterIntrusion_ContinuTime;
import newbee.morningGlory.mmorpg.sceneActivities.event.C2G_MonsterIntrusion_EnterMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.C2G_MonsterIntrusion_IsOpen;
import newbee.morningGlory.mmorpg.sceneActivities.event.C2G_MonsterIntrusion_LeaveMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_BossTimeRefresh;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_ContinuTime;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_EnterMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_Font;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_IsOpen;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_LeaveMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.SceneActivityEventDefines;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionRef;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionScrollRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.gameInstance.ComeFromScene;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterEnterWorld_GE;
import sophia.mmorpg.monster.gameEvent.MonsterLeaveWorld_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.Bricks;
import sophia.mmorpg.player.chat.sysytem.SpecialEffectsType;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.chat.sysytem.SystemPromptPosition;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.PlayerSwitchScene_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.itemBag.gameEvent.RemoveItem_GE;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MonsterInvasionComponent extends ConcreteComponent<Player> {

	private static Logger logger = Logger.getLogger(MonsterInvasionComponent.class);

	public static final String Tag = "MonsterInvasionComponent";
	private MonsterInvasionMgr monsterInvasionMgr1;
	private String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private String PlayerSwitchScene_GE_Id = PlayerSwitchScene_GE.class.getSimpleName();
	private String RemoveItem_GE_Id = RemoveItem_GE.class.getSimpleName();
	private String MonsterLeaveWorld_GE_Id = MonsterLeaveWorld_GE.class.getSimpleName();
	private String MonsterEnterWorld_GE_Id = MonsterEnterWorld_GE.class.getSimpleName();
	private ComeFromScene comeFromScene = null;
	private SFTimer timer = null;

	public MonsterInvasionComponent() {
	}

	@Override
	public void ready() {
		addActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_EnterMap);
		addActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_LeaveMap);
		addActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_ContinuTime);
		addActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_IsOpen);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		addInterGameEventListener(PlayerSwitchScene_GE_Id);
		addInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		addInterGameEventListener(RemoveItem_GE_Id);
		addInterGameEventListener(MonsterLeaveWorld_GE_Id);
		addInterGameEventListener(MonsterEnterWorld_GE_Id);
		monsterInvasionMgr1 = (MonsterInvasionMgr) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.MonsterIntrusionMgr1);
		timer = MMORPGContext.getTimerCreater().minuteCalendarChime(new SFTimeChimeListener() {
			
			@Override
			public void handleTimeChimeCancel() {
				
			}
			
			@Override
			public void handleTimeChime() {
				List<String> sceneRefIdList = monsterInvasionMgr1.getMonsterIntrusionRef().getSceneRefIdList();
				GameScene crtScene = getConcreteParent().getCrtScene();
				if (crtScene == null) {
					return;
				}
				
				if (!sceneRefIdList.contains(crtScene.getRef().getId())) {
					return;
				}
				
				monsterInvasionMgr1.sendMonsterRefreshTime(getConcreteParent());
			}
			
			@Override
			public void handleServiceShutdown() {
				
			}
		});
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_EnterMap);
		removeActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_LeaveMap);
		removeActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_ContinuTime);
		removeActionEventListener(SceneActivityEventDefines.C2G_MonsterIntrusion_IsOpen);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		removeInterGameEventListener(PlayerSwitchScene_GE_Id);
		removeInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		removeInterGameEventListener(RemoveItem_GE_Id);
		removeInterGameEventListener(MonsterLeaveWorld_GE_Id);
		removeInterGameEventListener(MonsterEnterWorld_GE_Id);
		if (timer != null) {
			timer.cancel();
		}
		
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		MonsterInvasionRef monsterInvasionRef = monsterInvasionMgr1.getMonsterIntrusionRef();
		List<String> sceneRefIdList = monsterInvasionRef.getSceneRefIdList();
		if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			String playerSceneRefId = concreteParent.getSceneRefId();
			if (!sceneRefIdList.contains(playerSceneRefId)) {
				return;
			}
			
			if (monsterInvasionMgr1.isOpen()) {
				G2C_MonsterIntrusion_EnterMap res = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_EnterMap);
				res.setContinuTime(monsterInvasionMgr1.getActivityRemainingEndTime());
				res.setExpMultiple(monsterInvasionMgr1.getMonsterIntrusionRef().getExpMultiple());
				GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
				concreteParent.getExpComponent().addExpMultiple((float) monsterInvasionMgr1.getMonsterIntrusionRef().getExpMultiple());
				monsterInvasionMgr1.sendMonsterRefreshTime(getConcreteParent());
				refreshSelfFont();
			}
		} else if (event.isId(PlayerManager.LeaveWorld_GE_Id)) {
			if (sceneRefIdList.contains(concreteParent.getSceneRefId()) && concreteParent.isSceneReady()) {
				Position crtPosition = concreteParent.getCrtPosition();
				dropItemAndRecoveryExp(concreteParent.getCrtScene(), crtPosition.getX(), crtPosition.getY());
			}
		} else if (event.isId(RemoveItem_GE_Id)) {
			RemoveItem_GE removeEvent = (RemoveItem_GE) event.getData();
			if (StringUtils.equals(removeEvent.getItemRefId(), "item_coupon") || StringUtils.equals(removeEvent.getItemRefId(), "item_coupon_2")) {
				refreshSelfFont();
			}
		} else if (event.isId(PlayerSwitchScene_GE_Id)) {
			if (!monsterInvasionMgr1.isOpen()) {
				return;
			}
			PlayerSwitchScene_GE ge = (PlayerSwitchScene_GE) event.getData();
			GameScene fromGameScene = ge.getFromScene();
			GameScene desGameScene = ge.getDstScene();
			if (sceneRefIdList.contains(desGameScene.getRef().getId())) {
				monsterInvasionMgr1.sendMonsterRefreshTime(getConcreteParent());
				return;
			}
			
			if (!sceneRefIdList.contains(fromGameScene.getRef().getId())) {
				return;
			}
			

			dropItemAndRecoveryExp(fromGameScene, ge.getFromX(), ge.getFromY());
			//此处不应该调用leaveScene, 这种传送事件，一般是由于使用传送石等传送出去的，如果重复调用leaveScene会调用goHome，导致客户端请求传送目标场景与goHome的场景不一致
			//monsterInvasionMgr.leaveScene(concreteParent);
			G2C_MonsterIntrusion_LeaveMap leaveMap = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_LeaveMap);
			GameRoot.sendMessage(getConcreteParent().getIdentity(), leaveMap);
		
		} else if (event.isId(MonsterLeaveWorld_GE_Id)) {
			MonsterLeaveWorld_GE ge = (MonsterLeaveWorld_GE)event.getData();
			Monster monster = ge.getMonster();
			if (monster.isBoss()) {
				monsterInvasionMgr1.sendMonsterRefreshTime(getConcreteParent());
			}
		} else if (event.isId(MonsterEnterWorld_GE_Id)) {
			MonsterEnterWorld_GE ge = (MonsterEnterWorld_GE)event.getData();
			String sceneRefId = ge.getSceneRefId();
			Monster monster = ge.getMonster();
			
			if (!sceneRefIdList.contains(sceneRefId)) {
				return;
			}
			
			if (!monster.isBoss()) {
				return;
			}
			
			monsterInvasionMgr1.sendMonsterRefreshTime(getConcreteParent());
		}
	
		super.handleGameEvent(event);
	}

	public void dropItemAndRecoveryExp(GameScene fromScene, int x, int y) {
		dropItem(fromScene,concreteParent);
		monsterInvasionMgr1.RecoveryExpMultiple(concreteParent);// 还原经验翻倍
	}

	public void dropItem(GameScene fromScene, Player player) {
		List<String> itemRefIdList = monsterInvasionMgr1.getMonsterIntrusionRef().getItemRefIdList();
		
		int allNum = 49;
		for (String itemRefId : itemRefIdList) {
			int number = ItemFacade.getNumber(player, itemRefId);
			if (allNum - number < 0 && allNum > 0) {
				number = allNum;
				allNum = 0;
			} else if (allNum > 0) {
				allNum -= number;
			} else {
				number = 0;
			}
			
			if (number != 0) {
				Position crtPosition = player.getCrtPosition();
				ItemFacade.dropItem(player, player,new ItemPair(itemRefId, number, true), fromScene, new Position(crtPosition.getX(), crtPosition.getY()), ItemOptSource.MonsterInvasion);
			}
		}
		MGPropertyAccesser.setOrPutMonsterInvasionFont(player.getSenceProperty(), (byte) 0);
	}
	
	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("怪物入侵Id:" + eventId);
		}
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (eventId) {
		case SceneActivityEventDefines.C2G_MonsterIntrusion_EnterMap:// 进入活动地图
			handle_Monster_EnterMap((C2G_MonsterIntrusion_EnterMap) event, actionEventId, identity);
			break;

		case SceneActivityEventDefines.C2G_MonsterIntrusion_LeaveMap:// 离开
			handle_Monster_LeaveScene((C2G_MonsterIntrusion_LeaveMap) event, actionEventId, identity);
			break;

		case SceneActivityEventDefines.C2G_MonsterIntrusion_ContinuTime:// 发送倒数时间给客户端
			handle_Monster_ContinuTime((C2G_MonsterIntrusion_ContinuTime) event, actionEventId, identity);
			break;

		case SceneActivityEventDefines.C2G_MonsterIntrusion_IsOpen:// 活动是否开启
			handle_Monster_IsOpen((C2G_MonsterIntrusion_IsOpen) event, actionEventId, identity);
			break;
		}
		super.handleActionEvent(event);
	}

	private void handle_Monster_IsOpen(C2G_MonsterIntrusion_IsOpen event, short actionEventId, Identity identity) {
		boolean isOpen = monsterInvasionMgr1.isOpen();
		// 发送倒数时间给客户端
		G2C_MonsterIntrusion_IsOpen res4 = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_IsOpen);
		res4.setIsOpen(isOpen);
		GameRoot.sendMessage(identity, res4);
	}

	private void handle_Monster_ContinuTime(C2G_MonsterIntrusion_ContinuTime event, short actionEventId, Identity identity) {
		G2C_MonsterIntrusion_ContinuTime countinuTime = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_ContinuTime);
		countinuTime.setTimeToStart(monsterInvasionMgr1.getActivityRemainingStartTime());
		countinuTime.setTimeToEnd(monsterInvasionMgr1.getActivityRemainingEndTime());
		GameRoot.sendMessage(identity, countinuTime);
	}

	private void handle_Monster_LeaveScene(C2G_MonsterIntrusion_LeaveMap event, short actionEventId, Identity identity) {
		List<String> sceneRefIdList = monsterInvasionMgr1.getMonsterIntrusionRef().getSceneRefIdList();// 活动场景
		if (!sceneRefIdList.contains(concreteParent.getSceneRefId())) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_INVASIOIN_ALREAD_LEAVE);
			return;
		}

//		Position crtPosition = concreteParent.getCrtPosition();
//		dropItemAndRecoveryExp(concreteParent.getCrtScene(), crtPosition.getX(), crtPosition.getY());  在切换场景处已有，此处注释
//		monsterInvasionMgr.leaveScene(concreteParent);
		concreteParent.getPlayerSceneComponent().goBackComeFromSceneOrGoHome();
		G2C_MonsterIntrusion_LeaveMap leaveMap = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_LeaveMap);
		GameRoot.sendMessage(identity, leaveMap);
	}

	private void handle_Monster_EnterMap(C2G_MonsterIntrusion_EnterMap event, short actionEventId, Identity identity) {
		if (!SceneActivityMgr.checkEnter(concreteParent, event.getSceneRefId())) {
			String systemPromptConfigRefId = "system_prompt_config_19";// systemPromptConfig.json
			MonsterInvasionRef monsterIntrusionRef = monsterInvasionMgr1.getMonsterIntrusionRef();
			//monsterInvasionMgr.checkMonsterReflesh();
			String time = monsterIntrusionRef.getTime();
			String[] timeSplit = time.split("&");
			List<String> list = new ArrayList<>();
			for (String sigleRange : timeSplit) {
				String[] sigleTime = sigleRange.split("\\|");
				for (String a : sigleTime) {
					list.add(a);
				}
			}
			if (list.size() < 4) {
				return;
			}
			String content = Bricks.getContents(systemPromptConfigRefId, list.get(0), list.get(1), list.get(2), list.get(3));
			SystemPromptFacade.sendMsgSpecialEffects(concreteParent, content, SystemPromptPosition.POSITION_MIDDLE_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_GREEN);
			return;
		}

		String sceneRefId = monsterInvasionMgr1.getRef().getSceneRefId();// 活动场景
		if (StringUtils.equals(concreteParent.getSceneRefId(), sceneRefId)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_INVASIOIN_ALREAD_IN);
			return;
		}

		if (!StringUtils.equals(sceneRefId, event.getSceneRefId())) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_INVASIOIN_WRONG_SCENE);
			return;
		}

		int pointLevel = monsterInvasionMgr1.getMonsterIntrusionRef().getLevel();// 判断等级
		if (concreteParent.getLevel() < pointLevel) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_INVASIOIN_NOT_ENOUGHT_LEVEL);
			return;
		}
		
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		GameScene destScene = gameArea.getSceneById(sceneRefId);
		SceneTransInRegion sceneTransInRegion = destScene.getRef().getTransInRegions().get(0);
		SceneGrid sceneGrid = sceneTransInRegion.getRegion().getRandomUnblockedGrid();

		concreteParent.getPlayerSceneComponent().switchTo(destScene, sceneGrid.getColumn(), sceneGrid.getRow());
		concreteParent.getExpComponent().addExpMultiple((float) monsterInvasionMgr1.getMonsterIntrusionRef().getExpMultiple());
		// 发送倒数时间给客户端
		G2C_MonsterIntrusion_EnterMap enterMap = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_EnterMap);
		enterMap.setContinuTime(monsterInvasionMgr1.getActivityRemainingEndTime());
		enterMap.setExpMultiple(monsterInvasionMgr1.getMonsterIntrusionRef().getExpMultiple());
		GameRoot.sendMessage(identity, enterMap);
		//monsterInvasionMgr.checkMonsterReflesh();
		refreshSelfFont();
	}

	public void refreshSelfFont() {
		int crtNumber = ItemFacade.getNumber(getConcreteParent(), "item_coupon");
		int crtNumber2 = ItemFacade.getNumber(getConcreteParent(), "item_coupon_2");
		crtNumber = crtNumber + crtNumber2 * 20;
		MonsterInvasionScrollRef ref1 = (MonsterInvasionScrollRef) GameRoot.getGameRefObjectManager().getManagedObject("monsterInvasionScroll_1");
		MonsterInvasionScrollRef ref2 = (MonsterInvasionScrollRef) GameRoot.getGameRefObjectManager().getManagedObject("monsterInvasionScroll_2");
		MonsterInvasionScrollRef ref3 = (MonsterInvasionScrollRef) GameRoot.getGameRefObjectManager().getManagedObject("monsterInvasionScroll_3");
		byte fontType = 0;
		if (crtNumber >= Integer.parseInt(StringUtils.split(ref1.getRange(), "|")[0]) && crtNumber <= Integer.parseInt(StringUtils.split(ref1.getRange(), "|")[1])) {
			fontType = 1;
		} else if (crtNumber > Integer.parseInt(StringUtils.split(ref2.getRange(), "|")[0]) && crtNumber <= Integer.parseInt(StringUtils.split(ref2.getRange(), "|")[1])) {
			fontType = 2;
		} else if (crtNumber >= Integer.parseInt(ref3.getRange())) {
			fontType = 3;
		}
		changeFontTypeBroadcast(fontType);
	}

	public void changeFontTypeBroadcast(byte fontType) {
		Player player = getConcreteParent();
		sendFontTypeToSelf(fontType);
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMonsterInvasionFont(pd, fontType);
		MGPropertyAccesser.setOrPutMonsterInvasionFont(player.getSenceProperty(), fontType);
		player.getAoiComponent().broadcastProperty(pd);
	}

	public void sendFontTypeToSelf(byte fontType) {
		G2C_MonsterIntrusion_Font res = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_Font);
		res.setFontType(fontType);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	public void bossRefreshTime(String monsterRefId, String sceneRefId, long time, byte isDead) {
		sendingBossTimeRefresh(monsterRefId, sceneRefId, time, isDead);
	}

	public void sendingBossTimeRefresh(String monsterRefId, String sceneRefId, long time, byte isDead) {
		G2C_MonsterIntrusion_BossTimeRefresh bossTimeRefresh = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_BossTimeRefresh);
		bossTimeRefresh.setMonsterRefId(monsterRefId);
		bossTimeRefresh.setSceneRefId(sceneRefId);
		bossTimeRefresh.setRefreshTime(time);
		bossTimeRefresh.setIsDead(isDead);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), bossTimeRefresh);
	}

	public ComeFromScene getComeFromScene() {
		return comeFromScene;
	}

	public void setComeFromScene(ComeFromScene comeFromScene) {
		this.comeFromScene = comeFromScene;
	}

}
