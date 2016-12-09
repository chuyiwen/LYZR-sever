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
package sophia.mmorpg.player.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.team.actionEvent.ReplyJoinTeamType;
import sophia.mmorpg.player.team.actionEvent.TeamEventDefines;
import sophia.mmorpg.player.team.actionEvent.assemble.C2G_AssembleTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.assemble.G2C_AssembleTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.broadcast.BroadcastTeamFacade;
import sophia.mmorpg.player.team.actionEvent.create.C2G_PlayerTeam_CreateEvent;
import sophia.mmorpg.player.team.actionEvent.create.G2C_PlayerTeam_CreateEvent;
import sophia.mmorpg.player.team.actionEvent.disband.C2G_DisbandTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.disband.G2C_DisbandTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.handover.C2G_HandoverTeamLeaderActionEvent;
import sophia.mmorpg.player.team.actionEvent.handover.G2C_HandoverTeamLeaderActionEvent;
import sophia.mmorpg.player.team.actionEvent.info.C2G_PlayerInfoActionEvent;
import sophia.mmorpg.player.team.actionEvent.info.C2G_PlayerTeam_InfomationEvent;
import sophia.mmorpg.player.team.actionEvent.info.C2G_PlayerTeam_Modify;
import sophia.mmorpg.player.team.actionEvent.info.C2G_TeamMenber_Detail;
import sophia.mmorpg.player.team.actionEvent.info.G2C_PlayerInfoActionEvent;
import sophia.mmorpg.player.team.actionEvent.info.G2C_PlayerTeam_InfomationEvent;
import sophia.mmorpg.player.team.actionEvent.info.G2C_PlayerTeam_Modify;
import sophia.mmorpg.player.team.actionEvent.info.G2C_TeamMenber_Detail;
import sophia.mmorpg.player.team.actionEvent.join.C2G_JoinRequestReplyActionEvent;
import sophia.mmorpg.player.team.actionEvent.join.C2G_JoinTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.join.C2G_PlayerTeam_JoinRequestEvent;
import sophia.mmorpg.player.team.actionEvent.join.G2C_JoinRequestReplyActionEvent;
import sophia.mmorpg.player.team.actionEvent.join.G2C_JoinTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.join.G2C_PlayerTeam_JoinRequestEvent;
import sophia.mmorpg.player.team.actionEvent.kickedOut.C2G_KickedOutTeamMemberActionEvent;
import sophia.mmorpg.player.team.actionEvent.kickedOut.G2C_KickedOutTeamMemberActionEvent;
import sophia.mmorpg.player.team.actionEvent.leave.C2G_LeaveTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.leave.G2C_LeaveTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.setting.C2G_PlayerTeamSettingActionEvent;
import sophia.mmorpg.player.team.actionEvent.setting.G2C_PlayerTeamSettingActionEvent;
import sophia.mmorpg.player.team.gameEvent.TeamGameEvent;
import sophia.mmorpg.sceneActivities.SceneActivityInterface;
import sophia.mmorpg.sceneActivities.SceneActivityMgrInterface;
import sophia.mmorpg.utils.RuntimeResult;

