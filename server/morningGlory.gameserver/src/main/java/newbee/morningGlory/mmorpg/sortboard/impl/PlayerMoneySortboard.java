package newbee.morningGlory.mmorpg.sortboard.impl;

import newbee.morningGlory.mmorpg.sortboard.Sortboard;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.money.PlayerMoneyComponent;

public class PlayerMoneySortboard extends Sortboard{

	//按玩家金币（不包括元宝和绑定元宝）数量排序
	@Override
	public SortboardScoreData getSortboard(Player player) {
		String playerId = player.getId();
		String name = player.getName();
		int profession = player.getProfession();
		PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
		int score = playMoneyCompoent.getGold();
		
		return (new SortboardScoreData(playerId, name, profession, score));
	}

	@Override
	public int getScore(Player player) {
		PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
		int score = playMoneyCompoent.getGold();
		return score;
	}


}
