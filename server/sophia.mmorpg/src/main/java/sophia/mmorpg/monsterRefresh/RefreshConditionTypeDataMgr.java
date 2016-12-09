package sophia.mmorpg.monsterRefresh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class RefreshConditionTypeDataMgr {

	private Map<String, List<RefreshConditionTypeData>> conditionMonsterRefIdToRefreshConditionTypeDataListMap;

	public RefreshConditionTypeDataMgr() {
		this.conditionMonsterRefIdToRefreshConditionTypeDataListMap = new HashMap<String, List<RefreshConditionTypeData>>();
	}

	public List<RefreshConditionTypeData> getRefreshConditionTypeDataList(String conditionMonsterRefId) {
		if (this.conditionMonsterRefIdToRefreshConditionTypeDataListMap.containsKey(conditionMonsterRefId)) {
			return this.conditionMonsterRefIdToRefreshConditionTypeDataListMap.get(conditionMonsterRefId);
		}
		return new ArrayList<>(0);
	}

	public void convert(List<RefreshMonsterRefData> refreshMonsterRefList) {
		if(refreshMonsterRefList == null){
			return;
		}
		for (RefreshMonsterRefData refreshMonsterRef : refreshMonsterRefList) {
			Preconditions.checkNotNull(refreshMonsterRef);
			if (refreshMonsterRef.isOnPreMonster()) {
				RefreshConditionTypeData refreshConditionTypeData = refreshMonsterRef.getRefreshConditionTypeData();
				String monsterRefId = refreshConditionTypeData.getConditionMonsterRefId();
				if (!Strings.isNullOrEmpty(monsterRefId)) {
					List<RefreshConditionTypeData> refreshConditionTypeDataList = getNotNullRefreshConditionTypeDataList(monsterRefId);
					refreshConditionTypeDataList.add(refreshConditionTypeData);
				}
			}
		}
	}

	private List<RefreshConditionTypeData> getNotNullRefreshConditionTypeDataList(String conditionMonsterRefId) {
		List<RefreshConditionTypeData> refreshConditionTypeDataList = conditionMonsterRefIdToRefreshConditionTypeDataListMap.get(conditionMonsterRefId);
		if (refreshConditionTypeDataList == null) {
			refreshConditionTypeDataList = new ArrayList<>();
			conditionMonsterRefIdToRefreshConditionTypeDataListMap.put(conditionMonsterRefId, refreshConditionTypeDataList);
		}
		return refreshConditionTypeDataList;
	}

}
