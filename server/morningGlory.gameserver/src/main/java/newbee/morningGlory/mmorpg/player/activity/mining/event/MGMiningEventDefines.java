package newbee.morningGlory.mmorpg.player.activity.mining.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class MGMiningEventDefines {
	
	//挖矿活动
	public static final short C2G_Mining_EnterEvent = MGEventDefines.Mining_Message_Begin + 1;
	public static final short G2C_Mining_EnterEvent = MGEventDefines.Mining_Message_Begin + 2;
	public static final short C2G_Mining_ExitEvent = MGEventDefines.Mining_Message_Begin + 3;
	public static final short G2C_Mining_ExitEvent = MGEventDefines.Mining_Message_Begin + 4;
	public static final short G2C_Mining_FinishEvent = MGEventDefines.Mining_Message_Begin + 5;
	public static final short G2C_Mining_Update =MGEventDefines.Mining_Message_Begin + 6;
	public static final short G2C_Mining_Open = MGEventDefines.Mining_Message_Begin + 7;
	public static final short C2G_Mining_Open = MGEventDefines.Mining_Message_Begin + 8;
	public static final short C2G_Mining_RemainTime = MGEventDefines.Mining_Message_Begin + 9;
	public static final short G2C_Mining_RemainTime = MGEventDefines.Mining_Message_Begin + 10;
	public static final short C2G_Mining_RemainRrfreshTime = MGEventDefines.Mining_Message_Begin + 11;
	public static final short G2C_Mining_RemainRrfreshTime = MGEventDefines.Mining_Message_Begin + 12;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Mining_EnterEvent, C2G_Mining_EnterEvent.class);
		MessageFactory.addMessage(G2C_Mining_EnterEvent, G2C_Mining_EnterEvent.class);
		MessageFactory.addMessage(C2G_Mining_ExitEvent, C2G_Mining_ExitEvent.class);
		MessageFactory.addMessage(G2C_Mining_ExitEvent, G2C_Mining_ExitEvent.class);
		MessageFactory.addMessage(G2C_Mining_FinishEvent, G2C_Mining_FinishEvent.class);
		MessageFactory.addMessage(G2C_Mining_Update, G2C_Mining_Update.class);
		MessageFactory.addMessage(G2C_Mining_Open, G2C_Mining_Open.class);
		MessageFactory.addMessage(C2G_Mining_Open, C2G_Mining_Open.class);
		MessageFactory.addMessage(C2G_Mining_RemainTime, C2G_Mining_RemainTime.class);
		MessageFactory.addMessage(G2C_Mining_RemainTime, G2C_Mining_RemainTime.class);
		MessageFactory.addMessage(C2G_Mining_RemainRrfreshTime, C2G_Mining_RemainRrfreshTime.class);
		MessageFactory.addMessage(G2C_Mining_RemainRrfreshTime, G2C_Mining_RemainRrfreshTime.class);
	}

}
