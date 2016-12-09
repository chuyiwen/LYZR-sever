/**
 * 
 */
package sophia.mmorpg.Mail;

/**
 * @author Administrator
 *
 */
public class GmMail extends Mail{
	
	private int playerMinLevel;
	private int playerMaxLevel;
	private long effectBeginTime; // 有效开始时间
	private long effectEndTime;  // 有效结束时间

	public int getPlayerMinLevel() {
		return playerMinLevel;
	}

	public void setPlayerMinLevel(int playerMinLevel) {
		this.playerMinLevel = playerMinLevel;
	}

	public int getPlayerMaxLevel() {
		return playerMaxLevel;
	}

	public void setPlayerMaxLevel(int playerMaxLevel) {
		this.playerMaxLevel = playerMaxLevel;
	}

	public long getEffectBeginTime() {
		return effectBeginTime;
	}

	public void setEffectBeginTime(long effectBeginTime) {
		this.effectBeginTime = effectBeginTime;
	}

	public long getEffectEndTime() {
		return effectEndTime;
	}

	public void setEffectEndTime(long effectEndTime) {
		this.effectEndTime = effectEndTime;
	}

}
