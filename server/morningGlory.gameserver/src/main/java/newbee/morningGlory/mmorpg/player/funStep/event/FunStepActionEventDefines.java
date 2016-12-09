package newbee.morningGlory.mmorpg.player.funStep.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class FunStepActionEventDefines {
	public static final short C2G_FunStep_Request  = MGEventDefines.FunStep_Message_Begin + 1;
	public static final short C2G_FunStep_Complete_Request  = MGEventDefines.FunStep_Message_Begin + 2;
	
	public static final short G2C_FunStepList_Response  = MGEventDefines.FunStep_Message_Begin + 10;
	
	
	public static void registerActionEvents(){
		
		MessageFactory.addMessage(C2G_FunStep_Request, C2G_FunStep_Request.class);
		MessageFactory.addMessage(G2C_FunStepList_Response, G2C_FunStepList_Response.class);
		MessageFactory.addMessage(C2G_FunStep_Complete_Request, C2G_FunStep_Complete_Request.class);	
		
	}
}
