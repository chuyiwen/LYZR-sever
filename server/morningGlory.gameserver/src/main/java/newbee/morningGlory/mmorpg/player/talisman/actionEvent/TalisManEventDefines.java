package newbee.morningGlory.mmorpg.player.talisman.actionEvent;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class TalisManEventDefines {
	

	public static final short C2G_Talisman_List = MGEventDefines.Talisman_Message_Begin + 1;
	
	public static final short G2C_Talisman_List = MGEventDefines.Talisman_Message_Begin + 2;
	
	public static final short C2G_Talisman_Operation = MGEventDefines.Talisman_Message_Begin + 3;

	public static final short G2C_Talisman_Operation = MGEventDefines.Talisman_Message_Begin + 4;
	
	public static final short C2G_Talisman_Statistics = MGEventDefines.Talisman_Message_Begin + 9;
	
	public static final short G2C_Talisman_Statistics = MGEventDefines.Talisman_Message_Begin + 10;
	
	public static final short C2G_Citta_LevelUp = MGEventDefines.Talisman_Message_Begin + 11;
	
	public static final short G2C_Citta_LevelUp = MGEventDefines.Talisman_Message_Begin + 12;
	
	public static final short C2G_Talisman_Reward = MGEventDefines.Talisman_Message_Begin + 13;
	
	public static final short G2C_Talisman_Reward = MGEventDefines.Talisman_Message_Begin + 14;
	
	public static final short C2G_Talisman_GetReward = MGEventDefines.Talisman_Message_Begin + 15;
	
	public static final short G2C_Talisman_GetReward = MGEventDefines.Talisman_Message_Begin + 16;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(G2C_Talisman_List, G2C_Talisman_List.class);
		MessageFactory.addMessage(C2G_Talisman_List, C2G_Talisman_List.class);

		MessageFactory.addMessage(G2C_Talisman_Operation, G2C_Talisman_Operation.class);
		MessageFactory.addMessage(C2G_Talisman_Operation, C2G_Talisman_Operation.class);
		
		MessageFactory.addMessage(C2G_Talisman_Statistics, C2G_Talisman_Statistics.class);
		MessageFactory.addMessage(G2C_Talisman_Statistics, G2C_Talisman_Statistics.class);
		
		MessageFactory.addMessage(C2G_Citta_LevelUp, C2G_Citta_LevelUp.class);
		MessageFactory.addMessage(G2C_Citta_LevelUp, G2C_Citta_LevelUp.class);
		
		MessageFactory.addMessage(C2G_Talisman_Reward, C2G_Talisman_Reward.class);
		MessageFactory.addMessage(G2C_Talisman_Reward, G2C_Talisman_Reward.class);
		
		MessageFactory.addMessage(C2G_Talisman_GetReward, C2G_Talisman_GetReward.class);
		MessageFactory.addMessage(G2C_Talisman_GetReward, G2C_Talisman_GetReward.class);


	}

}
