package newbee.morningGlory.mmorpg.vip.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-11 下午5:31:01
 * @version 1.0
 */
public final class VipEventDefines {
	
	//VIP消息
	
	public static final short C2G_Vip_Message = MGEventDefines.Vip_Message_Begin + 1;
	public static final short G2C_Vip_Message = MGEventDefines.Vip_Message_Begin + 2;
	public static final short C2G_Vip_RewardList = MGEventDefines.Vip_Message_Begin + 3;
	public static final short G2C_Vip_RewardList = MGEventDefines.Vip_Message_Begin + 4;
	public static final short C2G_Vip_GetReward = MGEventDefines.Vip_Message_Begin + 5;
	public static final short G2C_Vip_GetReward = MGEventDefines.Vip_Message_Begin + 6;
	//vip抽奖
	public static final short C2G_Vip_OpenLottery = MGEventDefines.Vip_Message_Begin + 7;
	public static final short G2C_Vip_OpenLottery = MGEventDefines.Vip_Message_Begin + 8;
	
	public static final short C2G_Vip_Lottery = MGEventDefines.Vip_Message_Begin + 9;
	public static final short G2C_Vip_Lottery = MGEventDefines.Vip_Message_Begin + 10;
	public static final short G2C_Vip_LotteryMsg = MGEventDefines.Vip_Message_Begin + 11;
	
	public static final short G2C_Vip_SendWing = MGEventDefines.Vip_Message_Begin + 12;
	public static final short C2G_Vip_GetWing = MGEventDefines.Vip_Message_Begin + 13;
								
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Vip_Message, C2G_Vip_Message.class);	
		MessageFactory.addMessage(G2C_Vip_Message, G2C_Vip_Message.class);	
		MessageFactory.addMessage(C2G_Vip_RewardList, C2G_Vip_RewardList.class);	
		MessageFactory.addMessage(G2C_Vip_RewardList, G2C_Vip_RewardList.class);	
		MessageFactory.addMessage(C2G_Vip_GetReward, C2G_Vip_GetReward.class);	
		MessageFactory.addMessage(G2C_Vip_GetReward, G2C_Vip_GetReward.class);	
		
		MessageFactory.addMessage(C2G_Vip_OpenLottery, C2G_Vip_OpenLottery.class);	
		MessageFactory.addMessage(G2C_Vip_OpenLottery, G2C_Vip_OpenLottery.class);	
		MessageFactory.addMessage(C2G_Vip_Lottery, C2G_Vip_Lottery.class);	
		MessageFactory.addMessage(G2C_Vip_Lottery, G2C_Vip_Lottery.class);	
		MessageFactory.addMessage(G2C_Vip_LotteryMsg, G2C_Vip_LotteryMsg.class);	
		
		MessageFactory.addMessage(G2C_Vip_SendWing, G2C_Vip_SendWing.class);	
		MessageFactory.addMessage(C2G_Vip_GetWing, C2G_Vip_GetWing.class);	
	}
}
