package sophia.mmorpg.player.team.actionEvent.broadcast;

import java.util.Collection;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.team.PlayerTeam;
import sophia.mmorpg.player.team.actionEvent.TeamEventDefines;

import com.google.common.base.Preconditions;

public class BroadcastTeamFacade {

	/**
	 * 
	 * 广播队长转让
	 * 
	 * @param team
	 * @param oldTeamLeader
	 * @param newTeamLeader
	 */
	public static void broadcastTeamLeaderHandover(PlayerTeam team, Player oldTeamLeader, Player newTeamLeader) {
		Preconditions.checkNotNull(team);
		Preconditions.checkNotNull(oldTeamLeader);
		Preconditions.checkNotNull(newTeamLeader);

		G2C_Broadcast_HandoverTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_Broadcast_HandoverTeamActionEvent);
		response.setNewTeamLeader(newTeamLeader);
		response.setOldTeamLeader(oldTeamLeader);

		// 通知队长
		//GameRoot.sendMessage(team.getTeamLeader().getIdentity(), response);

		for (String playerId : team.getMembers()) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			
			// 通知队员
			if(player!=null)
				GameRoot.sendMessage(player.getIdentity(), response);
		}
	}

	/**
	 * 广播队友增加
	 * 
	 * @param team
	 * @param teamMember
	 */
	public static void broadcastJoinTeam(PlayerTeam team, Collection<Player> teamMembers) {
		broadcastTeamMemberNumChange(team, teamMembers, BroadcastActionType.NOTIFY_ACTION_TYPE_JOIN);
	}

	/**
	 * 广播队友减少
	 * 
	 * @param team
	 * @param teamMember
	 */
	public static void broadcastMemberLeaveTeam(PlayerTeam team, Collection<Player> teamMembers) {
		broadcastTeamMemberNumChange(team, teamMembers, BroadcastActionType.NOTIFY_ACTION_TYPE_QUIT);
	}

	/**
	 * 广播队长离开队伍
	 * 
	 * @param team
	 * @param oldTeamLeader
	 * @param newTeamLeader
	 */
	public static void broadcastTeamLeaderQuit(PlayerTeam team, Player quitTeamLeader, Player newTeamLeader) {
		Preconditions.checkNotNull(team);
		Preconditions.checkNotNull(quitTeamLeader);
		Preconditions.checkNotNull(newTeamLeader);

		G2C_Broadcast_TeamLeaderQuitTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_Broadcast_TeamLeaderQuitTeamActionEvent);
		response.setNewTeamLeader(newTeamLeader);
		response.setQuitTeamLeader(quitTeamLeader);

		// 通知队长
		//GameRoot.sendMessage(team.getTeamLeader().getIdentity(), response);

		for (String playerId : team.getMembers()) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			
			// 通知队员
			if(player!=null)
				GameRoot.sendMessage(player.getIdentity(), response);
		}
	}

	/**
	 * 广播队长解散队伍
	 * 
	 * @param team
	 * @param teamLeader
	 */
	public static void broadcastTeamLeaderDisband(PlayerTeam team, Player teamLeader) {
		Preconditions.checkNotNull(team);
		Preconditions.checkNotNull(teamLeader);

		G2C_Broadcast_DisbandTeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_Broadcast_DisbandTeamActionEvent);

		for (String playerId : team.getMembers()) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			// 通知队员
			if (player != null) {
				GameRoot.sendMessage(player.getIdentity(), response);
			}
		}
	}

	/**
	 * 广播队长踢出队员
	 * 
	 * @param team
	 * @param kickedOutteamMember
	 */
	public static void broadcastKickedOutTeam(PlayerTeam team, Player kickedOutTeamLeader) {
		Preconditions.checkNotNull(team);
		Preconditions.checkNotNull(kickedOutTeamLeader);

		G2C_Broadcast_TeamLeaderKickedOutActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_Broadcast_TeamLeaderKickedOutActionEvent);
		response.setKickedOutTeamLeader(kickedOutTeamLeader);

		//GameRoot.sendMessage(team.getTeamLeader().getIdentity(), response);

		for (String playerId : team.getMembers()) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			
			// 通知队员
			if(player!=null)
				GameRoot.sendMessage(player.getIdentity(), response);
		}
	}

	/**
	 * 广播队友死亡
	 * 
	 * @param team
	 * @param deadTeamMember
	 */
	public static void broadcastDeadTeamMember(PlayerTeam team, Player deadTeamMember) {
		Preconditions.checkNotNull(team);
		Preconditions.checkNotNull(deadTeamMember);

		G2C_Broadcast_TeamMemberDeadActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_Broadcast_TeamMemberDeadActionEvent);
		response.setDeadTeamMember(deadTeamMember);

		for (String playerId : team.getMembers()) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			
			// 通知队员
			if(player!=null)
				GameRoot.sendMessage(player.getIdentity(), response);
		}
	}

	// =====================================================================================================================================
	private static void broadcastTeamMemberNumChange(PlayerTeam team, Collection<Player> teamMembers, byte actionType) {
		Preconditions.checkNotNull(team);
		Preconditions.checkNotNull(teamMembers);

		G2C_Broadcast_TeamActionEvent response = MessageFactory.getConcreteMessage(TeamEventDefines.G2C_Broadcast_TeamActionEvent);
		response.setActionType(actionType);
		response.setTeamMembers(teamMembers);
		response.setTeamLeader(team.getTeamLeader());
		// 通知队长
		//GameRoot.sendMessage(team.getTeamLeader().getIdentity(), response);
		for (String playerId : team.getMembers()) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			
			// 通知队员
			if(player!=null)
				GameRoot.sendMessage(player.getIdentity(), response);

		}
	}
}
