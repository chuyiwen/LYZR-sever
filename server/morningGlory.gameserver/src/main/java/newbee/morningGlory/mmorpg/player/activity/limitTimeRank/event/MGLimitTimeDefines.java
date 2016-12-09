package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class MGLimitTimeDefines {
	// 限时冲榜
	public static final short G2C_LimitTimeRank_TimeOver = MGEventDefines.Activity_Message_Begin + 30;
	public static final short C2G_LimitTimeRank_List = MGEventDefines.Activity_Message_Begin + 31;
	public static final short G2C_LimitTimeRank_List = MGEventDefines.Activity_Message_Begin + 32;
	public static final short C2G_LimitTimeRank_GetReward = MGEventDefines.Activity_Message_Begin + 33;
	public static final short G2C_LimitTimeRank_GetReward = MGEventDefines.Activity_Message_Begin + 34;
	public static final short C2G_LimitTimeRank_Version = MGEventDefines.Activity_Message_Begin + 35;
	public static final short G2C_LimitTimeRank_Version = MGEventDefines.Activity_Message_Begin + 36;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_LimitTimeRank_List, C2G_LimitTimeRank_List.class);
		MessageFactory.addMessage(G2C_LimitTimeRank_List, G2C_LimitTimeRank_List.class);
		MessageFactory.addMessage(C2G_LimitTimeRank_GetReward, C2G_LimitTimeRank_GetReward.class);
		MessageFactory.addMessage(G2C_LimitTimeRank_GetReward, G2C_LimitTimeRank_GetReward.class);
		MessageFactory.addMessage(G2C_LimitTimeRank_TimeOver, G2C_LimitTimeRank_TimeOver.class);
		MessageFactory.addMessage(C2G_LimitTimeRank_Version, C2G_LimitTimeRank_Version.class);
		MessageFactory.addMessage(G2C_LimitTimeRank_Version, G2C_LimitTimeRank_Version.class);
	}
}
