package sophia.mmorpg.monsterRefresh;

public class MonsterGroupRefData {
	private String monsterRefId;

	private int number;

	/** 延迟刷新时间 **/
	private int delaySec;

	public MonsterGroupRefData(String monsterRefId, int number, int delaySec) {
		super();
		this.monsterRefId = monsterRefId;
		this.number = number;
		this.delaySec = delaySec;
	}

	public String getMonsterRefId() {
		return monsterRefId;
	}

	public int getNumber() {
		return number;
	}

	public int getDelaySec() {
		return delaySec;
	}

}