public final class PlayerTeamComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerTeamComponent.class);

	public static final String TeamGameEvent_ID = TeamGameEvent.class.getSimpleName();
	
	private PlayerTeam team = null;
	private Player player;
	private final PlayerTeamSetting teamSetting = new PlayerTeamSetting();
	private static PlayerTeamManagerComponent playerTeamManagerComponent = MMORPGContext.playerTeamManagerComponent();
	private final Map<Integer, Integer> levelLimit = new HashMap<Integer, Integer>() {
		{
			put(1, 40);
			put(2, 45);
			put(3, 50);
			put(4, 55);
			put(5, 60);
			put(6, 70);
			put(7, 80);
		}
	};

	private final CopyOnWriteArrayList<String> crtInvitePlayerMembers = new CopyOnWriteArrayList<>(); // 邀请的玩家列表
	private final CopyOnWriteArrayList<String> crtRequestJoinPlayers = new CopyOnWriteArrayList<>(); // 申请加入的玩家列表
	private String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private final static int INVITIED_JOIN = 1;
	private final static int REQUEST_JOIN = 2;
	private final static int ME_JOIN = 3;// 自己创建
	public static final int INVITE_PLAYER_MEMBERS_LENGTH = 160;

	@Override
	public void ready() {
		player = getConcreteParent();
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
		addInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		addActionEventListener(TeamEventDefines.C2G_PlayerInfoActionEvent);
		addActionEventListener(TeamEventDefines.C2G_AssembleTeamActionEvent);
		addActionEventListener(TeamEventDefines.C2G_JoinTeamActionEvent);
		addActionEventListener(TeamEventDefines.C2G_KickedOutTeamMemberActionEvent);
		addActionEventListener(TeamEventDefines.C2G_LeaveTeamActionEvent);
		addActionEventListener(TeamEventDefines.C2G_HandoverTeamLeaderActionEvent);
		addActionEventListener(TeamEventDefines.C2G_DisbandTeamActionEvent);
		addActionEventListener(TeamEventDefines.C2G_PlayerTeamSettingActionEvent);
		addActionEventListener(TeamEventDefines.C2G_PlayerTeam_CreateEvent);
		addActionEventListener(TeamEventDefines.C2G_PlayerTeam_JoinRequestEvent);
		addActionEventListener(TeamEventDefines.C2G_PlayerTeam_InfomationEvent);
		addActionEventListener(TeamEventDefines.C2G_JoinRequestReplyActionEvent);
		addActionEventListener(TeamEventDefines.C2G_PlayerTeam_Modify);
		addActionEventListener(TeamEventDefines.C2G_TeamMenber_Detail);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
		removeInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		removeActionEventListener(TeamEventDefines.C2G_PlayerInfoActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_AssembleTeamActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_JoinTeamActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_KickedOutTeamMemberActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_LeaveTeamActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_HandoverTeamLeaderActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_DisbandTeamActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_PlayerTeamSettingActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_PlayerTeam_CreateEvent);
		removeActionEventListener(TeamEventDefines.C2G_PlayerTeam_JoinRequestEvent);
		removeActionEventListener(TeamEventDefines.C2G_PlayerTeam_InfomationEvent);
		removeActionEventListener(TeamEventDefines.C2G_JoinRequestReplyActionEvent);
		removeActionEventListener(TeamEventDefines.C2G_PlayerTeam_Modify);
		removeActionEventListener(TeamEventDefines.C2G_TeamMenber_Detail);

		clearInviteAndRequestList();
		super.suspend();
	}

	public PlayerTeamComponent() {

	}

	public void clearInviteAndRequestList() {
		if (crtInvitePlayerMembers != null) {
			crtInvitePlayerMembers.clear();
		}
		if (crtRequestJoinPlayers != null) {
			crtRequestJoinPlayers.clear();
		}
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerDead_GE.class.getSimpleName())) {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家死亡通知," + player.isDead() + ",hasTeam=" + this.hasTeam());
			}
			if (player.isDead() && this.hasTeam()) {
				BroadcastTeamFacade.broadcastDeadTeamMember(team, player);
			}
		} else if (event.isId(PlayerManager.LeaveWorld_GE_Id)) {
			if (logger.isDebugEnabled()) {
				logger.debug("收到玩家离开世界事件" + player);
			}

			if (this.hasTeam()) {
				playerTeamManagerComponent.addPlayerTeamCache(player, team);
			}
		} else if (event.isId(Monster.MonsterDead_GE_Id)) {
			MonsterDead_GE monsterDead_GE = (MonsterDead_GE) event.getData();
			FightSprite attacker = monsterDead_GE.getAttacker();
			if (StringUtils.equals(attacker.getId(), getConcreteParent().getId()) && team != null) {
				List<Player> otherMember = team.getCrtSceneOtherMember(getConcreteParent());
				GameEvent<MonsterDead_GE> monsterDead = GameEvent.getInstance(Monster.MonsterDead_GE_Id, monsterDead_GE);
				
				for (Player player : otherMember) {
					if (StringUtils.equals(player.getId(), getConcreteParent().getId()) || !StringUtils.equals(player.getCrtScene().getId(), attacker.getCrtScene().getId())) {
						continue;
					}
					player.handleGameEvent(monsterDead);
				}
				
				GameEvent.pool(monsterDead);
			}
		} else if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			if (team == null) {
				return;
			}

			if (playerTeamManagerComponent.hasGameInstanceCache(player)) {
				playerTeamManagerComponent.removePlayerTeamCache(player);
			}

			Collection<Player> notifyPlayerList = new ArrayList<>();
			for (String playerId : team.getMembers()) {
				Player player = getPlayer(playerId);
				if (player != null)
					notifyPlayerList.add(player);
			}

			if (notifyPlayerList.size() > 0)
				BroadcastTeamFacade.broadcastJoinTeam(team, notifyPlayerList);
		}
		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		switch (event.getActionEventId()) {
		case TeamEventDefines.C2G_PlayerInfoActionEvent:
			// 1.查看玩家信息（已组队/已经是你的的队员/邀请组队）
			handle_PlayerInfoActionEvent((C2G_PlayerInfoActionEvent) event);
			break;
		case TeamEventDefines.C2G_AssembleTeamActionEvent:
			// 2.邀请组队(发起人+邀请目标)
			handle_AssembleTeamActionEvent((C2G_AssembleTeamActionEvent) event);
			break;
		case TeamEventDefines.C2G_JoinTeamActionEvent:
			// 3.通过别人邀请的组队
			handle_PassJoinTeamActionEvent((C2G_JoinTeamActionEvent) event);
			break;
		case TeamEventDefines.C2G_KickedOutTeamMemberActionEvent:
			// 4.踢出队员
			handle_KickedOutTeamMemberActionEvent((C2G_KickedOutTeamMemberActionEvent) event);
			break;
		case TeamEventDefines.C2G_LeaveTeamActionEvent:
			// 5.退出队伍
			handle_LeaveTeamActionEvent((C2G_LeaveTeamActionEvent) event);
			break;
		case TeamEventDefines.C2G_HandoverTeamLeaderActionEvent:
			// 6.转让队长
			handle_HandoverTeamLeaderActionEvent((C2G_HandoverTeamLeaderActionEvent) event);
			break;
		case TeamEventDefines.C2G_DisbandTeamActionEvent:
			// 7.解散队伍
			handle_DisbandTeamActionEvent((C2G_DisbandTeamActionEvent) event);
			break;
		case TeamEventDefines.C2G_PlayerTeamSettingActionEvent:
			// 8.是否接受队伍加入邀请设G2C_Broadcast_TeamLeaderKickedOutActionEvent置
			handle_PlayerTeamSettingActionEvent((C2G_PlayerTeamSettingActionEvent) event);
			break;
		case TeamEventDefines.C2G_PlayerTeam_CreateEvent:
			// 9.创建队伍（只有在活动开启的时候启用）
			handle_PlayerTeam_CreateActionEvent((C2G_PlayerTeam_CreateEvent) event);
			break;
		case TeamEventDefines.C2G_PlayerTeam_JoinRequestEvent:
			// 10.申请加入队伍（只有在活动开启的时候启用）
			handle_PlayerTeam_JoinRequestActionEvent((C2G_PlayerTeam_JoinRequestEvent) event);
			break;
		case TeamEventDefines.C2G_PlayerTeam_InfomationEvent:
			// 11.申请队伍信息列表（只有在活动开启的时候启用）
			handle_PlayerTeam_InfomationActionEvent((C2G_PlayerTeam_InfomationEvent) event);
			break;
		case TeamEventDefines.C2G_JoinRequestReplyActionEvent:
			// 12.组队申请处理 （队长发送给服务器）
			handle_PassRequestJoinTeamActionEvent((C2G_JoinRequestReplyActionEvent) event);
			break;
		case TeamEventDefines.C2G_PlayerTeam_Modify:
			// 13.队伍修改请求
			handle_PlayerTeam_ModifyActionEvent((C2G_PlayerTeam_Modify) event);
			break;
		case TeamEventDefines.C2G_TeamMenber_Detail:
			// 14.队伍信息请求
			handle_TeamMenber_Detail((C2G_TeamMenber_Detail) event);
			break;
		default:
			break;
		}
	}

	private void handle_PlayerInfoActionEvent(C2G_PlayerInfoActionEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter handle_PlayerInfoActionEvent");
		}
		Player invitedPlayer = getPlayer(event.getPlayerId());
		if (invitedPlayer == player || invitedPlayer == null) {
			// 不能对自己操作
			if (logger.isDebugEnabled()) {
				logger.debug("邀请的玩家不存在," + event.getPlayerId());
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
			return;
		}
		G2C_PlayerInfoActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerInfoActionEvent);
		response.setInvitedPlayer(invitedPlayer);
		response.setInvitePlayer(getConcreteParent());
		GameRoot.sendMessage(event.getIdentity(), response);
		if (logger.isDebugEnabled()) {
			logger.debug("return success");
		}
	}

	private void handle_AssembleTeamActionEvent(C2G_AssembleTeamActionEvent event) {
		Player invitedPlayer = getPlayer(event.getInvitedPlayerId());// 被邀请者

		if (invitedPlayer == player) {
			// 自己不能查看自己
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
			return;
		}

		if (invitedPlayer == null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_NOT_ONLINE);
			return;
		}

		if (isExistCrtInvitePlayer(invitedPlayer)) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_INVITE_EXIST);
			return;
		}

		// 非队长不能邀请
		if (getTeam() != null) {
			if (!isTeamLeader()) {
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_NO_INVITE);
				return;
			}
		}

		// 不能邀请有队伍的人
		if (invitedPlayer.getPlayerTeamComponent().getTeam() != null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_EXIST);
			return;
		}

		if (addCrtInvitePlayerMember(invitedPlayer)) {
			// 通知被邀请者
			G2C_AssembleTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_AssembleTeamActionEvent);
			response.setInvitePlayer(player);
			GameRoot.sendMessage(invitedPlayer.getIdentity(), response);
			if (logger.isDebugEnabled()) {
				logger.debug("return success");
			}

			return;
		}
		// 邀请失败
		ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_INVITE_FAIL);
	}

	private void handle_PassJoinTeamActionEvent(C2G_JoinTeamActionEvent event) {
		Player invitePlayer = getPlayer(event.getInvitePlayerId());
		if (invitePlayer == player) {
			// 不能对自己做操作
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
			return;
		}
		if (invitePlayer == null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_NOT_ONLINE_JOIN);
			return;
		}

		// 拒绝加入队伍
		if (event.getReplyJoinTeamType() == ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_NO || event.getReplyJoinTeamType() == ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_GIVE_UP) {
			// 通知队长拒绝加入
			invitePlayer.getPlayerTeamComponent().getCrtInvitePlayerMembers().remove(player.getId());
			G2C_JoinTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_JoinTeamActionEvent);
			response.setPlayerActionType(event.getReplyJoinTeamType());
			GameRoot.sendMessage(invitePlayer.getIdentity(), response);
			// 通过加入队伍
		} else if (event.getReplyJoinTeamType() == ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_YES) {
			Player teamLeader = invitePlayer;
			if (!this.hasTeam()) {

				RuntimeResult runtimeResult = joinTeam(teamLeader, player, INVITIED_JOIN);
				if (runtimeResult.isOK()) {
					// 加入成功
					G2C_JoinTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_JoinTeamActionEvent);
					response.setPlayerActionType(ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_YES);
					GameRoot.sendMessage(event.getIdentity(), response);
					return;
				} else {
					ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), runtimeResult.getApplicationCode());
					return;
				}
			}
		}
		// 加入失败
		ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_JOIN_FAIL);
	}

	private void handle_KickedOutTeamMemberActionEvent(C2G_KickedOutTeamMemberActionEvent event) {
		// 被踢出的玩家
		Player kickedOutTeamMember = getPlayer(event.getKickdOutPlayerId());
		if (kickedOutTeamMember == player || kickedOutTeamMember == null) {
			// 不能对自己做操作
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
			return;
		}
		if (this.hasTeam()) {
			Player teamLeader = player;
			boolean kickedOut = teamLeaderKickedOut(teamLeader, kickedOutTeamMember);
			if (kickedOut) {
				// 通知被踢出玩家
				G2C_KickedOutTeamMemberActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_KickedOutTeamMemberActionEvent);
				GameRoot.sendMessage(kickedOutTeamMember.getIdentity(), response);
				return;
			}
		}
		// 踢出玩家失败
		ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_KICKEDOUT_FAIL);
	}

	private void handle_LeaveTeamActionEvent(C2G_LeaveTeamActionEvent event) {
		if (this.hasTeam()) {
			boolean quitTeam = this.teamQuit(player);
			if (quitTeam) {
				G2C_LeaveTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_LeaveTeamActionEvent);
				GameRoot.sendMessage(event.getIdentity(), response);
				return;
			}
		}
		// 离开队伍失败
		ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_LEAVE_FAIL);
	}

	private void handle_HandoverTeamLeaderActionEvent(C2G_HandoverTeamLeaderActionEvent event) {
		if (this.hasTeam()) {
			Player newTeamLeader = getPlayer(event.getNewTeamLeaderPlayerId());
			boolean leaderHandover = teamLeaderHandover(player, newTeamLeader);
			if (leaderHandover) {
				G2C_HandoverTeamLeaderActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_HandoverTeamLeaderActionEvent);
				GameRoot.sendMessage(event.getIdentity(), response);
				return;
			}
		}
		// 转移队长失败
		ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_HANDEROVER_FAIL);
	}

	private void handle_DisbandTeamActionEvent(C2G_DisbandTeamActionEvent event) {
		if (this.hasTeam()) {
			boolean disband = teamLeaderDisband(getConcreteParent());
			if (disband) {
				G2C_DisbandTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_DisbandTeamActionEvent);
				GameRoot.sendMessage(event.getIdentity(), response);
				return;
			}
		}
		// 解散队伍失败
		ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_DISBAND_FAIL);
	}

	private void handle_PlayerTeamSettingActionEvent(C2G_PlayerTeamSettingActionEvent event) {
		this.teamSetting.setAcceptedInvite(event.isAcceptedInvite());
		G2C_PlayerTeamSettingActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeamSettingActionEvent);
		GameRoot.sendMessage(event.getIdentity(), response);
	}

	/**
	 * 创建队伍
	 * 
	 * @param event
	 */
	private void handle_PlayerTeam_CreateActionEvent(C2G_PlayerTeam_CreateEvent event) {
		AbstractGameSceneRef ref = player.getCrtScene().getRef();
		if (ref.getType() == SceneRef.Activity) {
			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId(ref.getId());
			byte crtActivityState = sceneActivity.getCrtActivityState();
			if (crtActivityState == 2 && PlayerTeamManagerComponent.teamBossScene.contains(ref.getId())) {
				PlayerTeam team = player.getPlayerTeamComponent().getTeam();
				G2C_PlayerTeam_CreateEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeam_CreateEvent);
				if (team != null) {
					logger.info("player alrealy has a team can't creat team.  player:" + player + "     team:" + team);
					ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_ME_EXIST);
					response.setCreateSuccee((byte) 0);
					GameRoot.sendMessage(event.getIdentity(), response);
					return;
				}
				int levelChoice = event.getLevelChoice();
				if (!levelLimit.containsKey(levelChoice)) {
					logger.info("client has sent a worng levelChoice:" + levelChoice);
					response.setCreateSuccee((byte) 0);
					GameRoot.sendMessage(event.getIdentity(), response);
					return;
				}

				RuntimeResult runtimeResult = joinTeam(player, null, ME_JOIN);
				if (runtimeResult.isOK()) {
					PlayerTeam newTeam = player.getPlayerTeamComponent().getTeam();
					if (newTeam != null) {
						newTeam.setTeamLevelLimit(levelLimit.get(levelChoice));
						response.setCreateSuccee((byte) 1);
						response.setTeamId(newTeam.getId());
						GameRoot.sendMessage(event.getIdentity(), response);
					}
				} else {
					response.setCreateSuccee((byte) 0);
					GameRoot.sendMessage(event.getIdentity(), response);
				}
			} else {
				logger.info("activity doesn't not open");
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_NOT_ACTIVITY_TIME);
			}
		}
	}
	

	/**
	 * 请求加入队伍
	 * 
	 * @param event
	 */
	private void handle_PlayerTeam_JoinRequestActionEvent(C2G_PlayerTeam_JoinRequestEvent event) {
		AbstractGameSceneRef ref = player.getCrtScene().getRef();
		if (ref.getType() == SceneRef.Activity) {
			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId(ref.getId());
			byte crtActivityState = sceneActivity.getCrtActivityState();
			if (crtActivityState == 2 && PlayerTeamManagerComponent.teamBossScene.contains(ref.getId())) {
				String joinRequestTeamId = event.getJoinRequestTeamId();
				PlayerTeam team = playerTeamManagerComponent.getTeam(joinRequestTeamId);
				if (team == null) {
					logger.info("Do not has this Team.");
					return;
				}

				int teamLevelLimit = team.getTeamLevelLimit();
				if (player.getLevel() < teamLevelLimit) {
					logger.info("Not Level Enought~!");
					return;
				}

				Player teamLeader = team.getTeamLeader();

				if (teamLeader == player || teamLeader == null) {
					// 自己不能查看自己
					ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
					return;
				}

				if (player.getPlayerTeamComponent().getTeam() != null) {
					ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_ME_EXIST);
					return;
				}

				PlayerTeamComponent playerTeamComponent = teamLeader.getPlayerTeamComponent();
				if (playerTeamComponent.isExistCrtRequestJoinPlayer(player)) {
					ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_REQUEST_EXIST);
				} else {
					playerTeamComponent.addCrtRequestJoinPlayer(player);
				}
				G2C_PlayerTeam_JoinRequestEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeam_JoinRequestEvent);
				response.setRequestPlayer(player);
				GameRoot.sendMessage(teamLeader.getIdentity(), response);
				if (logger.isDebugEnabled()) {
					logger.debug("return success");
				}
