package newbee.morningGlory.mmorpg.player.offLineAI.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class OffLineAIEventDefines {
	
	public static final short C2G_ViewOffLineAIReward = MGEventDefines.OffLineAISeting_Begin + 1;// 查看离线奖励
	public static final short C2G_DrawOffLineAIReward = MGEventDefines.OffLineAISeting_Begin + 2;// 领取离线奖励
	public static final short C2G_OffLineAISeting = MGEventDefines.OffLineAISeting_Begin + 3;// 离线挂机AI设置
	
	public static final short G2C_ViewOffLineAIReward = MGEventDefines.OffLineAISeting_Begin + 50;// 查看离线奖励
	public static final short G2C_DrawOffLineAIReward = MGEventDefines.OffLineAISeting_Begin + 51;// 领取离线奖励
	
	
	
	
	public static void registerActionEvents() {
		
		MessageFactory.addMessage(C2G_ViewOffLineAIReward, C2G_ViewOffLineAIReward.class);
		MessageFactory.addMessage(C2G_DrawOffLineAIReward, C2G_DrawOffLineAIReward.class);
		MessageFactory.addMessage(C2G_OffLineAISeting, C2G_OffLineAISeting.class);
		
		MessageFactory.addMessage(G2C_ViewOffLineAIReward, G2C_ViewOffLineAIReward.class);
		MessageFactory.addMessage(G2C_DrawOffLineAIReward, G2C_DrawOffLineAIReward.class);
	}

}
