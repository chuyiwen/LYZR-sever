package sophia.mmorpg.player.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.component.AbstractComponent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Preconditions;

public class PlayerTeamManagerComponent extends AbstractComponent {

	protected static final Logger logger = Logger.getLogger(PlayerTeamManagerComponent.class);
	
	public static List<String> teamBossScene = new ArrayList<>();

	private ConcurrentHashMap<String, PlayerTeam> teams = new ConcurrentHashMap<String, PlayerTeam>();

	private ConcurrentHashMap<String, PlayerTeam> playerTeamCache = new ConcurrentHashMap<>();

	private static final long removePlayerTeamCache = 5 * 60 * 1000L;

	private SFTimer playerTeamCacheTimer;

	@Override
	public void ready() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		playerTeamCacheTimer = timerCreater.minuteCalendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				kickTimeoutPlayerInTeam();
			}

			@Override
			public void handleServiceShutdown() {
			}
		});

		if (logger.isDebugEnabled()) {
			logger.debug(" PlayerTeamCacheTick was running.");
		}
		super.ready();
	}

	@Override
	public void suspend() {
		if (playerTeamCacheTimer != null) {
			playerTeamCacheTimer.cancel();
		}

		if (logger.isDebugEnabled()) {
			logger.debug(" PlayerTeamCacheTick was terminated.");
		}
		super.destroy();
	}

	/**
	 * 创建队伍
	 * 
	 * @param teamLeader
	 * @param teamMember
	 * @return
	 */
	public PlayerTeam createTeam(PlayerTeam team, Player teamLeader) {
		Preconditions.checkNotNull(team);
		Preconditions.checkNotNull(teamLeader);

		team.setTeamLeader(teamLeader);
		team.addMember(teamLeader);
		teams.put(team.getId(), team);
		teamLeader.getPlayerTeamComponent().setTeam(team);

		if (logger.isDebugEnabled()) {
			logger.debug(teamLeader + "创建队伍" + team);
		}

		return team;
	}

	/**
	 * 解散队伍
	 * 
	 * @param teamLeader
	 * @return
	 */
	public void disbandTeam(PlayerTeam team) {
		Preconditions.checkNotNull(team);
		teams.remove(team.getId());

	}
	
	/**
	 * 是否为同一队
	 * 
	 * @param playerA
	 * @param playerB
	 * @return
	 */
	public boolean isSameTeam(Player playerA, Player playerB) {
		Preconditions.checkNotNull(playerA);
		Preconditions.checkNotNull(playerB);
		PlayerTeam ateam = playerA.getPlayerTeamComponent().getTeam();
		PlayerTeam bteam = playerB.getPlayerTeamComponent().getTeam();
		if (ateam != null || bteam != null) {
			if (ateam == bteam)
				return true;
		}
		return false;
	}

	public ConcurrentHashMap<String, PlayerTeam> getTeams() {
		return teams;
	}

	public PlayerTeam getTeam(String teamId) {
		for (String strTeamId: teams.keySet()) {
			PlayerTeam team = teams.get(strTeamId);
			if(team!=null){
				if (StringUtils.equals(team.getId(), teamId)) {
					return team;
				}
			}
		}
		return null;
	}

	public List<PlayerTeam> getCrtSceneTeams(Player player) {
		List<PlayerTeam> newteams = new ArrayList<>();
		String sceneRefId = player.getCrtScene().getRef().getId();
		for (Entry<String, PlayerTeam> entry : teams.entrySet()) {
			GameScene crtScene = entry.getValue().getTeamLeader().getCrtScene();
			if (crtScene != null) {
				String teamLeaderSceneRefId = crtScene.getRef().getId();
				if (StringUtils.equals(sceneRefId, teamLeaderSceneRefId)) {
					newteams.add(entry.getValue());
				}
			}
		}
		return newteams;
	}

	public boolean hasGameInstanceCache(Player player) {
		return playerTeamCache.containsKey(player.getId());
	}
	
	public void addPlayerTeamCache(Player player, PlayerTeam playerTeam) {
		playerTeamCache.put(player.getId(), playerTeam);
	}

	public PlayerTeam removePlayerTeamCache(Player player) {
		return playerTeamCache.remove(player.getId());
	}

	private void kickTimeoutPlayerInTeam() {
		for (Entry<String, PlayerTeam> entry : playerTeamCache.entrySet()) {
			PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
			Player player = playerManager.getPlayer(entry.getKey());
			long now = System.currentTimeMillis();
			if (now - MGPropertyAccesser.getLastLogoutTime(player.getProperty()) >= removePlayerTeamCache) {
				PlayerTeam playerTeamCache = removePlayerTeamCache(player);
				if (player.getPlayerTeamComponent().isTeamLeader()) {
					playerTeamCache.teamLeaderQuitTeam(player);
				} else {
					playerTeamCache.teamMemberQuitTeam(player);
				}
			}
		}
	}
}
