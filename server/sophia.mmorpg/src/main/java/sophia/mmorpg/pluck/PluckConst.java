package sophia.mmorpg.pluck;

public class PluckConst {
	// --------------------
	/** 采集模式-独占性采集 */
	public static final byte Pluck_Module_Exclusive = 1;
	/** 采集模式-非独占性采集 */
	public static final byte Pluck_Module_Non_Exclusive = 2;
	// -------------------
	/** 采集数量归属-玩家获得 */
	public static final byte Pluck_Num_Adscription_Player = 1;
	/** 采集数量归属-队伍所有人获得 */
	public static final byte Pluck_Num_Adscription_Team = 2;
	// ------------------
	/** 采集表现-被攻击中断 */
	public static final byte Pluck_Behavior_Attacked_Interrupt = 1;
	/** 采集表现-被攻击不中断 */
	public static final byte Pluck_Behavior_Attacked_Non_Interrupt = 2;
	
	/**单人采集*/
	public static final byte SingleShareType = 1;
	
	/**多人共享采集*/
	public static final byte MultiShareType = 2;
	
	/**正常产出*/
	public static final byte NormalOutput = 1;
	
	/**掉落产出*/
	public static final byte LootOutput = 2;

}