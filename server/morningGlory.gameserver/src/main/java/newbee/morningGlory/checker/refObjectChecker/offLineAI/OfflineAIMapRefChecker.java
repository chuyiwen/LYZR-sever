package newbee.morningGlory.checker.refObjectChecker.offLineAI;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.OfflineAIMapRef;

import org.apache.commons.lang3.StringUtils;

import sophia.game.ref.GameRefObject;

public class OfflineAIMapRefChecker extends BaseRefChecker<OfflineAIMapRef> {

	@Override
	public String getDescription() {
		return "OfflineAIMapRefChecker";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		OfflineAIMapRef ref = (OfflineAIMapRef) gameRefObject;
		String id = ref.getId();
		if(!id.startsWith("offlineAIMap_")) {
			error(gameRefObject, "错误的离线AI地图id, 离线AI地图id以offlineAIMap_开头! id = " + id);
		} else if (StringUtils.containsWhitespace(id)) {
			error(gameRefObject, "错误的离线AI地图id, 离线AI地图id不能包含空格。 id = \'" + id + "\'");
		}
	}

}
