package newbee.morningGlory.mmorpg.union;

public class MGUnionConstant {
	/** 会长 */
	public static final byte Chairman = 1;
	/** 副会长 */
	public static final byte Vice_Chairman = 2;
	/** 帮众 */
	public static final byte Common = 3;
	/** 非公会成员 */
	public static final byte NotUnionMember = -1;
	/** 帮会成员在线 */
	public static final byte Online = 1;
	/** 帮会成员不在线 */
	public static final byte NotOnline = 0;
	/** 王城公会 */
	public static final byte Is_KingCity = 1;
	/** 非王城公会 */
	public static final byte Not_KingCity = 0;
	
	/** 被邀请上限 */
	public static final int Invited_Number_UpperLimit = 5;
	/** 申请上限 */
	public static final int Apply_Number_UpperLimit = 100;
	/** 帮会列表分页显示个数 */
	public static final int Union_List_CountPerPage = 21;
	/** 帮会成员上限 */
	public static final int Member_Number_UpperLimit = 50;
	/** 帮会成员列表分页显示个数 */
	public static final byte Member_List_CountPerPage = 21;
}
