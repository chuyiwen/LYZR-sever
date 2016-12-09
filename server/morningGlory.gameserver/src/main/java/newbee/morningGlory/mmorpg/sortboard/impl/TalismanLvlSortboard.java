package newbee.morningGlory.mmorpg.sortboard.impl;

import newbee.morningGlory.mmorpg.player.talisman.MGPlayerCitta;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerTalismanComponent;
import newbee.morningGlory.mmorpg.sortboard.Sortboard;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class TalismanLvlSortboard extends Sortboard {

	// 法宝按玩家身上所有法宝战力之和排序
	@Override
	public SortboardScoreData getSortboard(Player player) {
		String playerId = player.getId();
		String name = player.getName();
		int profession = player.getProfession();
		int score = 0;
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		MGPlayerCitta playerTalismanMgr = talismanComponent.getPlayerCitta();
		if(playerTalismanMgr.getCittaRef() != null){
			int fightPower = MGPropertyAccesser.getFightValue(playerTalismanMgr.getCittaRef().getProperty());
			score += fightPower;
		}
		return (new SortboardScoreData(playerId, name, profession, score));
	}

	@Override
	public int getScore(Player player) {
		int score = 0;
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		MGPlayerCitta playerTalismanMgr = talismanComponent.getPlayerCitta();
		if(playerTalismanMgr.getCittaRef() != null){
			int fightPower = MGPropertyAccesser.getFightValue(playerTalismanMgr.getCittaRef().getProperty());
			score += fightPower;
		}
		return score;
	}

}
