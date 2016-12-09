package sophia.mmorpg.player.quest.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;


public class QuestActionEventDefines {
	// 获取任务系统的消息请求
	public static final short C2G_QST_GetQuestList = MMORPGEventDefines.QST_Message_Begin + 1;
	// 任务列表响应
	public static final short G2C_QST_QuestAcceptedList = MMORPGEventDefines.QST_Message_Begin + 2;
	// 接收任务请求
	public static final short G2C_QST_QuestVisibleList = MMORPGEventDefines.QST_Message_Begin + 3;
	// 接收任务响应
	public static final short C2G_QST_QuestAccept = MMORPGEventDefines.QST_Message_Begin + 4;
	// 提交任务请求
	public static final short C2G_QST_QuestSubmit = MMORPGEventDefines.QST_Message_Begin + 5;
	// 更新任务状态
	public static final short G2C_QST_StateUpdate = MMORPGEventDefines.QST_Message_Begin + 6;
	// 任务的进度更新消息
	public static final short G2C_QST_QuestUpdate = MMORPGEventDefines.QST_Message_Begin + 7;
	// 任务的进度更新消息
	public static final short C2G_QST_QuestInstanceTrans = MMORPGEventDefines.QST_Message_Begin + 8;
	
	public static final short C2G_COM_ActionToSucceed = MMORPGEventDefines.QST_Message_Begin + 9;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_QST_GetQuestList, C2G_QST_GetQuestList.class);
		MessageFactory.addMessage(G2C_QST_QuestAcceptedList, G2C_QST_QuestAcceptedList.class);
		MessageFactory.addMessage(G2C_QST_QuestVisibleList, G2C_QST_QuestVisibleList.class);
		MessageFactory.addMessage(C2G_QST_QuestAccept, C2G_QST_QuestAccept.class);
		MessageFactory.addMessage(C2G_QST_QuestSubmit, C2G_QST_QuestSubmit.class);
		MessageFactory.addMessage(G2C_QST_StateUpdate, G2C_QST_StateUpdate.class);
		MessageFactory.addMessage(G2C_QST_QuestUpdate, G2C_QST_QuestUpdate.class);
		MessageFactory.addMessage(C2G_QST_QuestInstanceTrans, C2G_QST_QuestInstanceTrans.class);
		MessageFactory.addMessage(C2G_COM_ActionToSucceed, C2G_COM_ActionToSucceed.class);
	}
}
