package newbee.morningGlory.mmorpg.player.union;

public class UnionMacro {
	public static final int Default_CreateUnion_Level = 10;
	public static final int Default_CreateUnion_gold = 100000;
	public static final int Default_CreateUnion_item = 10;
	public static final int Default_CreateUnion_Name_MinLen = 4;
	public static final int Default_CreateUnion_Name_MaxLen = 12;
	
	// 默认副会长个数
	public static final int Default_ViceChairman_Count = 2;
	public static final String Union_Card_RefId = "item_gonghuiling";
	
	// 玩家申请公会处理
	public static final byte Agree = 1;
	public static final byte Decline = 0;
	// 公会操作类型
	public static final byte Add_Member = 1;
	public static final byte Remove_Member = 2;
	public static final byte Upgrade_Member = 3;
	
	public static final byte Create_Union = 4;
	public static final byte BeKickedOut = 5;
	public static final byte BeDissolved = 6;
	public static final byte Dissolve = 7;
	// 公会被邀请玩家回复类型
	public static final byte Reply_NoReply = 11;
	public static final byte Reply_Agree = 12;
	public static final byte Reply_Decline = 13;

	public static final byte Type_System = 1;
	public static final byte Type_Personal = 2;

	public static final byte Type_SelfUnionList = 1;
	public static final byte Type_AllUnionList = 0;
	
	public static final byte Type_UI_Refresh = 1;
	public static final byte Type_UI_Keep = 2;
	
	// 申请公会列表类型
	public static final byte Type_All_UnionList = 1;
	public static final byte Type_Uncertain_UnionList = 2;
	
	// 创建公会默认公告
	public static final String Default_Message = "烈焰齐心，铁血精英";
	
	// 公会自动通过标记
	public static final byte Auto_Agree = 1;
	
}
