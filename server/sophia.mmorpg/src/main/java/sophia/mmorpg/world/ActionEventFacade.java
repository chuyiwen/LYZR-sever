package sophia.mmorpg.world;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.GameRoot;
import sophia.game.utils.eventBuf.EventBufMgr;
import sophia.game.utils.eventBuf.Executable;
import sophia.game.utils.eventBuf.IDoer;
import sophia.game.utils.eventBuf.Pair;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;

public final class ActionEventFacade {
	
	/**
	 * 发给全服
	 * 
	 * @param actionEvent
	 */
	public final static void sendMessageToWorld(final ActionEventBase actionEvent) {
		PlayerManager playerManager   = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> characters = playerManager.getPlayerList();
		sendMessageToPart(actionEvent, characters);
	}
	/**
	 * 发给指定局部集合玩家
	 * 
	 * @param actionEvent
	 */
	public final static void sendMessageToPart(final ActionEventBase actionEvent,final Collection<Player> characters) {
		
		Pair<Integer, TimeUnit> pair  = EventBufMgr.getInstance().getOptimizationTotalUseTime(characters.size(), EventBufMgr.DefaultBatchSize);
		EventBufMgr.getInstance().addEvent(characters, null, new Executable() {
			@Override
			public void execute(IDoer doer) {
				Player player = (Player) doer;
				if (player.isOnline()) {
					GameRoot.sendMessage(player.getIdentity(), actionEvent);
				}
			}
		}, EventBufMgr.DefaultBatchSize, pair.getKey(), pair.getValue());
	}

}
