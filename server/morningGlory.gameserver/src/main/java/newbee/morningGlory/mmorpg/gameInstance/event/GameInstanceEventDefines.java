package newbee.morningGlory.mmorpg.gameInstance.event;

import newbee.morningGlory.event.MGEventDefines;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.C2G_Reward_GameInstanceQuest;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.C2G_Show_GameInstanceQuestReward;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.G2C_Instance_QuestAccepted;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.G2C_Instance_QuestFinish;
import newbee.morningGlory.mmorpg.player.gameInstance.quest.event.G2C_Instance_QuestUpdate;
import sophia.foundation.communication.core.MessageFactory;

public class GameInstanceEventDefines {
	public static final short C2G_GameInstanceList = MGEventDefines.Game_Instance_Message_Begin + 1;
	public static final short C2G_GameInstanceEnter = MGEventDefines.Game_Instance_Message_Begin + 2;
	public static final short C2G_GameInstanceLeave = MGEventDefines.Game_Instance_Message_Begin + 3;
	public static final short C2G_GameInstanceEnterNextLayer = MGEventDefines.Game_Instance_Message_Begin + 4;
	// 任务列表响应
	public static final short G2C_Instance_QuestAccepted = MGEventDefines.Game_Instance_Message_Begin + 5;
	// 接收任务请求
	public static final short G2C_Instance_QuestUpdate = MGEventDefines.Game_Instance_Message_Begin + 6;
	// 接收任务响应
	public static final short G2C_Instance_QuestFinish = MGEventDefines.Game_Instance_Message_Begin + 7;
	// 副本
	public static final short G2C_Instance_QuestReward = MGEventDefines.Game_Instance_Message_Begin + 8;
	
	public static final short G2C_Instance_LayerFinish = MGEventDefines.Game_Instance_Message_Begin + 9;

	public static final short G2C_GameInstanceList = MGEventDefines.Game_Instance_Message_Begin + 51;
	
	public static final short G2C_GameInstanceSceneFinish = MGEventDefines.Game_Instance_Message_Begin + 52;
	//离开副本
	public static final short G2C_GameInstanceLeave = MGEventDefines.Game_Instance_Message_Begin + 53;

	public static final short C2G_Reward_GameInstanceQuest = MGEventDefines.Game_Instance_Message_Begin + 54;
	
	public static final short C2G_Show_GameInstanceQuestReward = MGEventDefines.Game_Instance_Message_Begin + 55;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_GameInstanceList, C2G_GameInstanceList.class);
		MessageFactory.addMessage(C2G_GameInstanceEnter, C2G_GameInstanceEnter.class);
		MessageFactory.addMessage(C2G_GameInstanceLeave, C2G_GameInstanceLeave.class);
		MessageFactory.addMessage(C2G_GameInstanceEnterNextLayer, C2G_GameInstanceEnterNextLayer.class);

		MessageFactory.addMessage(G2C_Instance_QuestAccepted, G2C_Instance_QuestAccepted.class);
		MessageFactory.addMessage(G2C_Instance_LayerFinish, G2C_Instance_LayerFinish.class);
		MessageFactory.addMessage(G2C_Instance_QuestUpdate, G2C_Instance_QuestUpdate.class);
		MessageFactory.addMessage(G2C_Instance_QuestFinish, G2C_Instance_QuestFinish.class);
		MessageFactory.addMessage(G2C_Instance_QuestReward, G2C_Instance_QuestReward.class);

		MessageFactory.addMessage(G2C_GameInstanceList, G2C_GameInstanceList.class);
		MessageFactory.addMessage(G2C_GameInstanceSceneFinish, G2C_GameInstanceSceneFinish.class);
		MessageFactory.addMessage(G2C_GameInstanceLeave, G2C_GameInstanceLeave.class);
		
		MessageFactory.addMessage(C2G_Reward_GameInstanceQuest, C2G_Reward_GameInstanceQuest.class);
		MessageFactory.addMessage(C2G_Show_GameInstanceQuestReward, C2G_Show_GameInstanceQuestReward.class);
	}
}
