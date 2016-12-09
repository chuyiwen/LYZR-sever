package sophia.mmorpg.monsterRefresh;

import java.util.HashMap;
import java.util.Map;

public class SceneMonsterRecordMgr {

	/** 记录怪物出生死亡次数 **/
	private Map<String, ActionCountRecord> monsterRefIdActionCountMap;
	

	public SceneMonsterRecordMgr() {
		this.monsterRefIdActionCountMap = new HashMap<String, ActionCountRecord>();
	}

	public int getMonsterActionCount(String monsterRefId, byte actionType) {
		ActionCountRecord actionCountRecord = getNotNullActionCountRecord(monsterRefId);
		if (actionType == RefreshConditionType.OnPreMonster_Dead_Type) {
			return actionCountRecord.getDeadCount();
		} else if (actionType == RefreshConditionType.OnPreMonster_Arise_Type) {
			return actionCountRecord.getAriseCount();
		}
		return 0;
	}

	public void setMonsterActionCount(String monsterRefId, int actionCount, byte actionType) {
		ActionCountRecord actionCountRecord = getNotNullActionCountRecord(monsterRefId);
		if (actionType == RefreshConditionType.OnPreMonster_Dead_Type) {
			actionCountRecord.setDeadCount(actionCount);
		} else if (actionType == RefreshConditionType.OnPreMonster_Arise_Type) {
			actionCountRecord.setAriseCount(actionCount);
		} 

		monsterRefIdActionCountMap.put(monsterRefId, actionCountRecord);
	}

	private ActionCountRecord getNotNullActionCountRecord(String monsterRefId) {
		ActionCountRecord actionCountRecord = monsterRefIdActionCountMap.get(monsterRefId);
		if (actionCountRecord == null) {
			actionCountRecord = new ActionCountRecord();
		}
		return actionCountRecord;
	}

}
