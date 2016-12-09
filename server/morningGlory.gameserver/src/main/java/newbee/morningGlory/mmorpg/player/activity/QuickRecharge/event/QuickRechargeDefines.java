/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.QuickRecharge.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;


public class QuickRechargeDefines {
	public static final short C2G_QuickRecharge_List = MGEventDefines.QuickRecharge_Message_Begin + 1;
	
	public static final short G2C_QuickRecharge_List = MGEventDefines.QuickRecharge_Message_Begin + 2;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_QuickRecharge_List, C2G_QuickRecharge_List.class);
		MessageFactory.addMessage(G2C_QuickRecharge_List, G2C_QuickRecharge_List.class);
	}
}
