package newbee.morningGlory.mmorpg.operatActivities.awardContent;


/**
 * 奖励发送方式<br>
 */
public enum AwardSendType {
	/** 玩家手动领取 */
	PlayersReceiveManually(0),
	/** 邮件发送 */
	MailSend(1),

	//
	;

	private AwardSendType(int value) {
		this.value = value;
	}

	private int value;

	public int getValue() {
		return value;
	}

	public static AwardSendType get(int type) {
		for (AwardSendType t : values()) {
			if (t.getValue() == type)
				return t;
		}
		return null;
	}
}
