package sophia.mmorpg.player.chat.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class ChatEventDefines {

	public final static short C2G_Chat_World = MMORPGEventDefines.Chat_Message_Begin + 1;
	public final static short C2G_Chat_Sociaty = MMORPGEventDefines.Chat_Message_Begin + 2;
	public final static short C2G_Chat_Private = MMORPGEventDefines.Chat_Message_Begin + 3;
	public final static short C2G_Chat_Get_ReceiverId = MMORPGEventDefines.Chat_Message_Begin + 5;
	public final static short C2G_Chat_Current_Scene = MMORPGEventDefines.Chat_Message_Begin + 6;
	public final static short C2G_Chat_Bugle = MMORPGEventDefines.Chat_Message_Begin + 7;
	//public final static short C2G_Is_Player_Online = MMORPGEventDefines.Chat_Message_Begin + 9;
	public final static short G2C_Is_Player_Online = MMORPGEventDefines.Chat_Message_Begin + 10;
//	public final static short C2G_AddOnePlayer = MMORPGEventDefines.Chat_Message_Begin + 11;
//	public final static short G2C_AddOnePlayer = MMORPGEventDefines.Chat_Message_Begin + 12;
	
	public final static short G2C_Chat_World = MMORPGEventDefines.Chat_Message_Begin + 51;
	public final static short G2C_Chat_Sociaty = MMORPGEventDefines.Chat_Message_Begin + 52;
	public final static short G2C_Chat_Private = MMORPGEventDefines.Chat_Message_Begin + 53;
	public final static short G2C_Chat_System = MMORPGEventDefines.Chat_Message_Begin + 54;
	public final static short G2C_Chat_Get_ReceiverId = MMORPGEventDefines.Chat_Message_Begin + 55;
	public final static short G2C_Chat_Current_Scene = MMORPGEventDefines.Chat_Message_Begin + 56;

	public final static short G2C_System_Prompt = MMORPGEventDefines.Chat_Message_Begin + 57;
	public final static short G2C_Chat_Bugle = MMORPGEventDefines.Chat_Message_Begin + 58;
	
//	public final static short C2G_GetPlayerList = MMORPGEventDefines.Chat_Message_Begin + 13;
//	public final static short G2C_GetPlayerList = MMORPGEventDefines.Chat_Message_Begin + 14;
	
//	public final static short C2G_DeleteOnePlayer = MMORPGEventDefines.Chat_Message_Begin + 15;
//	public final static short G2C_DeleteOnePlayer = MMORPGEventDefines.Chat_Message_Begin + 16;
//	public final static short G2C_Update_OnlinePlayer = MMORPGEventDefines.Chat_Message_Begin + 17;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Chat_World, C2G_Chat_World.class);
		MessageFactory.addMessage(C2G_Chat_Private, C2G_Chat_Private.class);
		MessageFactory.addMessage(C2G_Chat_Get_ReceiverId, C2G_Chat_Get_ReceiverId.class);
		MessageFactory.addMessage(C2G_Chat_Current_Scene, C2G_Chat_Current_Scene.class);
		MessageFactory.addMessage(C2G_Chat_Bugle, C2G_Chat_Bugle.class);
		
		MessageFactory.addMessage(G2C_Chat_World, G2C_Chat_World.class);
		MessageFactory.addMessage(G2C_Chat_Private, G2C_Chat_Private.class);
		MessageFactory.addMessage(G2C_Chat_System, G2C_Chat_System.class);
		MessageFactory.addMessage(G2C_Chat_Get_ReceiverId, G2C_Chat_Get_ReceiverId.class);
		MessageFactory.addMessage(G2C_Chat_Current_Scene, G2C_Chat_Current_Scene.class);
		MessageFactory.addMessage(G2C_System_Prompt, G2C_System_Prompt.class);
		MessageFactory.addMessage(G2C_Chat_Bugle, G2C_Chat_Bugle.class);
		
		//MessageFactory.addMessage(C2G_Is_Player_Online, C2G_Is_Player_Online.class);
		MessageFactory.addMessage(G2C_Is_Player_Online, G2C_Is_Player_Online.class);
	}
}
