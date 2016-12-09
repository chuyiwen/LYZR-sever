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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.gameEvent.LeaveTeam_GE;
import sophia.mmorpg.player.gameEvent.SinglePlayerTeam_GE;
import sophia.mmorpg.player.team.actionEvent.broadcast.BroadcastTeamFacade;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;

public final class PlayerTeam {
	private static final Logger logger = Logger.getLogger(PlayerTeam.class);

	public static final int Default_Max_Member_Number = 3;

	private String teamLeader;

	private int teamLevelLimit = 0; // 队伍等级限制，只有在活动里面创建的队伍才有相应的队伍等级限制

	private final CopyOnWriteArrayList<String> members = new CopyOnWriteArrayList<>();
	/** 加入是否需要答复.默认false，直接加入 */
	private volatile boolean needJoinReply = false;

	public PlayerTeam() {

	}

	public String getId() {
		return teamLeader;
	}

	public Player getTeamLeader() {
		Player player =  MMORPGContext.getPlayerComponent().getPlayerManager().getPlayer(teamLeader);
		return player;
	}

	public void setTeamLeader(Player teamLeader) {
		this.teamLeader = teamLeader.getId();
	}
	
	public boolean isTeamLeader(Player player) {
		Preconditions.checkNotNull(player);
		return StringUtils.equals(player.getId(), teamLeader);
	}

	public CopyOnWriteArrayList<String> getMembers() {
		return members;
	}
	
	private boolean isExistMember(String playerId){
		for(String membersId:members){
			if(membersId.equals(playerId))
				return true;
		}
		
		return false;
	}

	public synchronized RuntimeResult removeMember(Player member) {
		RuntimeResult ret = RuntimeResult.RuntimeApplicationError(1);
		if (members != null && member != null) {
			if (members.remove(member.getId())) {
				ret = RuntimeResult.OK();
			}
		}
		return ret;
	}

	/**
	 * 增加成员
	 * 
	 * @param member
	 * @return
	 */
	public synchronized RuntimeResult addMember(Player member) {
		if (members.size() >= Default_Max_Member_Number) {
			// 队伍已达上限，不能增加成员
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TEAM_LIMIT);
		}

