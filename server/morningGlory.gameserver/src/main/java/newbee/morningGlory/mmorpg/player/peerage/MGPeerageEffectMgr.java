package newbee.morningGlory.mmorpg.player.peerage;

import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.player.Player;


public class MGPeerageEffectMgr  {
	private Player player;

	public MGPeerageEffectMgr(Player player) {
		this.player = player;
	}

	public void restore(MGPeerageRef peerageRef) {
		attachWithoutSnapshot(peerageRef);
	}

	public void attach(MGPeerageRef peerageRef) {

		FightPropertyEffectFacade.attachAndNotify(player, peerageRef.getProperty());
	}

	public void detach(MGPeerageRef peerageRef) {

		FightPropertyEffectFacade.detachAndNotify(player, peerageRef.getProperty());
	}

	public void detachAndSnapshot(MGPeerageRef peerageRef) {

		FightPropertyEffectFacade.detachAndSnapshot(player, peerageRef.getProperty());
	}
	
	public void attachWithoutSnapshot(MGPeerageRef peerageRef) {
		
		FightPropertyEffectFacade.attachWithoutSnapshot(player, peerageRef.getProperty());
	}
	
	
	
}
