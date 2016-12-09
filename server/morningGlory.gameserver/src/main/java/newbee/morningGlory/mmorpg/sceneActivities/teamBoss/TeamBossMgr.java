package newbee.morningGlory.mmorpg.sceneActivities.teamBoss;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref.TeamBossRef;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref.TeamBossTransferIn;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref.TeamBossTransferOut;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.player.team.actionEvent.TeamEventDefines;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_End;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_PreStart;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_Start;

public class TeamBossMgr extends SceneActivity {
	private static final Logger logger = Logger.getLogger(TeamBossMgr.class);
	private boolean teamBossStart = false;
	public static final int minTeamMemerLimit = 2;
	
	public boolean isTeamBossStart() {
		return teamBossStart;
	}

	public void setTeamBossStart(boolean teamBossStart) {
		this.teamBossStart = teamBossStart;
	}

	@Override
	public boolean checkEnter(Player player) {
		if (getPreActivityState() == 3 && !isTeamBossStart()) {
			return false;
		}
		if (!isTeamBossStart()) {
			return false;
		}
		return true;
		//return transferIn(player);
	}

	@Override
	public boolean checkLeave(Player player) {
		if (!isTeamBossStart()) {
			return true;
		}
		return transferOut(player);
	}

	@Override
	public boolean onPreStart() {
		logger.info("TeamBossMgr onPreStart");
		sendTeamBossPreStartMessage();
		return false;
	}

	@Override
	public boolean onPreEnd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onStart() {
		logger.info("TeamBossMgr onStart");
		sendTeamBossStartMessage();
		setTeamBossStart(true);
		return false;
	}

	@Override 
	public boolean onCheckEnd() {
		return true;
	}
	
	@Override
	public boolean onEnd() {
		logger.info("TeamBossMgr onEnd");
		sendTeamBossEndMessage();
		setTeamBossStart(false);
		kickOutPlayer();
		return false;
	}

	@Override
	public boolean onEnter(Player player) {	
		if (!isTeamBossStart()) {
			return false;
		}
		
		return transferIn(player);
	}

	public boolean transferIn(Player player) {
		if (player.getPlayerTeamComponent().getTeam() != null && player.getPlayerTeamComponent().getTeam().getMembers().size() >= minTeamMemerLimit) {
			TeamBossTransferIn transferIn = ((TeamBossRef)getRef().getComponentRef(TeamBossRef.class)).getTransferIn();
			String targetScene = transferIn.getTargetScene();
			int tranferInId = transferIn.getTranferInId();
			if (transfer(targetScene, tranferInId, player)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean transfer(String targetScene, int tranferInId, Player player) {
		PlayerSceneComponent playerSceneComponent = player.getPlayerSceneComponent();
		
		AbstractGameSceneRef dstRef = (AbstractGameSceneRef) (GameRoot.getGameRefObjectManager().getManagedObject(targetScene));

		SceneTransInRegion transIn = null;
		List<SceneTransInRegion> transInRegionLst = dstRef.getTransInRegions();
		for (SceneTransInRegion transInRegion : transInRegionLst) {
			if (transInRegion.getId() == tranferInId) {
				transIn = transInRegion;
				break;
			}
		}
		if (transIn == null) {
			return false;
		}
		SceneGrid sceneGrid = transIn.getRegion().getRandomUnblockedGrid();
		playerSceneComponent.transferTo(targetScene, sceneGrid.getColumn(), sceneGrid.getRow());
		return true;
	}
	
	@Override
	public boolean onLeave(Player player) {
		transferOut(player);
		return true;
	}
	
	public void kickOutPlayer() {
		List<String> contentScene = ((TeamBossRef)getRef().getComponentRef(TeamBossRef.class)).getContentScene();
		for (String sceneRefId : contentScene) {
			String thisSceneRefId = getRef().getSceneRefId();
			if (StringUtils.equals(thisSceneRefId, sceneRefId)) {
				GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
				GameScene gameScene = gameArea.getSceneById(sceneRefId);
				if (gameScene == null) {
					return;
				}
				Map<String, Player> playerMap = gameScene.getPlayerMgrComponent().getPlayerMap();
				for (Entry<String, Player> entry : playerMap.entrySet()) {
					transferOut(entry.getValue());
				}
			}
		}
	}
	
	public boolean transferOut(Player player) {
		int playerFightPower = player.getFightPower();
		int sceneFightPowerLimit = player.getCrtScene().getRef().getFightPower();
		if (playerFightPower >= sceneFightPowerLimit) {
			return true;
		}
		TeamBossTransferOut transferOut = ((TeamBossRef)getRef().getComponentRef(TeamBossRef.class)).getTransferOut();
		String targetScene = transferOut.getTargetScene();
		int tranferInId = transferOut.getTranferInId();
		if (player.isDead()) {
			player.goHome();
			return false;
		} else {
			if (!transfer(targetScene, tranferInId, player)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		// TODO Auto-generated method stub
	}
	
	public void sendTeamBossPreStartMessage() {
		byte type = getTeamBossType();
		Collection<Player> onlinePlayerList = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayerList();
		for (Player player : onlinePlayerList) {
			G2C_PlayerTeamBoss_PreStart teamBossPreStart = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamBoss_PreStart);
			teamBossPreStart.setType(type);
			GameRoot.sendMessage(player.getIdentity(), teamBossPreStart);
		}
	}
	
	public void sendTeamBossStartMessage() {
		byte type = getTeamBossType();
		Collection<Player> onlinePlayerList = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayerList();
		for (Player player : onlinePlayerList) {
			G2C_PlayerTeamBoss_Start teamBossStart = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamBoss_Start);
			teamBossStart.setType(type);
			GameRoot.sendMessage(player.getIdentity(), teamBossStart);
		}
	}

	public void sendTeamBossEndMessage() {
		byte type = getTeamBossType();
		Collection<Player> onlinePlayerList = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayerList();
		for (Player player : onlinePlayerList) {
			G2C_PlayerTeamBoss_End teamBossEnd = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamBoss_End);
			teamBossEnd.setType(type);
			GameRoot.sendMessage(player.getIdentity(), teamBossEnd);
		}
	}
	
	public byte getTeamBossType() {
		return ((TeamBossRef)getRef().getComponentRef(TeamBossRef.class)).getType();
	}
	
	/**
	 * 距离活动开始时间
	 * 
	 * @return
	 */
	public long getActivityRemainStartTime() {
		if (teamBossStart) {
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
	public long getActivityRemainEndTime() {
		if (!teamBossStart) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainEndTime();
	}

	public void setPreActivityState(byte temp) {
		this.preActivityState = temp;
	}
	
	public void setCrtActivityState(byte temp) {
		this.crtActivityState = temp;
	}
}
