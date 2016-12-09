package newbee.morningGlory.mmorpg.sceneActivities.teamBoss;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref.TeamBossRef;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.LeaveTeam_GE;
import sophia.mmorpg.player.gameEvent.PlayerSwitchScene_GE;
import sophia.mmorpg.player.gameEvent.SinglePlayerTeam_GE;
import sophia.mmorpg.player.team.PlayerTeamManagerComponent;
import sophia.mmorpg.player.team.actionEvent.TeamEventDefines;
import sophia.mmorpg.player.team.actionEvent.activity.C2G_PlayerTeamBoss_RequestTime;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_PreStart;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_RequestTime;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_Show;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_Start;

public class MGPlayerTeamComponent extends ConcreteComponent<Player> {
	private Player player = null;
	public static final String Tag = "MGPlayerTeamComponent";
	private String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private String LeaveTeam_GE_Id = LeaveTeam_GE.class.getSimpleName();
	private String SinglePlayerTeam_GE_Id = SinglePlayerTeam_GE.class.getSimpleName();
	private String PlayerSwitchScene_GE_Id = PlayerSwitchScene_GE.class.getSimpleName();

	@Override
	public void ready() {
		player = getConcreteParent();
		addInterGameEventListener(LeaveTeam_GE_Id);
		addInterGameEventListener(SinglePlayerTeam_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		addInterGameEventListener(PlayerSwitchScene_GE_Id);
		addActionEventListener(TeamEventDefines.C2G_PlayerTeamBoss_RequestTime);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(LeaveTeam_GE_Id);
		removeInterGameEventListener(SinglePlayerTeam_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		removeInterGameEventListener(PlayerSwitchScene_GE_Id);
		removeActionEventListener(TeamEventDefines.C2G_PlayerTeamBoss_RequestTime);
		super.suspend();
	}
	
	private void sendStartTeamBossMessage() {
		List<TeamBossMgr> staredTeamBossMgr = getStaredTeamBossMgr();
		for (TeamBossMgr mgr : staredTeamBossMgr) {
			String refId = mgr.getRef().getSceneRefId();
			if (StringUtils.equals(refId, player.getSceneRefId())) {
				if (player.getPlayerTeamComponent().getTeam() == null) {
					mgr.transferOut(player);
				} else if (player.getPlayerTeamComponent().getTeam().getTeamMemberNum() == 1) {
					mgr.transferOut(player);
				}
			}

			G2C_PlayerTeamBoss_Start teamBossStart = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamBoss_Start);
			teamBossStart.setType(mgr.getTeamBossType());
			GameRoot.sendMessage(player.getIdentity(), teamBossStart);
		}
	}
	
	private void sendPreStartTeamBossMessage() {
		List<TeamBossMgr> preStaredTeamBossMgr = getPreStaredTeamBossMgr();
		for (TeamBossMgr mgr : preStaredTeamBossMgr) {
			String refId = mgr.getRef().getSceneRefId();
			if (StringUtils.equals(refId, player.getSceneRefId())) {
				if (player.getPlayerTeamComponent().getTeam() == null) {
					mgr.transferOut(player);
				} else if (player.getPlayerTeamComponent().getTeam().getTeamMemberNum() == 1) {
					mgr.transferOut(player);
				}
			}

			G2C_PlayerTeamBoss_PreStart teamBossPreStart = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamBoss_PreStart);
			teamBossPreStart.setType(mgr.getTeamBossType());
			GameRoot.sendMessage(player.getIdentity(), teamBossPreStart);
		}
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			sendStartTeamBossMessage();
			sendPreStartTeamBossMessage();
		} else if (event.isId(LeaveTeam_GE_Id)) {
			List<TeamBossMgr> staredTeamBossMgr = getStaredTeamBossMgr();
			for (TeamBossMgr mgr : staredTeamBossMgr) {
				String refId = mgr.getRef().getSceneRefId();
				if (StringUtils.equals(refId, player.getSceneRefId())) {
					mgr.transferOut(player);
				}
			}
		} else if (event.isId(SinglePlayerTeam_GE_Id)) {
			List<TeamBossMgr> staredTeamBossMgr = getStaredTeamBossMgr();
			for (TeamBossMgr mgr : staredTeamBossMgr) {
				String refId = mgr.getRef().getSceneRefId();
				if (StringUtils.equals(refId, player.getSceneRefId())) {
					mgr.transferOut(player);
				}
			}
		} else if (event.isId(PlayerSwitchScene_GE_Id)) {
			PlayerSwitchScene_GE ge = (PlayerSwitchScene_GE) event.getData();
			GameScene fromGameScene = ge.getFromScene();
			GameScene desGameScene = ge.getDstScene();
			if (fromGameScene.getRef().getType() != SceneRef.Activity && desGameScene.getRef().getType() == SceneRef.Activity) {
				List<TeamBossMgr> staredTeamBossMgr = getStaredTeamBossMgr();
				for (TeamBossMgr mgr : staredTeamBossMgr) {
					if (StringUtils.equals(mgr.getRef().getSceneRefId(), desGameScene.getRef().getId())) {
						G2C_PlayerTeamBoss_Show requestTime = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamBoss_Show);
						GameRoot.sendMessage(player.getIdentity(), requestTime);
					}
				}
			}
		}
		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		switch (actionEventId) {
		case TeamEventDefines.C2G_PlayerTeamBoss_RequestTime:
			handle_TeamBoss_RequestTime((C2G_PlayerTeamBoss_RequestTime) event);
			break;
		default:
			break;
		}
		super.handleActionEvent(event);
	}

	private void handle_TeamBoss_RequestTime(C2G_PlayerTeamBoss_RequestTime event) {
		byte type = event.getType();
		for (String sceneRefId : PlayerTeamManagerComponent.teamBossScene) {
			TeamBossMgr mgr = (TeamBossMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId(sceneRefId);
			if (mgr == null) {
				continue;
			}
			TeamBossRef componentRef = mgr.getRef().getComponentRef(TeamBossRef.class);
			if (componentRef.getType() == type) {
				G2C_PlayerTeamBoss_RequestTime requestTime = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamBoss_RequestTime);
				requestTime.setTimeToStart(mgr.getActivityRemainStartTime());
				requestTime.setTimeToEnd(mgr.getActivityRemainEndTime());
				GameRoot.sendMessage(event.getIdentity(), requestTime);
				break;
			}
		}
	}

	private List<TeamBossMgr> getStaredTeamBossMgr() {
		List<TeamBossMgr> list = new ArrayList<>();
		for (String sceneRefId : PlayerTeamManagerComponent.teamBossScene) {
			TeamBossMgr teamBossMgrTemp = (TeamBossMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId(sceneRefId);
			if (teamBossMgrTemp == null) {
				continue;
			}
			if (teamBossMgrTemp.isTeamBossStart()) {
				list.add(teamBossMgrTemp);
			}
		}
		return list;
	}
	
	private List<TeamBossMgr> getPreStaredTeamBossMgr() {
		List<TeamBossMgr> list = new ArrayList<>();
		for (String sceneRefId : PlayerTeamManagerComponent.teamBossScene) {
			TeamBossMgr teamBossMgrTemp = (TeamBossMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId(sceneRefId);
			if (teamBossMgrTemp == null) {
				continue;
			}
			if (teamBossMgrTemp.getPreActivityState() == 3) {
				list.add(teamBossMgrTemp);
			}
		}
		return list;
	}
}
