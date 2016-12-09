package newbee.morningGlory.mmorpg.ladder;

public class MGLadderMacro {
	// 天梯开放等级
	public static final int Default_Arena_Openlevel = 35;
	// 战斗cd清除, 每分钟消耗元宝
	public static final int Default_UnbinedGold_ClearCDEveryMin = 10;

	// 排名趋势
	public static final byte LadderTrend_Fair = 0;
	public static final byte LadderTrend_Up = 1;
	public static final byte LadderTrend_Down = 2;
	
	// 天梯列表最大显示个数
	public static final int Default_ShowLadderMembers_Count = 50;
	// 天梯最大排名人数
	public static final int Max_Member_Count = 1000;
	// 每天可以挑战次数
	public static final int Default_Challenge_Count = 15;
	// cd时长, 10分钟
	public static final int Default_CDTIME = 10 * 60;
	// 离线10分钟
	public static final long Offline_Cache_Members_Time = 10 * 60 * 1000;
	// 奖励名次(0表示无奖励)
	public static final int Default_RewardRank = 0;
}
