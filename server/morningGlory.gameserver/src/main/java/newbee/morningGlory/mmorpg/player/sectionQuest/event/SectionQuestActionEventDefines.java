package newbee.morningGlory.mmorpg.player.sectionQuest.event;

import newbee.morningGlory.event.MGEventDefines;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.mount.C2G_Mount_GetMountQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.mount.G2C_Mount_GetMountQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.mount.G2C_Mount_MountQuestResp;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.C2G_Talisman_GetQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.G2C_Talisman_GetQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.G2C_Talisman_QuestAccept;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.G2C_Talisman_QuestResp;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.C2G_Wing_GetWingQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.G2C_Wing_GetWingQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.G2C_Wing_WingQuestAccept;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.G2C_Wing_WingQuestResp;
import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class SectionQuestActionEventDefines {
		// 切面任务入口
		public static final short C2G_SectionQuest_Begin = MGEventDefines.Wing_Message_Begin + 50;
		
		/**
		 * 翅膀任务
		 */
		// 翅膀任务返回
		public static final short G2C_Wing_WingQuestResp = MGEventDefines.Wing_Message_Begin + 5;
		// 翅膀任务奖励领取
		public static final short C2G_Wing_GetWingQuestReward = MGEventDefines.Wing_Message_Begin + 6;
		// 领取翅膀任务奖励返回
		public static final short G2C_Wing_GetWingQuestReward = MGEventDefines.Wing_Message_Begin + 7;
		
		public static final short G2C_Wing_WingQuestAccept = MGEventDefines.Wing_Message_Begin + 8;
		
		/**
		 * 法宝任务
		 */
		public static final short G2C_Talisman_QuestAccept = MGEventDefines.Talisman_Message_Begin + 5;
		
		public static final short G2C_Talisman_QuestResp = MGEventDefines.Talisman_Message_Begin + 6;
		
		public static final short C2G_Talisman_GetQuestReward = MGEventDefines.Talisman_Message_Begin + 7;
		
		public static final short G2C_Talisman_GetQuestReward = MGEventDefines.Talisman_Message_Begin + 8;
		
		/**
		 * 坐骑任务
		 */
		public static final short G2C_Mount_MountQuestResp = MMORPGEventDefines.Mount_Message_Begin+5;
		
		public static final short C2G_Mount_GetMountQuestReward = MMORPGEventDefines.Mount_Message_Begin+6;
		
		public static final short G2C_Mount_GetMountQuestReward = MMORPGEventDefines.Mount_Message_Begin+7;
		
		
		public static void registerActionEvents() {
			MessageFactory.addMessage(C2G_SectionQuest_Begin, C2G_SectionQuest_Begin.class);
			MessageFactory.addMessage(G2C_Wing_WingQuestResp, G2C_Wing_WingQuestResp.class);
			MessageFactory.addMessage(C2G_Wing_GetWingQuestReward, C2G_Wing_GetWingQuestReward.class);
			MessageFactory.addMessage(G2C_Wing_GetWingQuestReward, G2C_Wing_GetWingQuestReward.class);
			MessageFactory.addMessage(G2C_Wing_WingQuestAccept, G2C_Wing_WingQuestAccept.class);
			
			MessageFactory.addMessage(G2C_Talisman_QuestAccept, G2C_Talisman_QuestAccept.class);
			MessageFactory.addMessage(G2C_Talisman_QuestResp, G2C_Talisman_QuestResp.class);
			MessageFactory.addMessage(C2G_Talisman_GetQuestReward, C2G_Talisman_GetQuestReward.class);
			MessageFactory.addMessage(G2C_Talisman_GetQuestReward, G2C_Talisman_GetQuestReward.class);
			
			MessageFactory.addMessage(G2C_Mount_MountQuestResp, G2C_Mount_MountQuestResp.class);
			MessageFactory.addMessage(C2G_Mount_GetMountQuestReward, C2G_Mount_GetMountQuestReward.class);
			MessageFactory.addMessage(G2C_Mount_GetMountQuestReward, G2C_Mount_GetMountQuestReward.class);
		}
}
