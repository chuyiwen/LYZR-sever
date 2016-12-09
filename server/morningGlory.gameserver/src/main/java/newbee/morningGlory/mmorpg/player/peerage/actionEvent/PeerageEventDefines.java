package newbee.morningGlory.mmorpg.player.peerage.actionEvent;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class PeerageEventDefines {
	// 领取俸禄
	public static final short C2G_GetSalaryEvent = MGEventDefines.Peerage_Message_Begin + 1;

	// 领取俸禄返回
	public static final short G2C_GetSalaryEvent = MGEventDefines.Peerage_Message_Begin + 2;

	// 官爵升级
	public static final short C2G_UpGradeEvent = MGEventDefines.Peerage_Message_Begin + 3;

	// 官爵升级返回
	public static final short G2C_UpGradeEvent = MGEventDefines.Peerage_Message_Begin + 4;

	public static final short C2G_CanReward = MGEventDefines.Peerage_Message_Begin + 5;
	// 爵位领奖重置
	public static final short G2C_CanReward = MGEventDefines.Peerage_Message_Begin + 6;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_GetSalaryEvent, C2G_GetSalaryEvent.class);
		MessageFactory.addMessage(G2C_GetSalaryEvent, G2C_GetSalaryEvent.class);

		MessageFactory.addMessage(C2G_UpGradeEvent, C2G_UpGradeEvent.class);
		MessageFactory.addMessage(G2C_UpGradeEvent, G2C_UpGradeEvent.class);
		MessageFactory.addMessage(C2G_CanReward, C2G_CanReward.class);
		MessageFactory.addMessage(G2C_CanReward, G2C_CanReward.class);

	}
}