//				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_REQUEST_FAIL);
			}
		}
	}

	/**
	 * 请求获取队伍信息
	 * 
	 * @param event
	 */
	private void handle_PlayerTeam_InfomationActionEvent(C2G_PlayerTeam_InfomationEvent event) {
		AbstractGameSceneRef ref = player.getCrtScene().getRef();
		if (ref.getType() == SceneRef.Activity) {
			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId(ref.getId());
			byte crtActivityState = sceneActivity.getCrtActivityState();
			if (crtActivityState == 2 && PlayerTeamManagerComponent.teamBossScene.contains(ref.getId())) {
				G2C_PlayerTeam_InfomationEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeam_InfomationEvent);
				List<PlayerTeam> crtSceneTeams = playerTeamManagerComponent.getCrtSceneTeams(player);
				response.setPlayerTeam(team);
				response.setTeams(crtSceneTeams);
				GameRoot.sendMessage(event.getIdentity(), response);
			}
		}
	}

	private void handle_PassRequestJoinTeamActionEvent(C2G_JoinRequestReplyActionEvent event) {
		Player requestPlayer = getPlayer(event.getRequestPlayerId());
		if (requestPlayer == player || requestPlayer == null) {
			// 不能对自己做操作
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
			return;
		}

		PlayerTeam requestPlayerTeam = requestPlayer.getPlayerTeamComponent().getTeam();
		if (requestPlayerTeam != null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_EXIST);
			return;
		}

		// 拒绝加入队伍
		if (event.getReplyJoinTeamType() == ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_NO || event.getReplyJoinTeamType() == ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_GIVE_UP) {
			// 通知队员该队拒绝其加入
			requestPlayer.getPlayerTeamComponent().getCrtRequestJoinPlayers().remove(player.getId());
			G2C_JoinRequestReplyActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_JoinRequestReplyActionEvent);
			response.setPlayerActionType(event.getReplyJoinTeamType());
			GameRoot.sendMessage(requestPlayer.getIdentity(), response);
			// 通过加入队伍
		} else if (event.getReplyJoinTeamType() == ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_YES) {
			RuntimeResult runtimeResult = joinTeam(player, requestPlayer, REQUEST_JOIN);
			if (runtimeResult.isOK()) {
				// 加入成功
				G2C_JoinRequestReplyActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_JoinRequestReplyActionEvent);
				response.setPlayerActionType(ReplyJoinTeamType.REPLY_JOIN_TEAM_TYPE_YES);
				GameRoot.sendMessage(requestPlayer.getIdentity(), response);
				return;
			}
		}
	}

	private void handle_PlayerTeam_ModifyActionEvent(C2G_PlayerTeam_Modify event) {
		if (team == null) {
			logger.info("player do not has team,can't modify. player:" + player);
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_NOT_TEAM_EXIT);
			return;
		}
		if (!team.isTeamLeader(player)) {
			logger.info("player isn't teamLeader,can't modify. player:" + player);
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_NOT_TEAMLEADER);
			return;
		}
		int levelChoice = event.getLevelChoice();
		G2C_PlayerTeam_Modify response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_PlayerTeam_Modify);
		if (!levelLimit.containsKey(levelChoice)) {
			logger.info("client has sent a worng levelChoice:" + levelChoice);
			response.setSucceed((byte) 0);
			GameRoot.sendMessage(event.getIdentity(), response);
			return;
		}

		team.setTeamLevelLimit(levelLimit.get(levelChoice));
		response.setSucceed((byte) 1);
		GameRoot.sendMessage(event.getIdentity(), response);
	}

	private void handle_TeamMenber_Detail(C2G_TeamMenber_Detail event) {
		if (team == null) {
			logger.debug("player do not has team,can't get Detail. player:" + player);
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_TEAM_NOT_TEAM_EXIT);
			return;
		}
		G2C_TeamMenber_Detail response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_TeamMenber_Detail);
		response.setPlayerTeam(team);
		GameRoot.sendMessage(event.getIdentity(), response);
	}

	// ==================================================================================================================

	public void clearTeam() {
		setTeam(null);
		clearInviteAndRequestList();
	}

	public boolean hasTeam() {
		return team != null;
	}

	public boolean isTeamLeader() {
		if (!hasTeam())
			return false;
		if (player.equals(team.getTeamLeader())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean setTeam(PlayerTeam team) {
		this.team = team;
		if (team != null) {
			// 发送组队gameEvent
			sendTeamGameEvent();
		}
		return true;
	}

	public PlayerTeam getTeam() {
		return team;
	}
	
	private void sendTeamGameEvent() {
		TeamGameEvent teamGameEvent_GE = new TeamGameEvent();
		GameEvent<TeamGameEvent> ge = (GameEvent<TeamGameEvent>) GameEvent.getInstance(TeamGameEvent_ID, teamGameEvent_GE);
		sendGameEvent(ge, getConcreteParent().getId());
	}

	public List<String> getCrtInvitePlayerMembers() {
		return crtInvitePlayerMembers;
	}

	public boolean isExistCrtInvitePlayer(Player player) {
		for (String invitePlayerId : crtInvitePlayerMembers) {
			if (invitePlayerId.equals(player.getId()))
				return true;
		}
		return false;
	}

	public boolean addCrtInvitePlayerMember(Player player) {

		if (crtInvitePlayerMembers.size() < INVITE_PLAYER_MEMBERS_LENGTH) {
			crtInvitePlayerMembers.add(player.getId());
			return true;
		}

		return false;
	}

	public List<String> getCrtRequestJoinPlayers() {
		return crtRequestJoinPlayers;
	}

	public boolean isExistCrtRequestJoinPlayer(Player player) {
		for (String requestPlayerId : crtRequestJoinPlayers) {
			if (requestPlayerId.equals(player.getId()))
				return true;
		}
		return false;
	}

	public boolean addCrtRequestJoinPlayer(Player player) {

		if (crtRequestJoinPlayers.size() < INVITE_PLAYER_MEMBERS_LENGTH) {
			crtRequestJoinPlayers.add(player.getId());
			return true;
		}

		return false;
	}

	// ==================================================================================================================
	private static synchronized RuntimeResult joinTeam(Player teamLeader, Player teamMember, int type) {

		if (type == ME_JOIN) {
			PlayerTeam team = teamLeader.getPlayerTeamComponent().getTeam();
			if (team == null) {
				team = new PlayerTeam();
				team = playerTeamManagerComponent.createTeam(team, teamLeader);
				return RuntimeResult.OK();
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TEAM_EXIST);
		}
		// 判断成员有没组队
		PlayerTeam memberTeam = teamMember.getPlayerTeamComponent().getTeam();
		if (memberTeam != null) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TEAM_ME_EXIST);
		}

		// 对方有队伍 但不为队长也不能组队
		PlayerTeam inviteTeam = teamLeader.getPlayerTeamComponent().getTeam();
		if (inviteTeam != null) {
			if (!teamLeader.getPlayerTeamComponent().isTeamLeader()) {
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TEAM_EXIST);
			}
		}

		boolean ret = false;
		if (type == INVITIED_JOIN) {
			ret = teamLeader.getPlayerTeamComponent().getCrtInvitePlayerMembers().remove(teamMember.getId());
			teamMember.getPlayerTeamComponent().getCrtInvitePlayerMembers().remove(teamLeader.getId());
		} else if (type == REQUEST_JOIN) {
			ret = teamLeader.getPlayerTeamComponent().getCrtRequestJoinPlayers().remove(teamMember.getId());
			teamMember.getPlayerTeamComponent().getCrtRequestJoinPlayers().remove(teamLeader.getId());
		}
		if (ret) {
			PlayerTeam team = teamLeader.getPlayerTeamComponent().getTeam();
			if (team == null) {
				team = new PlayerTeam();
				team = playerTeamManagerComponent.createTeam(team, teamLeader);
			}
			RuntimeResult runtimeResult = team.joinTeam(teamMember);
			if (runtimeResult.isOK()) {
				Collection<Player> notifyPlayerList = new ArrayList<>();
				for (String playerId : team.getMembers()) {
					Player player = getPlayer(playerId);
					if (player != null)
						notifyPlayerList.add(player);
				}

				if (notifyPlayerList.size() > 0)
					BroadcastTeamFacade.broadcastJoinTeam(team, notifyPlayerList);

			}
			return runtimeResult;
		}
		return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TEAM_JOIN_FAIL);
	}

	private boolean teamQuit(Player player) {
		PlayerTeam team = player.getPlayerTeamComponent().getTeam();
		if (team == null) {
			logger.info("teamMember do not has team,can't quit team. teamMember:" + player);
			return false;
		}

		if (player.getPlayerTeamComponent().isTeamLeader()) {
			boolean quitTeam = team.teamLeaderQuitTeam(player);
			return quitTeam;
		} else {
			boolean quitTeam = team.teamMemberQuitTeam(player);
			return quitTeam;
		}

	}

	private boolean teamLeaderHandover(Player oldTeamLeader, Player newTeamLeader) {

		PlayerTeam team = oldTeamLeader.getPlayerTeamComponent().getTeam();
		if (team == null) {
			logger.info("oldTeamLeader do not has team,can't handover. oldTeamLeader:" + oldTeamLeader);
			return false;
		}

		return team.handoverTeamLeader(oldTeamLeader, newTeamLeader);
	}

	private boolean teamLeaderDisband(Player teamLeader) {

		PlayerTeam team = teamLeader.getPlayerTeamComponent().getTeam();
		if (team == null) {
			logger.info("teamLeader do not has team,can't disband. teamLeader:" + teamLeader);
			return false;
		}

		boolean disbandTeam = team.disbandTeam(teamLeader);
		return disbandTeam;

	}

	private boolean teamLeaderKickedOut(Player teamLeader, Player kickedOutTeamLeader) {
		PlayerTeam team = teamLeader.getPlayerTeamComponent().getTeam();
		if (team == null) {
			logger.info("teamLeader do not has team,can't kicked out. teamLeader:" + teamLeader);
			return false;
		}

		if (teamLeader.getPlayerTeamComponent().isTeamLeader()) {
			boolean kickedOut = team.kickedOutTeam(kickedOutTeamLeader);
			if (kickedOut) {
				BroadcastTeamFacade.broadcastKickedOutTeam(team, kickedOutTeamLeader);
			}
			return kickedOut;
		}

		return false;
	}

	private static Player getPlayer(String playerId) {
		Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
		return player;
	}
}
