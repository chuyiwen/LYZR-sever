package newbee.morningGlory.mmorpg.sortboard.impl;

import newbee.morningGlory.mmorpg.sortboard.Sortboard;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import sophia.mmorpg.player.Player;

public class PlayerLvlSortboard extends Sortboard{

	@Override
	public SortboardScoreData getSortboard(Player player) {
		String playerId = player.getId();
		String name = player.getName();
		int profession = player.getProfession();
		int score = player.getLevel();
		
		return new SortboardScoreData(playerId, name, profession, score);
	}

	@Override
	public int getScore(Player player) {
		return player.getLevel();
	}

}