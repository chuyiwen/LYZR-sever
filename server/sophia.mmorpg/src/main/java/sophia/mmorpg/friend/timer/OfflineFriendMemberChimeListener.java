package sophia.mmorpg.friend.timer;

import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.friend.FriendSystemManager;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class OfflineFriendMemberChimeListener implements SFTimeChimeListener{
	private static final Logger logger = Logger.getLogger(OfflineFriendMemberChimeListener.class);
	
	private static final long KickOutOfflinePlayerMgrIntervalTime = 1 * 3600 * 1000L;
	
	@Override
	public void handleServiceShutdown() {
		
	}

	@Override
	public void handleTimeChimeCancel() {
		
	}

	@Override
	public void handleTimeChime() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
	
		ConcurrentMap<String, Player> playerMap = playerManager.getPlayerMap();
		
		long now = System.currentTimeMillis();
		for (Player player : playerMap.values()) {
			if (player == null || player.isOnline()) {
				continue;
			}
			
			String playerId = player.getId();
			long lastLogoutTime = MGPropertyAccesser.getLastLogoutTime(player.getProperty());
			if (now - lastLogoutTime > KickOutOfflinePlayerMgrIntervalTime) {
				if (logger.isDebugEnabled()) {
					logger.debug("KickOutOfflinePlayerMgr player=" + player);
				}
				
				FriendSystemManager.removePlayerChatFriendMgr(playerId);
			}
		}
	}

}
