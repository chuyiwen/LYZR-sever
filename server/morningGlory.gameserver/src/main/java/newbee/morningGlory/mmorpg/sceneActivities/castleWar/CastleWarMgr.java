package newbee.morningGlory.mmorpg.sceneActivities.castleWar;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.castleWar.MGCastleWarComponent;
import newbee.morningGlory.mmorpg.player.castleWar.event.CastleWarActionEventDefines;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_End;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_Enter;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_Exit;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_MonsterRefresh;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_PreStart;
import newbee.morningGlory.mmorpg.player.castleWar.event.G2C_CastleWar_Start;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.CastleWarEnd_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarRef;
import newbee.morningGlory.mmorpg.union.MGUnion;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;
import newbee.morningGlory.mmorpg.union.MGUnionMember;
import newbee.morningGlory.mmorpg.union.MGUnionMgr;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatCastleWar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.task.Task;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class CastleWarMgr extends SceneActivity {
	private final static Logger logger = Logger.getLogger(CastleWarMgr.class);
	private volatile boolean castleWarStart = false;
	private CastleWarMonster castleMonster = new CastleWarMonster();
	private String monsterOwner;
	private int killMonsterTime = 0;

	public String getMonsterOwner() {
		return monsterOwner;
	}

	public void setMonsterOwner(String monsterOwner) {
		this.monsterOwner = monsterOwner;
	}

	public CastleWarRef getCastleWarRef() {
		return getRef().getComponentRef(CastleWarRef.class);
	}

	public CastleWarMgr() {
	}

	@Override
	public boolean onPreStart() {
		logger.info("CastleWarMgr.onPreStart()");
		broadCastCastleWarPreStart();
		sendCastleWarPreStartMessage();
		kickOutAllPlayer();
		return false;
	}

	@Override
	public boolean onPreEnd() {
		return false;
	}

	@Override
	public boolean onStart() {
		castleWarStart();
		return false;
	}
	
	@Override
	public boolean onCheckEnd(){
		return true;
	}
	
	@Override
	public boolean onEnd() {
		castleWarEnd();
		return false;
	}

	@Override
	public boolean checkEnter(Player player) {
		boolean ret = false;
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		String kingCityUnionName = unionMgr.getKingCityUnionName();
		if (!isCastleWarStart()) {
			// 攻城战尚未开始
			String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
			if (unionName != null && StringUtils.equals(unionName, kingCityUnionName)) {
				sendEnterMessage(player);
				ret = true;
			} else {
				ResultEvent.sendResult(player.getIdentity(), (short) 351, MGErrorCode.CODE_CASTLEWAR_NO_KINGCITY);
			}
			return ret;
		}
		PropertyDictionary pd = player.getProperty();
		String unionName = MGPropertyAccesser.getUnionName(pd);
		if (StringUtils.equals(kingCityUnionName, unionName)) {
			sendEnterMessage(player);
			return true;
		} else if (StringUtils.isEmpty(unionName) || !CastleWarApplyMgr.getInstance().isAlreadySignupWar(unionName)) {
			ResultEvent.sendResult(player.getIdentity(), (short) 351, MGErrorCode.CODE_CASTLEWAR_STARTED);
			return ret;
		}
		sendEnterMessage(player);
		ret = true;
		return ret;
	}

	@Override
	public boolean checkLeave(Player player) {
		sendLeaveMessage(player);
		return true;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
	}

	/**
	 * 攻城战开始，（参与攻城战的攻守方公会都能够进入地图）
	 */
	public void castleWarStart() {
		setCastleWarStart(true);
		sendCastleWarStartMessage();
		broadCastCastleWarStart();
		castleMonster.createMonster(getActivityType());
	}

	/**
	 * 攻城战结束
	 */
	public void castleWarEnd() {
		sendCastleWarEndMessage();
		sendCastleWarEndGameEvent();
		monsterLeaveWorld();
		setCastleWarStart(false);
		resetMonsterKillTime();
		kickOutAllPlayer();
		CastleWarApplyMgr.getInstance().clearSignupWarUnions();
		broadCastNewKingUnion();
	}
	
	/*
	 * 统计攻城战结果，并发送gameEvent事件
	 */
	private void sendCastleWarEndGameEvent() {
		logger.info("send castleWar end game event!");
		CastleWarEnd_GE castleWarEndGE = new CastleWarEnd_GE();
		GameEvent<CastleWarEnd_GE> event = GameEvent.getInstance(CastleWarEnd_GE.class.getSimpleName(), castleWarEndGE);
		
		Collection<Player> onlinePlayerList = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayerList();
		for (Player player : onlinePlayerList) {
			MGCastleWarComponent caltleWarCastleWarComponent = (MGCastleWarComponent)player.getTagged(MGCastleWarComponent.Tag);
			caltleWarCastleWarComponent.recordCastleWarResult();
			player.handleGameEvent(event);
			GameEvent.pool(event);
		}
	}


	/**
	 * 将地图中所有人踢出地图
	 */
	private void kickOutAllPlayer() {
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		String warScene = getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
		GameScene gameScene = gameArea.getSceneById(warScene);
		if (gameScene == null) {
			return;
		}
		String outScene = getCastleWarRef().getCastleWarOutSceneTransfer().getTargetScene();
		int outSceneTranferInId = getCastleWarRef().getCastleWarOutSceneTransfer().getTranferInId();

		Map<String, Player> playerMap = gameScene.getPlayerMgrComponent().getPlayerMap();
		for (Entry<String, Player> entry : playerMap.entrySet()) {
			if (entry.getValue().isDead()) {
				entry.getValue().goHome();
			} else {
				PlayerSceneComponent playerSceneComponent = entry.getValue().getPlayerSceneComponent();
				playerSceneComponent.switchToByTransId(outScene, outSceneTranferInId);
			}
		}
	}

	private void monsterLeaveWorld() {
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		String warScene = getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
		GameScene gameScene = gameArea.getSceneById(warScene);
		if (gameScene == null) {
			return;
		}

		Collection<Monster> allMonsters = gameScene.getMonsterMgrComponent().getAllMonsters();
		for (Monster monster : allMonsters) {
			gameScene.getMonsterMgrComponent().leaveWorld(monster);
		}
		if (!StringUtils.isEmpty(monsterOwner)) {
			resetKingCityUnion(monsterOwner);
		}
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		if (monsterOwner == null) {
			return;
		}
		MGUnion union = unionMgr.getUnion(monsterOwner);
		if(union == null){
			return;
		}
		List<MGUnionMember> members = union.getMemberMgr().getMemberList();
		for (MGUnionMember member : members) {
			Player player = playerManager.getPlayer(member.getPlayerId());
			MGStatFunctions.castleWarStat(player, StatCastleWar.Finish, monsterOwner, member.getUnionOfficialId());
		}
	}

	public void transferOut(Player player) {
		String crtSceneRefId = player.getCrtScene().getRef().getId();
		String targetScene = getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
		if (StringUtils.equals(crtSceneRefId, targetScene)) {
			String warScene = getCastleWarRef().getCastleWarOutSceneTransfer().getTargetScene();
			int tranferInId = getCastleWarRef().getCastleWarOutSceneTransfer().getTranferInId();
			PlayerSceneComponent playerSceneComponent = player.getPlayerSceneComponent();
			playerSceneComponent.switchToByTransId(warScene, tranferInId);
		}
	}

	public void transferIn(Player player) {
		String crtSceneRefId = player.getCrtScene().getRef().getId();
		String targetScene = getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
		if (!StringUtils.equals(crtSceneRefId, targetScene)) {
			sendEnterMessage(player);
			String warScene = getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
			int tranferInId = getCastleWarRef().getCastleWarSceneTransfer().getTranferInId();
			PlayerSceneComponent playerSceneComponent = player.getPlayerSceneComponent();
			playerSceneComponent.switchToByTransId(warScene, tranferInId);
		}
	}

	/**
	 * 重新设置新的王城公会
	 */
	public void resetKingCityUnion(String unionName) {
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		
		MGUnion union = unionMgr.getUnion(unionName);
		if (union == null) {
			logger.error("union of = " + unionName + " is not exist!");
			return;
		}
	
		MGUnionHelper.changeKingCityUnion(union);
	}

	/**
	 * 广播新的王城公会
	 */
	public void broadCastNewKingUnion() {
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		String unionName = unionMgr.getKingCityUnionName();
		SystemPromptFacade.broadCastNewKingUnion(unionName);
	}

	/**
	 * 攻城战开始前广播
	 */
	public void broadCastCastleWarPreStart() {
		SystemPromptFacade.broadCastCastleWarPreStart();
	}

	/**
	 * 攻城战开始广播
	 */
	public void broadCastCastleWarStart() {
		SystemPromptFacade.broadCastCastleWarStart();
	}

	/**
	 * 玩家击杀麒麟广播
	 */
	public void broadCastCastleWarMonsterDead(final Player player) {
		String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
		SystemPromptFacade.broadCastCastleWarMonsterDead(unionName, player.getName());
		GameContext.getTaskManager().scheduleTask(new Task() {
			@Override
			public void run() throws Exception {
				String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
				SystemPromptFacade.broadCastCastleWarMonsterDead(unionName, player.getName());
			}
		}, 5 * 1000);

		GameContext.getTaskManager().scheduleTask(new Task() {
			@Override
			public void run() throws Exception {
				String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
				SystemPromptFacade.broadCastCastleWarMonsterDead(unionName, player.getName());
			}
		}, 10 * 1000);
	}

	public void sendEnterMessage(Player player) {
		if (StringUtils.isEmpty(monsterOwner)) {
			monsterOwner = MorningGloryContext.getUnionSystemComponent().getUnionMgr().getKingCityUnionName();
		}
		G2C_CastleWar_Enter enter = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_Enter);
		enter.setMonsterOwner(monsterOwner);
		GameRoot.sendMessage(player.getIdentity(), enter);
		String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
		MGStatFunctions.castleWarStat(player, StatCastleWar.Enter, unionName, (byte) 0);
	}

	public void sendLeaveMessage(Player player) {
		G2C_CastleWar_Exit exit = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_Exit);
		GameRoot.sendMessage(player.getIdentity(), exit);
		String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
		MGStatFunctions.castleWarStat(player, StatCastleWar.Leave, unionName, (byte) 0);
	}

	public void sendMonsterRefreshMessage() {
		String sceneRefId = getRef().getSceneRefId();
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		GameScene gameScene = gameArea.getSceneById(sceneRefId);
		Map<String, Player> playerMap = gameScene.getPlayerMgrComponent().getPlayerMap();

		for (Entry<String, Player> entry : playerMap.entrySet()) {
			G2C_CastleWar_MonsterRefresh monsterRefresh = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_MonsterRefresh);
			monsterRefresh.setMonsterOwner(monsterOwner);
			GameRoot.sendMessage(entry.getValue().getIdentity(), monsterRefresh);
		}
	}

	public void sendCastleWarPreStartMessage() {
		Collection<Player> onlinePlayerList = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayerList();
		for (Player player : onlinePlayerList) {
			G2C_CastleWar_PreStart warPreStart = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_PreStart);
			GameRoot.sendMessage(player.getIdentity(), warPreStart);
		}
	}

	public void sendCastleWarStartMessage() {
		Collection<Player> onlinePlayerList = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayerList();
		for (Player player : onlinePlayerList) {
			G2C_CastleWar_Start castleWarStart = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_Start);
			GameRoot.sendMessage(player.getIdentity(), castleWarStart);
		}
	}

	public void sendCastleWarEndMessage() {
		Collection<Player> onlinePlayerList = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayerList();
		for (Player player : onlinePlayerList) {
			G2C_CastleWar_End warPreStart = MessageFactory.getConcreteMessage(CastleWarActionEventDefines.G2C_CastleWar_End);
			GameRoot.sendMessage(player.getIdentity(), warPreStart);
		}
	}

	public void refreshMonster(Player player) {
		if (!castleWarStart) {
			return;
		}
		killMonsterTime++;
		castleMonster.refreshMonster(player, killMonsterTime);
	}

	private void resetMonsterKillTime() {
		killMonsterTime = 0;
	}

	public boolean isCastleWarStart() {
		return castleWarStart;
	}

	public void setCastleWarStart(boolean castleWarStart) {
		this.castleWarStart = castleWarStart;
	}

	public void setCrtActivityState(byte temp) {
		this.crtActivityState = temp;
	}

	public void setPreActivityState(byte temp) {
		this.preActivityState = temp;
	}

	/**
	 * 距离活动开始时间
	 * 
	 * @return
	 */
	public long getActivityRemainingStartTime() {
		if (castleWarStart) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainStartTime();
	}

	/**
	 * 距离活动结束时间
	 * 
	 * @return
	 */
	public long getActivityRemainingEndTime() {
		if (!castleWarStart) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainEndTime();
	}

	@Override
	public boolean onEnter(Player player) {
		//transferIn(player);
		return false;
	}

	@Override
	public boolean onLeave(Player player) {
		transferOut(player);
		return false;
	}
}
