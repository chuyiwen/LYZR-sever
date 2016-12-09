package newbee.morningGlory.mmorpg.player.achievement;

public class CastleWarRecord {
	/** 进入皇宫时间 */
	private long lastInPalaceStamp;

	/** 上次攻城战胜败 */
	private boolean lastCastleWarResult;

	/** 参加公会战次数 */
	private int joinCastleWarCount;

	/** 赢得几次攻城战 */
	private int winCastleWarCount;

	/** 连续赢得攻城战 */
	private int ConsecutiveCastelWarCount;

	/** 以会长身份赢得攻城战胜利 */
	private int winAsChairmanCastleWarCount;

	/** 在攻城战中击杀玩家数量 */
	private int killEnemyInCastleWarCount;

	/** 击杀祥瑞麒麟次数 */
	private int killCastleWarBossCount;

	/** 加入公会 */
	private int addUnionCount;

	/** 创建公会 */
	private int createUnionCount;

	/** 公会满员 */
	private int fullUnionCount;

	public synchronized void recordEnterCastleWar(long millis) {
		this.joinCastleWarCount++;
		setLastInPalaceStamp(millis);
	}

	public synchronized void addKillCastleWarBossCount() {
		this.killCastleWarBossCount++;
	}
	
	public synchronized void addKillEnmeyCount() {
		this.killEnemyInCastleWarCount++;
	}

	public synchronized void addWinAsChairmanCastleWarCount() {
		this.winAsChairmanCastleWarCount++;
	}

	public synchronized void resetConsecutiveCastelWarCount() {
		this.ConsecutiveCastelWarCount = 0;
		lastCastleWarResult = false;
	}
	
	public synchronized void addConsecutiveCastelWarCount() {
		this.winCastleWarCount++;
		this.ConsecutiveCastelWarCount++;
		lastCastleWarResult = true;
	}

	public synchronized void addUnionCount() {
		this.addUnionCount++;
	}

	public synchronized void createUnionCount() {
		this.createUnionCount++;
	}

	public synchronized void fullUnionCount() {
		this.fullUnionCount++;
	}

	// ============================

	public synchronized long getLastInPalaceStamp() {
		return lastInPalaceStamp;
	}

	public synchronized void setLastInPalaceStamp(long lastInPalaceStamp) {
		this.lastInPalaceStamp = lastInPalaceStamp;
	}

	public synchronized int getJoinCastleWarCount() {
		return joinCastleWarCount;
	}

	public synchronized void setJoinCastleWarCount(int joinCastleWarCount) {
		this.joinCastleWarCount = joinCastleWarCount;
	}

	public synchronized int getWinCastleWarCount() {
		return winCastleWarCount;
	}

	public synchronized void setWinCastleWarCount(int winCastleWarCount) {
		this.winCastleWarCount = winCastleWarCount;
	}

	public synchronized int getConsecutiveCastelWarCount() {
		return ConsecutiveCastelWarCount;
	}

	public synchronized void setConsecutiveCastelWarCount(int consecutiveCastelWarCount) {
		ConsecutiveCastelWarCount = consecutiveCastelWarCount;
	}

	public synchronized int getWinAsChairmanCastleWarCount() {
		return winAsChairmanCastleWarCount;
	}

	public synchronized void setWinAsChairmanCastleWarCount(int winAsChairmanCastleWarCount) {
		this.winAsChairmanCastleWarCount = winAsChairmanCastleWarCount;
	}

	public synchronized int getKillEnemyInCastleWarCount() {
		return killEnemyInCastleWarCount;
	}

	public synchronized void setKillEnemyInCastleWarCount(int killEnemyInCastleWarCount) {
		this.killEnemyInCastleWarCount = killEnemyInCastleWarCount;
	}

	public synchronized int getKillCastleWarBossCount() {
		return killCastleWarBossCount;
	}

	public synchronized void setKillCastleWarBossCount(int killCastleWarBossCount) {
		this.killCastleWarBossCount = killCastleWarBossCount;
	}

	public synchronized boolean isLastCastleWarResult() {
		return lastCastleWarResult;
	}

	public synchronized void setLastCastleWarResult(boolean lastCastleWarResult) {
		this.lastCastleWarResult = lastCastleWarResult;
	}

	public synchronized int getAddUnionCount() {
		return addUnionCount;
	}

	public synchronized void setAddUnionCount(int addUnionCount) {
		this.addUnionCount = addUnionCount;
	}

	public synchronized int getCreateUnionCount() {
		return createUnionCount;
	}

	public synchronized void setCreateUnionCount(int createUnionCount) {
		this.createUnionCount = createUnionCount;
	}

	public synchronized int getFullUnionCount() {
		return fullUnionCount;
	}

	public synchronized void setFullUnionCount(int fullUnionCount) {
		this.fullUnionCount = fullUnionCount;
	}

}
