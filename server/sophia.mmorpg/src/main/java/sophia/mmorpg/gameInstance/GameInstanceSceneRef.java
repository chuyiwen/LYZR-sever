package sophia.mmorpg.gameInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.core.linyuesheng.LinYueShengModeCondition;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class GameInstanceSceneRef {
	private String Id;

	private PropertyDictionary gameInstanceScenePD;

	/** 任务ID **/
	private List<String> conditionField;
	/** 类型具体数据(副本层通关条件) **/
	private CompleteCondition completeCondition;

	private GameInstanceRef gameInstanceRef;

	private Map<String, Integer> consumptionItems = new HashMap<>();

	/** 副本层完成条件 **/
	List<LinYueShengModeCondition<Player>> sceneFinishConditionList = new ArrayList<LinYueShengModeCondition<Player>>();

	public List<String> getConditionField() {
		return conditionField;
	}

	public void setConditionField(List<String> conditionField) {
		this.conditionField = conditionField;
	}

	public void setSucceedConditionData(String succeedConditionData) {
		if (this.completeCondition == null) {
			this.completeCondition = new CompleteCondition();
		}
		this.completeCondition.setCompleteCondition(succeedConditionData);
	}

	public GameInstanceRef getGameInstanceRef() {
		return gameInstanceRef;
	}

	public void setGameInstanceRef(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}

	public CompleteCondition getCompleteCondition() {
		return completeCondition;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public Map<String, Integer> getConsumptionItems() {
		return consumptionItems;
	}

	public void setConsumptionItems(Map<String, Integer> consumptionItems) {
		this.consumptionItems = consumptionItems;
	}

	public PropertyDictionary getGameInstanceScenePD() {
		return gameInstanceScenePD;
	}

	public void setGameInstanceScenePD(PropertyDictionary gameInstanceScenePD) {
		this.gameInstanceScenePD = gameInstanceScenePD;
	}

	public String getSceneRefId() {
		return MGPropertyAccesser.getSceneRefId(getGameInstanceScenePD());
	}

	public byte getSucceedType() {
		return MGPropertyAccesser.getSucceedType(getGameInstanceScenePD());
	}

	public List<LinYueShengModeCondition<Player>> getSceneFinishConditionList() {
		return sceneFinishConditionList;
	}

	public void setSceneFinishConditionList(List<LinYueShengModeCondition<Player>> sceneFinishConditionList) {
		this.sceneFinishConditionList = sceneFinishConditionList;
	}

}
