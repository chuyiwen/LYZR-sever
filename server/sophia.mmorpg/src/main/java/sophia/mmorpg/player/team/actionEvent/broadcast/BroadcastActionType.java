package sophia.mmorpg.player.team.actionEvent.broadcast;

public class BroadcastActionType {

	/** 建立 **/
	public static final byte NOTIFY_ACTION_TYPE_CREATE = 1;

	/** 加入 **/
	public static final byte NOTIFY_ACTION_TYPE_JOIN = 2;

	/** 退出 **/
	public static final byte NOTIFY_ACTION_TYPE_QUIT = 3;

	/** 转让队长 **/
	public static final byte NOTIFY_ACTION_TYPE_HANDOVER = 4;

	/** 踢出 **/
	public static final byte NOTIFY_ACTION_TYPE_KICKEDOUT = 5;

	/** 解散队伍 **/
	public static final byte NOTIFY_ACTION_TYPE_Disband = 6;

}
