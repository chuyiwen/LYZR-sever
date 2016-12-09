package sophia.mmorpg.sceneActivities;

import sophia.mmorpg.player.Player;

public interface SceneActivityInterface {
	/** 活动结束 */
	public static final byte ACTIVITY_END = 1;
	/** 活动开始 */
	public static final byte ACTIVITY_START = 2;
	/** 活动预开始 */
	public static final byte ACTIVITY_PRE_START = 3;
	/** 活动预结束 */
	public static final byte ACTIVITY_PRE_END = 4;
	/** 默认预状态 */
	public static final byte ACTIVITY_PRE_DEFAULT = 5;
	
	public byte getCrtActivityState();
	
	public byte getPreActivityState();
	
	public boolean checkEnter(Player player);
	
	public boolean checkLeave(Player player);
	
	public abstract boolean onEnter(Player player);
	
	public abstract boolean onLeave(Player player);
}
