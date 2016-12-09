package sophia.mmorpg.player.team.actionEvent;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;
import sophia.mmorpg.player.team.actionEvent.activity.C2G_PlayerTeamBoss_RequestTime;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_End;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_PreStart;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_RequestTime;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_Show;
import sophia.mmorpg.player.team.actionEvent.activity.G2C_PlayerTeamBoss_Start;
import sophia.mmorpg.player.team.actionEvent.assemble.C2G_AssembleTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.assemble.G2C_AssembleTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.broadcast.G2C_Broadcast_DisbandTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.broadcast.G2C_Broadcast_HandoverTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.broadcast.G2C_Broadcast_TeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.broadcast.G2C_Broadcast_TeamLeaderKickedOutActionEvent;
import sophia.mmorpg.player.team.actionEvent.broadcast.G2C_Broadcast_TeamLeaderQuitTeamActionEvent;
import sophia.mmorpg.player.team.actionEvent.broadcast.G2C_Broadcast_TeamMemberDeadActionEvent;
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

public class TeamEventDefines {
	// 玩家信息请求
	public static final short C2G_PlayerInfoActionEvent = MMORPGEventDefines.Team_Message_Begin + 1;
	// 玩家信息返还
	public static final short G2C_PlayerInfoActionEvent = MMORPGEventDefines.Team_Message_Begin + 51;

	// 组队邀请请求
	public static final short C2G_AssembleTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 2;
	// 组队邀请返回
	public static final short G2C_AssembleTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 52;

	// 组队邀请通知
	public static final short C2G_JoinTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 3;
	// 组队邀请返还
	public static final short G2C_JoinTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 53;

	// 离开队伍请求
	public static final short C2G_LeaveTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 4;
	// 离开队伍返还
	public static final short G2C_LeaveTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 54;

	// 踢出队伍成员请求
	public static final short C2G_KickedOutTeamMemberActionEvent = MMORPGEventDefines.Team_Message_Begin + 5;
	// 踢出队伍成员返还
	public static final short G2C_KickedOutTeamMemberActionEvent = MMORPGEventDefines.Team_Message_Begin + 55;

	// 队长转让请求
	public static final short C2G_HandoverTeamLeaderActionEvent = MMORPGEventDefines.Team_Message_Begin + 6;
	// 队长转让返还
	public static final short G2C_HandoverTeamLeaderActionEvent = MMORPGEventDefines.Team_Message_Begin + 56;

	// 解散队伍请求
	public static final short C2G_DisbandTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 7;
	// 解散队伍返还
	public static final short G2C_DisbandTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 57;

	// 设置是否接受队伍加入邀请请求
	public static final short C2G_PlayerTeamSettingActionEvent = MMORPGEventDefines.Team_Message_Begin + 8;
	// 设置是否接受队伍加入邀请返还
	public static final short G2C_PlayerTeamSettingActionEvent = MMORPGEventDefines.Team_Message_Begin + 58;

	// 队伍创建请求
	public static final short C2G_PlayerTeam_CreateEvent = MMORPGEventDefines.Team_Message_Begin + 9;
	// 队伍创建返回
	public static final short G2C_PlayerTeam_CreateEvent = MMORPGEventDefines.Team_Message_Begin + 59;

	// 申请加入队伍请求
	public static final short C2G_PlayerTeam_JoinRequestEvent = MMORPGEventDefines.Team_Message_Begin + 10;
	// 申请加入队伍返回
	public static final short G2C_PlayerTeam_JoinRequestEvent = MMORPGEventDefines.Team_Message_Begin + 60;

	// 队伍列表及信息请求
	public static final short C2G_PlayerTeam_InfomationEvent = MMORPGEventDefines.Team_Message_Begin + 11;
	// 队伍列表及信息返回
	public static final short G2C_PlayerTeam_InfomationEvent = MMORPGEventDefines.Team_Message_Begin + 61;

