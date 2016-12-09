package sophia.mmorpg.monsterRefresh;

public class RefreshConditionTypeData {
	private String refreshMonsterRefId;
	private String conditionMonsterRefId;
	private int conditionNumber;
	private byte refreshConditionType;
	private String refreshMonsterStartTime;
	private long refreshMonsterLastTime;

	public RefreshConditionTypeData(String refreshMonsterRefId, String conditionMonsterRefId, int conditionNumber, byte refreshConditionType, String refreshMonsterStartTime, long refreshMonsterLastTime) {
		super();
		this.refreshMonsterRefId = refreshMonsterRefId;
		this.conditionMonsterRefId = conditionMonsterRefId;
		this.conditionNumber = conditionNumber;
		this.refreshConditionType = refreshConditionType;
		this.refreshMonsterStartTime = refreshMonsterStartTime;
		this.refreshMonsterLastTime = refreshMonsterLastTime;
	}

	public RefreshConditionTypeData() {
	}

	public String getRefreshMonsterRefId() {
		return refreshMonsterRefId;
	}

	public String getConditionMonsterRefId() {
		return conditionMonsterRefId;
	}

	public int getConditionNumber() {
		return conditionNumber;
	}
	
	public void setRefreshMonsterRefId(String refreshMonsterRefId) {
		this.refreshMonsterRefId = refreshMonsterRefId;
	}

	public void setConditionMonsterRefId(String conditionMonsterRefId) {
		this.conditionMonsterRefId = conditionMonsterRefId;
	}

	public void setConditionNumber(int conditionNumber) {
		this.conditionNumber = conditionNumber;
	}

	public void setRefreshConditionType(byte refreshConditionType) {
		this.refreshConditionType = refreshConditionType;
	}

	/**
	 * 怪物刷新规则类型
	 * 
	 * @return
	 */
	public byte getRefreshConditionType() {
		return refreshConditionType;
	}

	public boolean checkRefreshConditionType(byte conditionType) {
		return this.getRefreshConditionType() == conditionType;
	}

	/**
	 * 这个怪物组的数量是否满足条件
	 * 
	 * @param monsterRefId
	 * @param count
	 * @return
	 */
	public boolean eligible(String monsterRefId, int count) {
		if (count < this.getConditionNumber()) {
			return false;
		}
		if (!conditionMonsterRefId.equals(monsterRefId)) {
			return false;
		}
		return true;
	}

	public String getRefreshMonsterStartTime() {
		return refreshMonsterStartTime;
	}

	public void setRefreshMonsterStartTime(String refreshMonsterStartTime) {
		this.refreshMonsterStartTime = refreshMonsterStartTime;
	}

	public long getRefreshMonsterLastTime() {
		return refreshMonsterLastTime;
	}

	public void setRefreshMonsterLastTime(long refreshMonsterLastTime) {
		this.refreshMonsterLastTime = refreshMonsterLastTime;
	}

}
