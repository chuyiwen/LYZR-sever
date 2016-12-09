package newbee.morningGlory.mmorpg.player.wing.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class WingEventDefines {
		// 获取当前翅膀请求
		public static final short C2G_Wing_RequestNowWing = MGEventDefines.Wing_Message_Begin + 1;
		// 获取当前翅膀响应
		public static final short G2C_Wing_RequestNowWing = MGEventDefines.Wing_Message_Begin + 2;
		// 翅膀升级请求
		public static final short C2G_Wing_WingLevelUp = MGEventDefines.Wing_Message_Begin + 3;
		// 翅膀升级响应
		public static final short G2C_Wing_WingLevelUp = MGEventDefines.Wing_Message_Begin + 4;

		public static void registerActionEvents() {
			MessageFactory.addMessage(C2G_Wing_RequestNowWing, C2G_Wing_RequestNowWing.class);
			MessageFactory.addMessage(G2C_Wing_RequestNowWing, G2C_Wing_RequestNowWing.class);
			MessageFactory.addMessage(C2G_Wing_WingLevelUp, C2G_Wing_WingLevelUp.class);
			MessageFactory.addMessage(G2C_Wing_WingLevelUp, G2C_Wing_WingLevelUp.class);
		}
}
