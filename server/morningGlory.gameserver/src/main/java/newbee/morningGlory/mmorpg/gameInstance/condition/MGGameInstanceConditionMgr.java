package newbee.morningGlory.mmorpg.gameInstance.condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.mmorpg.core.linyuesheng.LinYueShengModeCondition;
import sophia.mmorpg.player.Player;

public class MGGameInstanceConditionMgr {
	/** 副本开放条件 **/
	private Map<String, List<LinYueShengModeCondition<Player>>> gameInstanceSceneRefIdToOpenConditionListMap = new HashMap<>();

	/** 副本进入条件 **/
	private Map<String, List<LinYueShengModeCondition<Player>>> gameInstanceSceneRefIdToEnterConditionListMap = new HashMap<>();

	public MGGameInstanceConditionMgr() {

	}

	public List<LinYueShengModeCondition<Player>> getGameInstanceOpenConditionList(String gameInstanceSceneRefId) {
		return gameInstanceSceneRefIdToOpenConditionListMap.get(gameInstanceSceneRefId);
	}

	public void setGameInstanceOpenConditionList(String gameInstanceRefId, List<LinYueShengModeCondition<Player>> gameInstanceOpenConditionList) {
		gameInstanceSceneRefIdToOpenConditionListMap.put(gameInstanceRefId, gameInstanceOpenConditionList);
	}

	public List<LinYueShengModeCondition<Player>> getGameInstanceEnterConditionList(String gameInstanceSceneRefId) {
		return this.gameInstanceSceneRefIdToEnterConditionListMap.get(gameInstanceSceneRefId);
	}

	public void setGameInstanceEnterConditionList(String gameInstanceRefId, List<LinYueShengModeCondition<Player>> gameInstanceEnterConditionList) {
		gameInstanceSceneRefIdToEnterConditionListMap.put(gameInstanceRefId, gameInstanceEnterConditionList);
	}

}