	// 队伍列表及信息返回
	public static final short C2G_JoinRequestReplyActionEvent = MMORPGEventDefines.Team_Message_Begin + 12;
	// 队伍列表及信息返回
	public static final short G2C_JoinRequestReplyActionEvent = MMORPGEventDefines.Team_Message_Begin + 62;

	// 队伍列表及信息返回
	public static final short C2G_PlayerTeam_Modify = MMORPGEventDefines.Team_Message_Begin + 13;
	// 队伍列表及信息返回
	public static final short G2C_PlayerTeam_Modify = MMORPGEventDefines.Team_Message_Begin + 63;

	// 请求队员的状态信息
	public static final short C2G_TeamMenber_Detail = MMORPGEventDefines.Team_Message_Begin + 14;
	// 队伍状态信息返回
	public static final short G2C_TeamMenber_Detail = MMORPGEventDefines.Team_Message_Begin + 64;

	// ======================================================================================================================
	// 队员进入/队员离开(广播)
	public static final short G2C_Broadcast_TeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 81;
	// 队长转让(广播)
	public static final short G2C_Broadcast_HandoverTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 82;
	// 队长离开队伍(广播)
	public static final short G2C_Broadcast_TeamLeaderQuitTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 83;
	// 队长解散队伍(广播)
	public static final short G2C_Broadcast_DisbandTeamActionEvent = MMORPGEventDefines.Team_Message_Begin + 84;
	// 队长踢出队员(广播)
	public static final short G2C_Broadcast_TeamLeaderKickedOutActionEvent = MMORPGEventDefines.Team_Message_Begin + 85;
	// 队员死亡(广播)
	public static final short G2C_Broadcast_TeamMemberDeadActionEvent = MMORPGEventDefines.Team_Message_Begin + 86;

	// ==========================================================================================================
	// 队员死亡(广播)
	public static final short G2C_PlayerTeamBoss_PreStart = MMORPGEventDefines.Team_Message_Begin + 65;

	public static final short G2C_PlayerTeamBoss_End = MMORPGEventDefines.Team_Message_Begin + 15;

	public static final short C2G_PlayerTeamBoss_RequestTime = MMORPGEventDefines.Team_Message_Begin + 66;

	public static final short G2C_PlayerTeamBoss_RequestTime = MMORPGEventDefines.Team_Message_Begin + 67;

	public static final short G2C_PlayerTeamBoss_Start = MMORPGEventDefines.Team_Message_Begin + 68;
	
