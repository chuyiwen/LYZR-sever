/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package newbee.morningGlory.mmorpg.player.depot.event;

import sophia.foundation.communication.core.MessageFactory;
import newbee.morningGlory.event.MGEventDefines;

public class PlayerDepotEventDefines {
	public static final short C2G_WareHouse_Capacity = MGEventDefines.Depot_Message_Begin + 1;
	public static final short G2C_WareHouse_Capacity = MGEventDefines.Depot_Message_Begin + 2;
	
	public static final short C2G_WareHouse_Item_List = MGEventDefines.Depot_Message_Begin + 3;
	public static final short G2C_WareHouse_Item_List = MGEventDefines.Depot_Message_Begin + 4;
	
	public static final short C2G_WareHouse_Item_Update = MGEventDefines.Depot_Message_Begin + 5;
	public static final short G2C_WareHouse_Item_Update = MGEventDefines.Depot_Message_Begin + 6;
	
	public static final short C2G_WareHouse_Item_SoltUnLock = MGEventDefines.Depot_Message_Begin + 9;
	public static final short G2C_WareHouse_Item_SoltUnLock = MGEventDefines.Depot_Message_Begin + 10;
	
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_WareHouse_Capacity, C2G_WareHouse_Capacity.class);
		MessageFactory.addMessage(G2C_WareHouse_Capacity, G2C_WareHouse_Capacity.class);
		MessageFactory.addMessage(C2G_WareHouse_Item_List, C2G_WareHouse_Item_List.class);
		MessageFactory.addMessage(G2C_WareHouse_Item_List, G2C_WareHouse_Item_List.class);
		MessageFactory.addMessage(C2G_WareHouse_Item_Update, C2G_WareHouse_Item_Update.class);
		MessageFactory.addMessage(G2C_WareHouse_Item_Update, G2C_WareHouse_Item_Update.class);
		MessageFactory.addMessage(C2G_WareHouse_Item_SoltUnLock, C2G_WareHouse_Item_SoltUnLock.class);
		MessageFactory.addMessage(G2C_WareHouse_Item_SoltUnLock, G2C_WareHouse_Item_SoltUnLock.class);
	}
	
	

}