		members.add(member.getId());
		return RuntimeResult.OK();
	}

	/**
	 * 退出队伍
	 * 
	 * @param team
	 * @param teamMember
	 * @return
	 */
	public synchronized boolean teamMemberQuitTeam(Player teamMember) {
		Preconditions.checkNotNull(teamMember);
		if (isTeamLeader(teamMember)) {
			return false;
		}
		
		boolean removeMember = removeMember(teamMember).isOK();
		if (removeMember) {
			teamMember.getPlayerTeamComponent().clearTeam();
			sendLeaveTeamGE(teamMember);

			Collection<Player> notifyPlayerList = new ArrayList<>();
			notifyPlayerList.add(teamMember);
			BroadcastTeamFacade.broadcastMemberLeaveTeam(this, notifyPlayerList);
			
			sendSinglePlayerGE();
			
			if (logger.isDebugEnabled()) {
				logger.debug(teamMember + "退出队伍" + this);
			}

		}
			
		
		return removeMember;
	}

	/**
	 * 退出队伍
	 * 
	 * @param team
	 * @param teamLeader
	 * @return
	 */
	public synchronized boolean teamLeaderQuitTeam(Player teamLeader) {
		Preconditions.checkNotNull(teamLeader);
		
		if (!isTeamLeader(teamLeader)) {
			return false;
		}
		
		// 只有一个人的队伍
		if (getMembers().size() <= 1) {
			disbandTeam(teamLeader);
			return true;
		}
		
		members.remove(teamLeader.getId());// 先移除队长
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player newTeamLeader = playerManager.getPlayer(getMembers().get(0));
		if (newTeamLeader != null) {
			teamLeader.getPlayerTeamComponent().clearTeam();
			sendLeaveTeamGE(teamLeader);
			setTeamLeader(newTeamLeader);
			if (logger.isDebugEnabled()) {
				logger.debug(teamLeader + "退出队伍" + this + "转让队长" + newTeamLeader);
			}

			PlayerTeam team = newTeamLeader.getPlayerTeamComponent().getTeam();
			BroadcastTeamFacade.broadcastTeamLeaderQuit(team, teamLeader, team.getTeamLeader());
			sendSinglePlayerGE();
			return true;
		}
		
		if (logger.isInfoEnabled()) {
			logger.info(teamLeader + "退出队伍" + "转让队长" + getMembers().get(0) + "不存在");
		}

		return false;
	}
	
	/**
	 * 转让队长
	 * 
	 * @param playerLeader
	 * @param playerMember
	 * @return
	 */
	public synchronized boolean handoverTeamLeader(Player playerLeader, Player playerMember) {
		Preconditions.checkNotNull(playerLeader);
		Preconditions.checkNotNull(playerMember);
		
		if (isTeamLeader(playerLeader)) {	
			if(isExistMember(playerMember.getId())){
				setTeamLeader(playerMember);
				if (logger.isDebugEnabled()) {
					logger.debug(playerLeader + "转让队长" + playerMember);
				}
				return true;
			}

		}
		return false;

	}
	
	public synchronized boolean disbandTeam(Player teamLeader) {
		if (!isTeamLeader(teamLeader)) {
			return false;
		}
		
		teamLeader.getPlayerTeamComponent().clearTeam();
		sendLeaveTeamGE(teamLeader);
		
		CopyOnWriteArrayList<String> members = getMembers();
		for (String playerId : members) {
			PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
			Player player = playerManager.getPlayer(playerId);
			if(player!=null) {
				player.getPlayerTeamComponent().clearTeam();
				sendLeaveTeamGE(player);
			}
		}
		
		MMORPGContext.playerTeamManagerComponent().disbandTeam(this);
		
		BroadcastTeamFacade.broadcastTeamLeaderDisband(this, teamLeader);
		
		return true;
	}

	
	/**
	 * 获取队伍人数
	 * @return
	 */
	public int getTeamMemberNum() {
		return members.size();
	}
	
	public int getTeamAverageLevel() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		int teamMemberNum = getTeamMemberNum();
		if(teamMemberNum==0)
			return 0;
		
		int allLevel = 0;
		for (String playerId : members) {
			allLevel += playerManager.getPlayer(playerId).getLevel();
		}
		return allLevel / teamMemberNum;
	}

	/**
	 * 加入队伍
	 * 
	 * @param team
	 * @param playerMember
	 * @return
	 */
	public RuntimeResult joinTeam(Player playerMember) {
		Preconditions.checkNotNull(playerMember);
		
		RuntimeResult runtimeResult = RuntimeResult.OK();
		
		PlayerTeam ownerTeam = playerMember.getPlayerTeamComponent().getTeam();
		if (ownerTeam == null) {
			runtimeResult = addMember(playerMember);
			if (runtimeResult.isOK()) {
				playerMember.getPlayerTeamComponent().setTeam(this);
			}
		}else{
			runtimeResult = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_TEAM_ME_EXIST); 
		}
		
		return runtimeResult;
	}
	
	/**
	 * 踢出队伍
	 * 
	 * @param team
	 * @param playerMember
	 * @return
	 */
	public boolean kickedOutTeam(Player playerMember) {
		Preconditions.checkNotNull(playerMember);
		boolean removeMember = removeMember(playerMember).isOK();
		if (removeMember) {
			playerMember.getPlayerTeamComponent().clearTeam();
			sendLeaveTeamGE(playerMember);
			sendSinglePlayerGE();
		}
		
		return removeMember;
	}

	public boolean isNeedJoinReply() {
		return needJoinReply;
	}

	public void setNeedJoinReply(boolean needJoinReply) {
		this.needJoinReply = needJoinReply;
	}

	/**
	 * 获取当前场景的其它队员
	 * 
	 * @param self
	 * @return
	 */
	public List<Player> getCrtSceneOtherMember(Player self) {
		List<Player> allTeam = new ArrayList<>();
		CopyOnWriteArrayList<String> members = getMembers();
		for (String playerId : members) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
			if(player==null){
				continue;
			}
				
			if (StringUtils.equals(playerId, self.getId()) || !StringUtils.equals(player.getCrtScene().getId(), self.getCrtScene().getId())) {
				continue;
			}
			allTeam.add(player);
		}

		if(self!=null && getTeamLeader()!=null){
			if (!StringUtils.equals(teamLeader, self.getId()) && StringUtils.equals(getTeamLeader().getCrtScene().getId(), self.getCrtScene().getId())) {
				allTeam.add(getTeamLeader());
			}
		}
		return allTeam;
	}
	
	/**
	 * 如果没有队员的话，解散队伍
	 */
	public void disbandTeamIfNoMember() {
		if (members.size() == 1) {
			disbandTeam(getTeamLeader());
		}
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (super.getClass() != obj.getClass())
			return false;
		PlayerTeam other = (PlayerTeam) obj;
		if (this.getId() != null) {
			if (other.getId() == null) {
				return false;
			} else if ((this.getId().equals(other.getId()))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "队伍@" + getId() + ",队长：" + this.getTeamLeader() + ",队员数量：" + members.size();
	}

	public int getTeamLevelLimit() {
		return teamLevelLimit;
	}

	public void setTeamLevelLimit(int teamLevelLimit) {
		this.teamLevelLimit = teamLevelLimit;
	}

	public void sendLeaveTeamGE(Player player) {
		LeaveTeam_GE leaveTeam_GE = new LeaveTeam_GE(player.getSceneRefId());
		GameEvent<LeaveTeam_GE> leaveTeamGE = GameEvent.getInstance(LeaveTeam_GE.class.getSimpleName(), leaveTeam_GE);
		player.handleGameEvent(leaveTeamGE);
		GameEvent.pool(leaveTeamGE);
	}
	
	public void sendSinglePlayerGE() {
		if (members.size() != 1) {
			return;
		}
		for (String playerId : members) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayer(playerId);
			if (!player.isOnline()) {
				return;
			}
			SinglePlayerTeam_GE singlePlayerTeam_GE = new SinglePlayerTeam_GE(player.getSceneRefId());
			GameEvent<SinglePlayerTeam_GE> singlePlayerTeamGE = GameEvent.getInstance(SinglePlayerTeam_GE.class.getSimpleName(), singlePlayerTeam_GE);
			player.handleGameEvent(singlePlayerTeamGE);
			GameEvent.pool(singlePlayerTeamGE);
		}
	}
}
