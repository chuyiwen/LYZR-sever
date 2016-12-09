/**
 * 
 */
package newbee.morningGlory.mmorpg.sprite.buff.actionEvent;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

/**
 * @author yinxinglin
 *
 */
public final class BuffEventDefines {
	
	/**buff */
	public static final short G2C_Attach_Buff = MMORPGEventDefines.Buff_Message_Begin + 1;
	
	public static final short G2C_Effect_Buff = MMORPGEventDefines.Buff_Message_Begin + 2;
	
	public static final short G2C_State_Buff = MMORPGEventDefines.Buff_Message_Begin + 3;
	
	public static final short C2G_Buff_List = MMORPGEventDefines.Buff_Message_Begin + 4;
	
	public static final short G2C_Buff_List = MMORPGEventDefines.Buff_Message_Begin + 5;
	
	public static final short G2C_BuffAppearance_Change =  MMORPGEventDefines.Buff_Message_Begin + 6;
	
	public static final short C2G_MoXueShi_Amount =  MMORPGEventDefines.Buff_Message_Begin + 7;
	
	public static final short G2C_MoXueShi_Amount =  MMORPGEventDefines.Buff_Message_Begin + 8;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(G2C_Attach_Buff, G2C_Attach_Buff.class);
		MessageFactory.addMessage(G2C_Effect_Buff, G2C_Effect_Buff.class);
		MessageFactory.addMessage(G2C_State_Buff, G2C_State_Buff.class);
		MessageFactory.addMessage(C2G_Buff_List, C2G_Buff_List.class);
		MessageFactory.addMessage(G2C_Buff_List, G2C_Buff_List.class);
		MessageFactory.addMessage(G2C_BuffAppearance_Change, G2C_BuffAppearance_Change.class);
		
		MessageFactory.addMessage(C2G_MoXueShi_Amount, C2G_MoXueShi_Amount.class);
		MessageFactory.addMessage(G2C_MoXueShi_Amount, G2C_MoXueShi_Amount.class);
	}
		
}
