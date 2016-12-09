package newbee.morningGlory.mmorpg.player.achievement.actionEvent;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class AchievementEventDefines {
	// 成就列表
	public static final short C2G_Achievement_List = MGEventDefines.Achievement_Message_Begin + 1;

	// 返回成就列表
	public static final short G2C_Achievement_List = MGEventDefines.Achievement_Message_Begin + 2;

	// 领取奖励
	public static final short C2G_Achievement_GetReward = MGEventDefines.Achievement_Message_Begin + 3;

	// 领取奖励返回
	public static final short G2C_Achievement_GetReward = MGEventDefines.Achievement_Message_Begin + 4;

	// 获得的成
	public static final short G2C_Achievement_Get = MGEventDefines.Achievement_Message_Begin + 5;

	// 兑换勋章
	public static final short C2G_Achievement_ExchangeMedal = MGEventDefines.Achievement_Message_Begin + 6;

	// 兑换勋章返回
	public static final short G2C_Achievement_ExchangeMedal = MGEventDefines.Achievement_Message_Begin + 7;

	// 勋章升级
	public static final short C2G_Achievement_LevlUpMedal = MGEventDefines.Achievement_Message_Begin + 8;

	// 勋章升级返回
	public static final short G2C_Achievement_LevlUpMedal = MGEventDefines.Achievement_Message_Begin + 9;

	// 购买令牌返回
	public static final short C2G_Achievement_GetAllReward = MGEventDefines.Achievement_Message_Begin + 10;

	// 购买令牌返回
	public static final short G2C_Achievement_GetAllReward = MGEventDefines.Achievement_Message_Begin + 11;
	public static void registerActionEvents() {
		// TODO Auto-generated method stub
		MessageFactory.addMessage(C2G_Achievement_List, C2G_Achievement_List.class);
		MessageFactory.addMessage(G2C_Achievement_List, G2C_Achievement_List.class);
		MessageFactory.addMessage(C2G_Achievement_GetReward, C2G_Achievement_GetReward.class);
		MessageFactory.addMessage(G2C_Achievement_GetReward, G2C_Achievement_GetReward.class);
		MessageFactory.addMessage(C2G_Achievement_ExchangeMedal, C2G_Achievement_ExchangeMedal.class);
		MessageFactory.addMessage(G2C_Achievement_ExchangeMedal, G2C_Achievement_ExchangeMedal.class);
		MessageFactory.addMessage(C2G_Achievement_LevlUpMedal, C2G_Achievement_LevlUpMedal.class);
		MessageFactory.addMessage(G2C_Achievement_LevlUpMedal, G2C_Achievement_LevlUpMedal.class);
		MessageFactory.addMessage(G2C_Achievement_Get, G2C_Achievement_Get.class);
		MessageFactory.addMessage(C2G_Achievement_GetAllReward, C2G_Achievement_GetAllReward.class);
	}
}
