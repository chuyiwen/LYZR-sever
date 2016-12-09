package newbee.morningGlory.mmorpg.player.castleWar.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class CastleWarActionEventDefines {
	// 请求参加攻城战
	public static final short C2G_CastleWar_JoinWar = MGEventDefines.CastleWar_Message_Begin + 1;
	// 参加攻城战返回
	public static final short G2C_CastleWar_JoinWar = MGEventDefines.CastleWar_Message_Begin + 2;
	// 领取王城礼包请求
	public static final short C2G_CastleWar_GetGift = MGEventDefines.CastleWar_Message_Begin + 3;
	// 进入王城副本请求
	public static final short C2G_CastleWar_Instance = MGEventDefines.CastleWar_Message_Begin + 4;
	// 请求攻城公会列表
	public static final short C2G_CastleWar_FactionList = MGEventDefines.CastleWar_Message_Begin + 5;
	// 攻城公会列表返回
	public static final short G2C_CastleWar_FactionList = MGEventDefines.CastleWar_Message_Begin + 6;
	
	public static final short G2C_CastleWar_Enter = MGEventDefines.CastleWar_Message_Begin + 7;
	
	public static final short G2C_CastleWar_Exit = MGEventDefines.CastleWar_Message_Begin + 8;
	
	public static final short G2C_CastleWar_MonsterRefresh = MGEventDefines.CastleWar_Message_Begin + 9;
	
	public static final short G2C_CastleWar_PreStart = MGEventDefines.CastleWar_Message_Begin + 10;
	
	public static final short G2C_CastleWar_End = MGEventDefines.CastleWar_Message_Begin + 11;
	
	public static final short C2G_CastleWar_RequestTime = MGEventDefines.CastleWar_Message_Begin + 12;
	
	public static final short G2C_CastleWar_RequestTime = MGEventDefines.CastleWar_Message_Begin + 13;
	
	public static final short G2C_CastleWar_Start = MGEventDefines.CastleWar_Message_Begin + 14;
	
	public static final short C2G_CastleWar_OpenServer = MGEventDefines.CastleWar_Message_Begin + 15;

	public static final short G2C_CastleWar_OpenServer = MGEventDefines.CastleWar_Message_Begin + 16;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_CastleWar_JoinWar, C2G_CastleWar_JoinWar.class);
		MessageFactory.addMessage(G2C_CastleWar_JoinWar, G2C_CastleWar_JoinWar.class);
		MessageFactory.addMessage(C2G_CastleWar_GetGift, C2G_CastleWar_GetGift.class);
		MessageFactory.addMessage(C2G_CastleWar_Instance, C2G_CastleWar_Instance.class);
		MessageFactory.addMessage(C2G_CastleWar_FactionList, C2G_CastleWar_FactionList.class);
		MessageFactory.addMessage(G2C_CastleWar_FactionList, G2C_CastleWar_FactionList.class);
		MessageFactory.addMessage(G2C_CastleWar_Enter, G2C_CastleWar_Enter.class);
		MessageFactory.addMessage(G2C_CastleWar_Exit, G2C_CastleWar_Exit.class);
		MessageFactory.addMessage(G2C_CastleWar_MonsterRefresh, G2C_CastleWar_MonsterRefresh.class);
		MessageFactory.addMessage(G2C_CastleWar_PreStart, G2C_CastleWar_PreStart.class);
		MessageFactory.addMessage(G2C_CastleWar_End, G2C_CastleWar_End.class);
		MessageFactory.addMessage(C2G_CastleWar_RequestTime, C2G_CastleWar_RequestTime.class);
		MessageFactory.addMessage(G2C_CastleWar_RequestTime, G2C_CastleWar_RequestTime.class);
		MessageFactory.addMessage(G2C_CastleWar_Start, G2C_CastleWar_Start.class);
		MessageFactory.addMessage(C2G_CastleWar_OpenServer, C2G_CastleWar_OpenServer.class);
		MessageFactory.addMessage(G2C_CastleWar_OpenServer, G2C_CastleWar_OpenServer.class);
	}
}
