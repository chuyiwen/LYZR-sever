package sophia.mmorpg.monsterRefresh;

/**
 * 怪物死亡，出生次数
 * 
 */
public class ActionCountRecord {

	/** 死亡次数 **/
	private int deadCount;
	/** 出生次数 **/
	private int ariseCount;

	public ActionCountRecord() {
	}

	public int getDeadCount() {
		return deadCount;
	}

	public void setDeadCount(int deadCount) {
		this.deadCount = deadCount;
	}

	public int getAriseCount() {
		return ariseCount;
	}

	public void setAriseCount(int ariseCount) {
		this.ariseCount = ariseCount;
	}

}
