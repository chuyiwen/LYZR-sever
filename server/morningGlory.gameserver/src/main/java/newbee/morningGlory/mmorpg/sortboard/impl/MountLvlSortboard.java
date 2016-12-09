package newbee.morningGlory.mmorpg.sortboard.impl;

import newbee.morningGlory.mmorpg.sortboard.Sortboard;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MountLvlSortboard extends Sortboard {

	// 坐骑按坐骑等级（x阶x级）排序
	@Override
	public SortboardScoreData getSortboard(Player player) {
		String playerId = player.getId();
		String name = player.getName();
		int profession = player.getProfession();
		int score = 0;

		PlayerMountComponent mountComponent = player.getPlayerMountComponent();
		if (mountComponent.getMountManager().getCrtMount() != null) {
			PropertyDictionary pd = mountComponent.getMountManager().getCrtMount().getMountRef().getProperty();
			int stageLevel = MGPropertyAccesser.getStageLevel(pd);
			int startLevel = MGPropertyAccesser.getStartLevel(pd);
			score = stageLevel * 10 + startLevel;
		}

		return new SortboardScoreData(playerId, name, profession, score);
	}

	@Override
	public int getScore(Player player) {
		int score = 0;

		PlayerMountComponent mountComponent = player.getPlayerMountComponent();
		if (mountComponent.getMountManager().getCrtMount() != null) {
			PropertyDictionary pd = mountComponent.getMountManager().getCrtMount().getMountRef().getProperty();
			int stageLevel = MGPropertyAccesser.getStageLevel(pd);
			int startLevel = MGPropertyAccesser.getStartLevel(pd);
			score = stageLevel * 10 + startLevel;
		}
		return score;
	}

}
