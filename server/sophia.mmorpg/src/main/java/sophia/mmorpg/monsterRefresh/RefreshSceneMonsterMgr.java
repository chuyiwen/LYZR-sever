package sophia.mmorpg.monsterRefresh;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class RefreshSceneMonsterMgr {
	private static final Logger logger = Logger.getLogger(RefreshSceneMonsterMgr.class);

	private Map<String, List<RefreshMonsterRefData>> refreshMonsterRefMap;

	/** 怪物刷新规则 **/
	private RefreshConditionTypeDataMgr refreshConditionTypeDataMgr;

	/** 记录怪物出生，死亡次数 **/
	private SceneMonsterRecordMgr sceneMonsterRecordMgr;

	private final ReentrantLock monsterRecordLock = new ReentrantLock();

	public RefreshSceneMonsterMgr(List<RefreshMonsterRefData> refreshMonsterRefList) {
		if (refreshMonsterRefList == null) {
			return;
		}
		this.refreshMonsterRefMap = new HashMap<String, List<RefreshMonsterRefData>>();
		for (RefreshMonsterRefData refreshMonsterRefData : refreshMonsterRefList) {
			List<RefreshMonsterRefData> list = getNotNullRefreshMonsterRefDataList(refreshMonsterRefData.getMonsterGroup().getMonsterRefId());
			list.add(refreshMonsterRefData);
		}
		this.refreshConditionTypeDataMgr = new RefreshConditionTypeDataMgr();
		refreshConditionTypeDataMgr.convert(refreshMonsterRefList);
		this.sceneMonsterRecordMgr = new SceneMonsterRecordMgr();
	}

	private List<RefreshMonsterRefData> getNotNullRefreshMonsterRefDataList(String monsterRefId) {
		List<RefreshMonsterRefData> list = this.refreshMonsterRefMap.get(monsterRefId);
		if (list == null) {
			list = new ArrayList<RefreshMonsterRefData>();
			this.refreshMonsterRefMap.put(monsterRefId, list);
		}
		return list;
	}

	/**
	 * 当副本场景创建时，怪物出现
	 * 
	 * @return
	 */
	public List<RefreshMonsterRefData> getMonsterGroupListOnSceneCreated() {
		List<RefreshMonsterRefData> refreshMonsterRefDataList = new ArrayList<>();
		if (this.refreshMonsterRefMap == null) {
			return null;
		}
		Collection<List<RefreshMonsterRefData>> refreshMonsterRefDataListList = this.refreshMonsterRefMap.values();
		if (refreshMonsterRefDataListList == null) {
			return refreshMonsterRefDataList;
		}
		for (List<RefreshMonsterRefData> refreshMonsterRefList : refreshMonsterRefDataListList) {
			if (refreshMonsterRefList == null) {
				continue;
			}
			for (RefreshMonsterRefData refreshMonsterRef : refreshMonsterRefList) {
				Preconditions.checkNotNull(refreshMonsterRef);
				RefreshConditionTypeData refreshConditionTypeData = refreshMonsterRef.getRefreshConditionTypeData();
				Preconditions.checkNotNull(refreshConditionTypeData);
				if (refreshConditionTypeData.checkRefreshConditionType(RefreshConditionType.OnSceneCreated_Type)) {
					refreshMonsterRefDataList.add(refreshMonsterRef);
				}
			}
		}
		return refreshMonsterRefDataList;
	}

	/**
	 * 监听当指定怪物（组）出现之后，出现的怪物
	 * 
	 * @param ariseMonsterRefId
	 * @param ariseCount
	 * @return
	 */
	public List<RefreshMonsterRefData> listenerOnPreMonsterArise(String ariseMonsterRefId, int ariseCount) {
		return listenerOnPreMonsterAction(ariseMonsterRefId, ariseCount, RefreshConditionType.OnPreMonster_Arise_Type);
	}

	/**
	 * 监听当指定怪物（组）死亡之后，出现的怪物
	 * 
	 * @param deadMonsterRefId
	 * @param deadCount
	 * @return
	 */
	public List<RefreshMonsterRefData> listenerOnPreMonsterDead(String deadMonsterRefId, int deadCount) {
		return listenerOnPreMonsterAction(deadMonsterRefId, deadCount, RefreshConditionType.OnPreMonster_Dead_Type);
	}

	// ========================================================================================================================
	private List<RefreshMonsterRefData> listenerOnPreMonsterAction(String monsterRefId, int count, byte refreshConditionType) {
		if (Strings.isNullOrEmpty(monsterRefId) || count <= 0) {
			return new ArrayList<>(0);
		}
		if (refreshConditionTypeDataMgr == null) {
			return null;
		}
		List<RefreshConditionTypeData> refreshConditionTypeDataList = refreshConditionTypeDataMgr.getRefreshConditionTypeDataList(monsterRefId);
		List<RefreshMonsterRefData> refreshMonsterRefDataList = new ArrayList<>();
		for (RefreshConditionTypeData refreshConditionTypeData : refreshConditionTypeDataList) {
			Preconditions.checkNotNull(refreshConditionTypeData);
			// 检查是否需要监听该怪物出生/死亡
			if (refreshConditionTypeData.checkRefreshConditionType(refreshConditionType) && refreshConditionTypeData.getConditionMonsterRefId().equals(monsterRefId)) {
				// 怪物总的出生/死亡次数
				monsterRecordLock.lock();
				try {
					int totalAriseCount = sceneMonsterRecordMgr.getMonsterActionCount(monsterRefId, refreshConditionType) + count;

					boolean result = refreshConditionTypeData.eligible(monsterRefId, totalAriseCount);
					// 不满足条件
					if (result != true) {
						sceneMonsterRecordMgr.setMonsterActionCount(monsterRefId, totalAriseCount, refreshConditionType);
						continue;
					}

					// 记录多余的出生/死亡数量
					int moreAriseCount = totalAriseCount - refreshConditionTypeData.getConditionNumber();
					sceneMonsterRecordMgr.setMonsterActionCount(monsterRefId, moreAriseCount, refreshConditionType);
				} catch (Exception e) {
					logger.error(e);
				} finally {
					monsterRecordLock.unlock();
				}

				List<RefreshMonsterRefData> refreshMonsterRefList = this.refreshMonsterRefMap.get(refreshConditionTypeData.getRefreshMonsterRefId());
				Preconditions.checkNotNull(refreshMonsterRefList);

				for (RefreshMonsterRefData refreshMonsterRefData : refreshMonsterRefList) {
					Preconditions.checkNotNull(refreshMonsterRefData);
					Preconditions.checkNotNull(refreshMonsterRefData.getMonsterGroup());
					if (refreshMonsterRefData.isOnPreMonster()) {
						refreshMonsterRefDataList.add(refreshMonsterRefData);
					}
				}

			}
		}
		return refreshMonsterRefDataList;
	}

	/**
	 * 当处于场景怪物刷新时间段时，怪物出现
	 * 
	 * @return
	 */
	public List<RefreshMonsterRefData> getMonsterGroupListInSceneTimeRange() {
		List<RefreshMonsterRefData> refreshMonsterRefDataList = new ArrayList<>();
		if (this.refreshMonsterRefMap == null) {
			return null;
		}
		Collection<List<RefreshMonsterRefData>> refreshMonsterRefDataListList = this.refreshMonsterRefMap.values();
		if (refreshMonsterRefDataListList == null) {
			return refreshMonsterRefDataList;
		}
		for (List<RefreshMonsterRefData> refreshMonsterRefList : refreshMonsterRefDataListList) {
			if (refreshMonsterRefList == null) {
				continue;
			}
			for (RefreshMonsterRefData refreshMonsterRef : refreshMonsterRefList) {
				Preconditions.checkNotNull(refreshMonsterRef);
				RefreshConditionTypeData refreshConditionTypeData = refreshMonsterRef.getRefreshConditionTypeData();
				Preconditions.checkNotNull(refreshConditionTypeData);
				if (refreshConditionTypeData.checkRefreshConditionType(RefreshConditionType.InSceneTimeRange_Type)) {
					if (isInTimeRange(refreshConditionTypeData)) {
						refreshMonsterRefDataList.add(refreshMonsterRef);
					}
					
				}
			}
		}
		return refreshMonsterRefDataList;
	}
	
	public boolean isInTimeRange(RefreshConditionTypeData refreshConditionTypeData) {
		boolean ret = false;
		String refreshMonsterStartTime = refreshConditionTypeData.getRefreshMonsterStartTime();
		long refreshMonsterLastTime = refreshConditionTypeData.getRefreshMonsterLastTime();
		long nowTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int ds = cal.get(Calendar.DAY_OF_MONTH);
		String data = new StringBuffer().append(year).append("-").append(m < 10 ? (new StringBuffer("0").append(m).toString()) : m).append("-")
				.append(ds < 10 ? (new StringBuffer("0").append(ds).toString()) : ds).append(" ").append(refreshMonsterStartTime).toString();
		long timestamp = Timestamp.valueOf(data).getTime();
		if (timestamp <= nowTime && nowTime < (timestamp + refreshMonsterLastTime*1000)) {
			ret = true;
		}
		return ret;
	}

}
