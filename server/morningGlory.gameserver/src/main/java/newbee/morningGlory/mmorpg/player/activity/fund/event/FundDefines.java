package newbee.morningGlory.mmorpg.player.activity.fund.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class FundDefines {
	public static final short C2G_Fund_ApplyVersionByType = MGEventDefines.Activity_Message_Begin + 75; // 请求基金类型版本号
	public static final short G2C_Fund_ReturnVersion = MGEventDefines.Activity_Message_Begin + 76; // 返回基金类型版本号
	public static final short C2G_Fund_FundGetRewardList = MGEventDefines.Activity_Message_Begin + 77; // 请求基金领奖列表
	public static final short G2C_Fund_FundGetRewardList = MGEventDefines.Activity_Message_Begin + 78; // 返回基金领奖列表
	public static final short C2G_Fund_BuyWhichFund = MGEventDefines.Activity_Message_Begin + 79; // 请求购买哪个基金
	public static final short G2C_Fund_BuyWhichFund = MGEventDefines.Activity_Message_Begin + 80; // 购买结果
	public static final short C2G_Fund_GetReward = MGEventDefines.Activity_Message_Begin + 81; // 请求领取基金奖励
	public static final short G2C_Fund_GetReward = MGEventDefines.Activity_Message_Begin + 82;// 领取结果
	public static final short C2G_Fund_IsReceive = MGEventDefines.Activity_Message_Begin + 86;// 请求玩家基金领取状态
	public static final short G2C_Fund_IsReceive = MGEventDefines.Activity_Message_Begin + 87;// 返回玩家基金领取状态
	
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Fund_ApplyVersionByType, C2G_Fund_ApplyVersionByType.class);
		MessageFactory.addMessage(G2C_Fund_ReturnVersion, G2C_Fund_ReturnVersion.class);
		MessageFactory.addMessage(C2G_Fund_FundGetRewardList, C2G_Fund_FundGetRewardList.class);
		MessageFactory.addMessage(G2C_Fund_FundGetRewardList, G2C_Fund_FundGetRewardList.class);
		MessageFactory.addMessage(C2G_Fund_BuyWhichFund, C2G_Fund_BuyWhichFund.class);
		MessageFactory.addMessage(G2C_Fund_BuyWhichFund, G2C_Fund_BuyWhichFund.class);
		MessageFactory.addMessage(C2G_Fund_GetReward, C2G_Fund_GetReward.class);
		MessageFactory.addMessage(G2C_Fund_GetReward, G2C_Fund_GetReward.class);
		MessageFactory.addMessage(C2G_Fund_IsReceive, C2G_Fund_IsReceive.class);
		MessageFactory.addMessage(G2C_Fund_IsReceive, G2C_Fund_IsReceive.class);
	}
}
