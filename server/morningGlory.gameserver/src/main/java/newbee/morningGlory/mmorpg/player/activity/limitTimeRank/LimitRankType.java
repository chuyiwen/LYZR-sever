package newbee.morningGlory.mmorpg.player.activity.limitTimeRank;


public enum LimitRankType {
	// --------角色----------
	/** 角色战斗力 */
	PlayerFightPower((byte) 1),
	/** 角色等级 */
	PlayerLvl((byte) 2),
	/** 角色爵位 */
	PlayerMerit((byte) 4),
	/** 玩家翅膀 */
	PlayerWingLvl((byte) 5),
	/** 玩家坐骑 */
	MountLvl((byte) 6),
	/** 玩家法宝 */
	TalismanLvl((byte)7);

	LimitRankType(byte value) {
		this.value = value;
	}

	private byte value;

	public byte value() {
		return value;
	}

	public static LimitRankType get(byte type) {
		LimitRankType[] values = LimitRankType.values();
		for (LimitRankType st : values) {
			if (st.value() == type)
				return st;
		}
		return null;
	}
	
	public static final String getLimitRankName(byte value) {
		String limitRankName = "";
		switch (value) {
		case (byte)1:
			limitRankName = "限时战力冲榜"; 
			break;
			
		case (byte)2:
			limitRankName = "限时等级冲榜"; 
			break;
			
		case (byte)4:
			limitRankName = "限时爵位冲榜"; 
			break;
			
		case (byte)5:
			limitRankName = "限时翅膀冲榜"; 
			break;
			
		case (byte)6:
			limitRankName = "限时坐骑冲榜"; 
			break;
			
		case (byte)7:
			limitRankName = "限时法宝冲榜"; 
			break;

		default:
			break;
		}
		
		return limitRankName;
	}
}
