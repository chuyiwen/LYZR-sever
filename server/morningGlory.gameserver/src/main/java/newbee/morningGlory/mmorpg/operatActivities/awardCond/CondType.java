package newbee.morningGlory.mmorpg.operatActivities.awardCond;


/**
 * 奖励条件类型<br>
 */
public enum CondType {
	/** 复杂结构 */
	ComplexData(0),
	//
	;
	private CondType(int value) {
		this.value = value;
	}

	private int value;

	public int getValue() {
		return value;
	}

	public static CondType get(int type) {
		for (CondType t : values()) {
			if (t.getValue() == type)
				return t;
		}
		return null;
	}
}
