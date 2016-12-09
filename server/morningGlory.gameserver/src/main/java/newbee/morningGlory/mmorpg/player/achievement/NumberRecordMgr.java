package newbee.morningGlory.mmorpg.player.achievement;

public class NumberRecordMgr {
	private int killMonsterNumber;
	private int killBossNumber;
	private int strengthCount;
	private int washCount;

	public synchronized int getKillMonsterNumber() {
		return killMonsterNumber;
	}

	public synchronized void setKillMonsterNumber(int killMonsterNumber) {
		this.killMonsterNumber = killMonsterNumber;
	}

	public synchronized int getKillBossNumber() {
		return killBossNumber;
	}

	public synchronized void setKillBossNumber(int killBossNumber) {
		this.killBossNumber = killBossNumber;
	}

	public synchronized int getStrengthCount() {
		return strengthCount;
	}

	public synchronized void setStrengthCount(int strengthCount) {
		this.strengthCount = strengthCount;
	}

	public synchronized int getWashCount() {
		return washCount;
	}

	public synchronized void setWashCount(int washCount) {
		this.washCount = washCount;
	}

	public synchronized void addKillMonsterNumber() {
		this.killMonsterNumber ++;
	}

	public synchronized void addKillBossNumber() {
		this.killBossNumber ++;
	}

	public synchronized void addStrengthCount() {
		this.strengthCount ++;
	}

	public synchronized void addWashCount() {
		this.washCount++;
	}
}