	public static final short G2C_PlayerTeamBoss_Show = MMORPGEventDefines.Team_Message_Begin + 69;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_PlayerInfoActionEvent, C2G_PlayerInfoActionEvent.class);
		MessageFactory.addMessage(C2G_AssembleTeamActionEvent, C2G_AssembleTeamActionEvent.class);
		MessageFactory.addMessage(C2G_JoinTeamActionEvent, C2G_JoinTeamActionEvent.class);
		MessageFactory.addMessage(C2G_KickedOutTeamMemberActionEvent, C2G_KickedOutTeamMemberActionEvent.class);
		MessageFactory.addMessage(C2G_LeaveTeamActionEvent, C2G_LeaveTeamActionEvent.class);
		MessageFactory.addMessage(C2G_HandoverTeamLeaderActionEvent, C2G_HandoverTeamLeaderActionEvent.class);
		MessageFactory.addMessage(C2G_DisbandTeamActionEvent, C2G_DisbandTeamActionEvent.class);
		MessageFactory.addMessage(C2G_PlayerTeamSettingActionEvent, C2G_PlayerTeamSettingActionEvent.class);

		MessageFactory.addMessage(G2C_PlayerInfoActionEvent, G2C_PlayerInfoActionEvent.class);
		MessageFactory.addMessage(G2C_AssembleTeamActionEvent, G2C_AssembleTeamActionEvent.class);
		MessageFactory.addMessage(G2C_JoinTeamActionEvent, G2C_JoinTeamActionEvent.class);
		MessageFactory.addMessage(G2C_KickedOutTeamMemberActionEvent, G2C_KickedOutTeamMemberActionEvent.class);
		MessageFactory.addMessage(G2C_LeaveTeamActionEvent, G2C_LeaveTeamActionEvent.class);
		MessageFactory.addMessage(G2C_HandoverTeamLeaderActionEvent, G2C_HandoverTeamLeaderActionEvent.class);
		MessageFactory.addMessage(G2C_DisbandTeamActionEvent, G2C_DisbandTeamActionEvent.class);
		MessageFactory.addMessage(G2C_PlayerTeamSettingActionEvent, G2C_PlayerTeamSettingActionEvent.class);

		MessageFactory.addMessage(C2G_PlayerTeam_CreateEvent, C2G_PlayerTeam_CreateEvent.class);
		MessageFactory.addMessage(G2C_PlayerTeam_CreateEvent, G2C_PlayerTeam_CreateEvent.class);
		MessageFactory.addMessage(C2G_PlayerTeam_JoinRequestEvent, C2G_PlayerTeam_JoinRequestEvent.class);
		MessageFactory.addMessage(G2C_PlayerTeam_JoinRequestEvent, G2C_PlayerTeam_JoinRequestEvent.class);
		MessageFactory.addMessage(C2G_PlayerTeam_InfomationEvent, C2G_PlayerTeam_InfomationEvent.class);
		MessageFactory.addMessage(G2C_PlayerTeam_InfomationEvent, G2C_PlayerTeam_InfomationEvent.class);

		MessageFactory.addMessage(C2G_JoinRequestReplyActionEvent, C2G_JoinRequestReplyActionEvent.class);
		MessageFactory.addMessage(G2C_JoinRequestReplyActionEvent, G2C_JoinRequestReplyActionEvent.class);
		MessageFactory.addMessage(C2G_PlayerTeam_Modify, C2G_PlayerTeam_Modify.class);
		MessageFactory.addMessage(G2C_PlayerTeam_Modify, G2C_PlayerTeam_Modify.class);

		MessageFactory.addMessage(C2G_TeamMenber_Detail, C2G_TeamMenber_Detail.class);
		MessageFactory.addMessage(G2C_TeamMenber_Detail, G2C_TeamMenber_Detail.class);

		// 广播======================================================================================================================
		MessageFactory.addMessage(G2C_Broadcast_TeamActionEvent, G2C_Broadcast_TeamActionEvent.class);
		MessageFactory.addMessage(G2C_Broadcast_HandoverTeamActionEvent, G2C_Broadcast_HandoverTeamActionEvent.class);
		MessageFactory.addMessage(G2C_Broadcast_TeamLeaderQuitTeamActionEvent, G2C_Broadcast_TeamLeaderQuitTeamActionEvent.class);
		MessageFactory.addMessage(G2C_Broadcast_DisbandTeamActionEvent, G2C_Broadcast_DisbandTeamActionEvent.class);
		MessageFactory.addMessage(G2C_Broadcast_TeamLeaderKickedOutActionEvent, G2C_Broadcast_TeamLeaderKickedOutActionEvent.class);
		MessageFactory.addMessage(G2C_Broadcast_TeamMemberDeadActionEvent, G2C_Broadcast_TeamMemberDeadActionEvent.class);

		// ======================================================================================================================
		MessageFactory.addMessage(G2C_PlayerTeamBoss_PreStart, G2C_PlayerTeamBoss_PreStart.class);
		MessageFactory.addMessage(G2C_PlayerTeamBoss_End, G2C_PlayerTeamBoss_End.class);
		MessageFactory.addMessage(C2G_PlayerTeamBoss_RequestTime, C2G_PlayerTeamBoss_RequestTime.class);
		MessageFactory.addMessage(G2C_PlayerTeamBoss_RequestTime, G2C_PlayerTeamBoss_RequestTime.class);
		MessageFactory.addMessage(G2C_PlayerTeamBoss_Start, G2C_PlayerTeamBoss_Start.class);
		MessageFactory.addMessage(G2C_PlayerTeamBoss_Show, G2C_PlayerTeamBoss_Show.class);
	}
}
