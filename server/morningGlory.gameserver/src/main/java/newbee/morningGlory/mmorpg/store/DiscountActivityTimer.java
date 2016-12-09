package newbee.morningGlory.mmorpg.store;

import java.util.Collection;

import newbee.morningGlory.mmorpg.player.activity.discount.DiscountRefDataManager;
import newbee.morningGlory.mmorpg.player.activity.discount.DiscountTimeMgr;
import newbee.morningGlory.mmorpg.player.store.MGPlayerShopComponent;

import org.apache.log4j.Logger;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;

public class DiscountActivityTimer implements SFTimeChimeListener {
	private static Logger logger = Logger.getLogger(DiscountActivityTimer.class);
	
	@Override
	public void handleServiceShutdown() {

	}

	@Override
	public void handleTimeChimeCancel() {

	}

	@Override
	public void handleTimeChime() {
		if (DiscountTimeMgr.isRefreshTimeOver()) {
			logger.info("=============discount Timer is running!===============");
			DiscountRefDataManager.refreshDiscountItems();
			
			notifyOnlinePlayer();
		}
	}
	
	private static void notifyOnlinePlayer() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
	
		Collection<Player> onlinePlayerList = playerManager.getOnlinePlayerList();
	
		for (Player player : onlinePlayerList) {
			MGPlayerShopComponent playeshoComponent = (MGPlayerShopComponent)player.getTagged(MGPlayerShopComponent.Tag);
		
			playeshoComponent.sendDiscountBeginOrEndMsg(MGPlayerShopComponent.Discount_End);
			playeshoComponent.sendDiscountBeginOrEndMsg(MGPlayerShopComponent.Discount_Begin);
		}
	}
}
