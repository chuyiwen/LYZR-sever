package newbee.morningGlory.mmorpg.player.activity.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class MGActivityEventDefines {
	public static final short C2G_SignIn_List = MGEventDefines.Activity_Message_Begin + 1;// 签到列表
	public static final short G2C_SignIn_List = MGEventDefines.Activity_Message_Begin + 2;// 签到列表返回
	public static final short C2G_SignIn = MGEventDefines.Activity_Message_Begin + 3;// 签到(正常签到和补签)
	public static final short G2C_SignIn = MGEventDefines.Activity_Message_Begin + 4;
	public static final short C2G_getReward = MGEventDefines.Activity_Message_Begin + 5;// 领取奖励申请
	public static final short G2C_getReward = MGEventDefines.Activity_Message_Begin + 6;// 领取奖励返回
	public static final short G2C_CangetReward = MGEventDefines.Activity_Message_Begin + 7;// 可以领取签到累计奖励通知

	public static final short G2C_OT_ShowOnLineTimer = MGEventDefines.Activity_Message_Begin + 8;// 在线时长累计
	public static final short G2C_OT_ShowDailyOnLineTimer = MGEventDefines.Activity_Message_Begin + 13;
	public static final short G2C_OT_ResetDailyOnLineTimer = MGEventDefines.Activity_Message_Begin + 14;

	// 进阶
	public static final short C2G_Advanced_List = MGEventDefines.Activity_Message_Begin + 15;
	public static final short G2C_Advanced_List = MGEventDefines.Activity_Message_Begin + 16;
	public static final short C2G_Advanced_GetReward = MGEventDefines.Activity_Message_Begin + 17;// 领取奖励申请

	// 升级
	public static final short C2G_LevelUp_List = MGEventDefines.Activity_Message_Begin + 18;
	public static final short G2C_LevelUp_List = MGEventDefines.Activity_Message_Begin + 19;
	public static final short C2G_LevelUp_GetReward = MGEventDefines.Activity_Message_Begin + 20;
	public static final short G2C_LevelUp_ActivityOver = MGEventDefines.Activity_Message_Begin + 21;
	
	// 在线时长
	public static final short C2G_OT_ShowOnLineTimer = MGEventDefines.Activity_Message_Begin + 90;
	
	public static final short C2G_Activity_CanRecieve = MGEventDefines.Activity_Message_Begin + 95;
	public static final short G2C_Activity_CanRecieve = MGEventDefines.Activity_Message_Begin + 96;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_SignIn_List, C2G_SignIn_List.class);
		MessageFactory.addMessage(G2C_SignIn_List, G2C_SignIn_List.class);

		MessageFactory.addMessage(C2G_SignIn, C2G_SignIn.class);
		MessageFactory.addMessage(G2C_SignIn, G2C_SignIn.class);

		MessageFactory.addMessage(C2G_getReward, C2G_getReward.class);
		MessageFactory.addMessage(G2C_getReward, G2C_getReward.class);

		MessageFactory.addMessage(G2C_CangetReward, G2C_CangetReward.class);
		MessageFactory.addMessage(G2C_OT_ShowOnLineTimer, G2C_OT_ShowOnLineTimer.class);
//		MessageFactory.addMessage(G2C_OT_ShowDailyOnLineTimer, G2C_OT_ShowDailyOnLineTimer.class);
//		MessageFactory.addMessage(G2C_OT_ResetDailyOnLineTimer, G2C_OT_ResetDailyOnLineTimer.class);

		MessageFactory.addMessage(C2G_Advanced_List, C2G_Advanced_List.class);
		MessageFactory.addMessage(G2C_Advanced_List, G2C_Advanced_List.class);
		MessageFactory.addMessage(C2G_Advanced_GetReward, C2G_Advanced_GetReward.class);

		MessageFactory.addMessage(C2G_LevelUp_List, C2G_LevelUp_List.class);
		MessageFactory.addMessage(G2C_LevelUp_List, G2C_LevelUp_List.class);
		MessageFactory.addMessage(C2G_LevelUp_GetReward, C2G_LevelUp_GetReward.class);
		MessageFactory.addMessage(G2C_LevelUp_ActivityOver, G2C_LevelUp_ActivityOver.class);
		
		MessageFactory.addMessage(C2G_OT_ShowOnLineTimer, C2G_OT_ShowOnLineTimer.class);
		
		MessageFactory.addMessage(C2G_Activity_CanRecieve, C2G_Activity_CanRecieve.class);
		MessageFactory.addMessage(G2C_Activity_CanRecieve, G2C_Activity_CanRecieve.class);
	}
}
