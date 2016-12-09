/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.digs.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

/**
 * @author yinxinglin
 * 
 */
public final class MGDigsEventDefines {

	
	
	public static final short C2G_Digs_Type = MGEventDefines.Activity_Message_Begin + 25;
	public static final short C2G_Digs_List = MGEventDefines.Activity_Message_Begin + 26;
	public static final short G2C_Digs_List = MGEventDefines.Activity_Message_Begin + 27;
	public static final short G2C_Digs_Update = MGEventDefines.Activity_Message_Begin + 28;
	public static final short C2G_Digs_Switch = MGEventDefines.Activity_Message_Begin + 29;
	public static final short G2C_Digs_Result = MGEventDefines.Activity_Message_Begin + 74;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Digs_Type, C2G_Digs_Type.class);
		MessageFactory.addMessage(C2G_Digs_List, C2G_Digs_List.class);
		MessageFactory.addMessage(G2C_Digs_List, G2C_Digs_List.class);
		MessageFactory.addMessage(G2C_Digs_Update, G2C_Digs_Update.class);
		MessageFactory.addMessage(C2G_Digs_Switch, C2G_Digs_Switch.class);
		MessageFactory.addMessage(G2C_Digs_Result, G2C_Digs_Result.class);
	}

}
