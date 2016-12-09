package sophia.mmorpg.monsterRefresh;

public class RefreshMonsterGroupDelay {

	private RefreshMonsterRefData refreshMonsterRefData;

	private long time = (System.currentTimeMillis() / 1000);
	
	public RefreshMonsterGroupDelay(RefreshMonsterRefData refreshMonsterRefData) {
		super();
		this.refreshMonsterRefData = refreshMonsterRefData;
	}

	public RefreshMonsterRefData getRefreshMonsterRefData() {
		return refreshMonsterRefData;
	}

	public long getTime() {
		return time;
	}

}
