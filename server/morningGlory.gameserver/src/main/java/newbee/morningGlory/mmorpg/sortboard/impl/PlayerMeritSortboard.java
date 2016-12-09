package newbee.morningGlory.mmorpg.sortboard.impl;

import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;
import newbee.morningGlory.mmorpg.sortboard.Sortboard;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class PlayerMeritSortboard extends Sortboard{

	@Override
	public SortboardScoreData getSortboard(Player player) {
		String playerId = player.getId();
		String name = player.getName();
		int profession = player.getProfession();
		int score = 0;
		MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent) player.getTagged(MGPlayerPeerageComponent.Tag);
		if (peerageComponent.getPeeragerefMgr().getCrtPeerageRef() != null) {
			score = MGPropertyAccesser.getKnight(peerageComponent.getPeeragerefMgr().getCrtPeerageRef().getProperty());
		}
		
		return new SortboardScoreData(playerId, name, profession, score);
	}

	@Override
	public int getScore(Player player) {
		int score = 0;
		MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent) player.getTagged(MGPlayerPeerageComponent.Tag);
		if (peerageComponent.getPeeragerefMgr().getCrtPeerageRef() != null) {
			score = MGPropertyAccesser.getKnight(peerageComponent.getPeeragerefMgr().getCrtPeerageRef().getProperty());
		}
		return score;
	}


}
