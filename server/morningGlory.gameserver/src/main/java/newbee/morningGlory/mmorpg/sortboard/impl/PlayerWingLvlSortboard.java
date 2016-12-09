package newbee.morningGlory.mmorpg.sortboard.impl;

import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.sortboard.Sortboard;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import sophia.mmorpg.player.Player;

public class PlayerWingLvlSortboard extends Sortboard{

	@Override
	public SortboardScoreData getSortboard(Player player) {
		String playerId = player.getId();
		String name = player.getName();
		int profession = player.getProfession();
		int score = 0;
		MGPlayerWingComponent  wingComponent = (MGPlayerWingComponent)  player.getTagged(MGPlayerWingComponent.Tag);
		MGPlayerWingRef playerWingRef = wingComponent.getPlayerWing().getPlayerWingRef();
		if (playerWingRef != null) {
			int stageLevel = playerWingRef.getCrtWingStageLevel();
			int startLevel = playerWingRef.getCrtWingStarLevel();
			score = stageLevel * 10 + startLevel;
		}
		return (new SortboardScoreData(playerId, name, profession, score));
	}

	@Override
	public int getScore(Player player) {
		int score = 0;
		MGPlayerWingComponent  wingComponent = (MGPlayerWingComponent)  player.getTagged(MGPlayerWingComponent.Tag);
		MGPlayerWingRef playerWingRef = wingComponent.getPlayerWing().getPlayerWingRef();
		if (playerWingRef != null) {
			int stageLevel = playerWingRef.getCrtWingStageLevel();
			int startLevel = playerWingRef.getCrtWingStarLevel();
			score = stageLevel * 10 + startLevel;
		}
		return score;
	}


}
