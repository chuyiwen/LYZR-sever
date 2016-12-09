package newbee.morningGlory.mmorpg.operatActivities;

public enum OperatActivityGroup {
	/** 开服活动 */
	OpenServerActivity(1),
	/** 充值消费活动 */
	RechargeConsumptionActivity(2),
	/** 每日登陆 */
	DailyLoginActivity(3),

	/** 杂项活动 */
	MiscsActivity(888),
	//
	;

	private OperatActivityGroup(int value) {
		this.value = value;
	}

	private int value;

	public int getValue() {
		return value;
	}

	public static OperatActivityGroup get(int group) {
		for (OperatActivityGroup g : values()) {
			if (g.getValue() == group)
				return g;
		}
		return null;
	}

}
