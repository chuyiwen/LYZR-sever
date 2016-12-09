package sophia.mmorpg.player.team.actionEvent;

public final class ReplyJoinTeamType {

	// 放弃（玩家30秒没有响应）
	public static final byte REPLY_JOIN_TEAM_TYPE_GIVE_UP = 1;
	// 通过加入队伍
	public static final byte REPLY_JOIN_TEAM_TYPE_YES = 2;
	// 拒绝加入队伍
	public static final byte REPLY_JOIN_TEAM_TYPE_NO = 3;

	private ReplyJoinTeamType() {

	}
}
