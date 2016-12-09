package sophia.mmorpg.player.friend.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class FriendEventDefines {

	public final static short C2G_GetPlayerList = MMORPGEventDefines.Friedn_Message_Begin + 1;
	public final static short G2C_GetPlayerList = MMORPGEventDefines.Friedn_Message_Begin + 2;
	
	public final static short C2G_AddOnePlayer = MMORPGEventDefines.Friedn_Message_Begin + 3;
	public final static short G2C_AddOnePlayer = MMORPGEventDefines.Friedn_Message_Begin + 4;
	
	public final static short C2G_DeleteOnePlayer = MMORPGEventDefines.Friedn_Message_Begin + 5;
	public final static short G2C_DeleteOnePlayer = MMORPGEventDefines.Friedn_Message_Begin + 6;
	
	public final static short G2C_Update_OnlinePlayer = MMORPGEventDefines.Friedn_Message_Begin + 7;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_AddOnePlayer, C2G_AddOnePlayer.class);
		MessageFactory.addMessage(G2C_AddOnePlayer, G2C_AddOnePlayer.class);
		
		MessageFactory.addMessage(C2G_GetPlayerList, C2G_GetPlayerList.class);
		MessageFactory.addMessage(G2C_GetPlayerList, G2C_GetPlayerList.class);
		
		MessageFactory.addMessage(C2G_DeleteOnePlayer, C2G_DeleteOnePlayer.class);
		MessageFactory.addMessage(G2C_DeleteOnePlayer, G2C_DeleteOnePlayer.class);
		
		MessageFactory.addMessage(G2C_Update_OnlinePlayer, G2C_Update_OnlinePlayer.class);
	}
}
