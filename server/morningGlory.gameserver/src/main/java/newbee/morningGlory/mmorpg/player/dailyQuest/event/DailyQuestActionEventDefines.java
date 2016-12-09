package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class DailyQuestActionEventDefines {
		// 获取任务系统的消息请求
		public static final short C2G_QST_GetDailyQuestList = MMORPGEventDefines.QST_Message_Begin + 51;
		// 任务列表响应
		public static final short G2C_QST_DailyQuestAcceptedList = MMORPGEventDefines.QST_Message_Begin + 52;
		// 接收任务请求
		public static final short G2C_QST_DailyQuestVisibleList = MMORPGEventDefines.QST_Message_Begin + 53;
		// 接收任务响应
		public static final short C2G_QST_DailyQuestAccept = MMORPGEventDefines.QST_Message_Begin + 54;
		// 提交任务请求
		public static final short C2G_QST_DailyQuestSubmit = MMORPGEventDefines.QST_Message_Begin + 55;
		// 更新任务状态
		public static final short G2C_QST_DailyStateUpdate = MMORPGEventDefines.QST_Message_Begin + 56;
		// 任务的进度更新消息
		public static final short G2C_QST_DailyQuestUpdate = MMORPGEventDefines.QST_Message_Begin + 57;
		// 任务等级更新
		public static final short C2G_QST_DailyStartLevel = MMORPGEventDefines.QST_Message_Begin + 58;
		
		public static final short G2C_QST_DailyStartLevel = MMORPGEventDefines.QST_Message_Begin + 59;
		
		public static void registerActionEvents() {
			MessageFactory.addMessage(C2G_QST_GetDailyQuestList, C2G_QST_GetDailyQuestList.class);
			MessageFactory.addMessage(G2C_QST_DailyQuestAcceptedList, G2C_QST_DailyQuestAcceptedList.class);
			MessageFactory.addMessage(G2C_QST_DailyQuestVisibleList, G2C_QST_DailyQuestVisibleList.class);
			MessageFactory.addMessage(C2G_QST_DailyQuestAccept, C2G_QST_DailyQuestAccept.class);
			MessageFactory.addMessage(C2G_QST_DailyQuestSubmit, C2G_QST_DailyQuestSubmit.class);
			MessageFactory.addMessage(G2C_QST_DailyStateUpdate, G2C_QST_DailyStateUpdate.class);
			MessageFactory.addMessage(G2C_QST_DailyQuestUpdate, G2C_QST_DailyQuestUpdate.class);
			MessageFactory.addMessage(C2G_QST_DailyStartLevel, C2G_QST_DailyStartLevel.class);
			MessageFactory.addMessage(G2C_QST_DailyStartLevel, G2C_QST_DailyStartLevel.class);
		}
}
