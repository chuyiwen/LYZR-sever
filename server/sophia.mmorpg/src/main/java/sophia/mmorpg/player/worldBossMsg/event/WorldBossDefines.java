package sophia.mmorpg.player.worldBossMsg.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class WorldBossDefines {
	
	// 请求boss刷新时间列表
	public static final short C2G_Boss_List = MMORPGEventDefines.WorldBoss_Message_Begin + 1;
	// 响应boss刷新时间列表
	public static final short G2C_Boss_List = MMORPGEventDefines.WorldBoss_Message_Begin + 2;
	
	public static final short G2C_Boss_Refresh = MMORPGEventDefines.WorldBoss_Message_Begin + 3;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Boss_List, C2G_Boss_List.class);
		MessageFactory.addMessage(G2C_Boss_List, G2C_Boss_List.class);
		MessageFactory.addMessage(G2C_Boss_Refresh, G2C_Boss_Refresh.class);
	}
}
