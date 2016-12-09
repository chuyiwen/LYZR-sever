package newbee.morningGlory.http.actionEvent;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class GameServerGmEventDefines {
	// 通知玩家即将关闭服务器
	public static final short G2C_GameServerShutDown = MGEventDefines.Game_Serve_Message_Begin + 1;

	public static void registerActionEvents() {
		MessageFactory.addMessage(G2C_GameServerShutDown, G2C_GameServerShutDown.class);
	}
}
