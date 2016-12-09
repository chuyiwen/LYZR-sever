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
package sophia.mmorpg.player.property.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public final class PlayerEventDefines {
	
	public static final short G2C_Player_Attribute = MMORPGEventDefines.Player_Message_Begin + 1;
	public static final short C2G_Player_LeaveWorld = MMORPGEventDefines.Player_Message_Begin + 3;
	public static final short C2G_Player_Revive = MMORPGEventDefines.Player_Message_Begin + 11;
	public static final short G2C_Player_Revive = MMORPGEventDefines.Player_Message_Begin + 12;
	public static final short C2G_Player_Heartbeat = MMORPGEventDefines.Player_Message_Begin + 13;
	public static final short G2C_Player_Heartbeat = MMORPGEventDefines.Player_Message_Begin + 14;
	public static final short C2G_Player_KillerInfo = MMORPGEventDefines.Player_Message_Begin + 15;
	public static final short G2C_Player_KillerInfo = MMORPGEventDefines.Player_Message_Begin + 16;
	
	public static final short C2G_OtherPlayer_EquipList = MMORPGEventDefines.Player_Message_Begin + 21;
	public static final short G2C_OtherPlayer_EquipList = MMORPGEventDefines.Player_Message_Begin + 22;
	public static final short C2G_OtherPlayer_Attribute = MMORPGEventDefines.Player_Message_Begin + 23;
	public static final short G2C_OtherPlayer_Attribute = MMORPGEventDefines.Player_Message_Begin + 24;
	public static final short C2G_OtherPlayer_Simple_Attribute = MMORPGEventDefines.Player_Message_Begin + 25;
	public static final short G2C_OtherPlayer_Simple_Attribute = MMORPGEventDefines.Player_Message_Begin + 26;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(G2C_Player_Attribute, G2C_Player_Attribute.class);
		MessageFactory.addMessage(C2G_Player_Revive, C2G_Player_Revive.class);
		MessageFactory.addMessage(G2C_Player_Revive, G2C_Player_Revive.class);
		MessageFactory.addMessage(C2G_OtherPlayer_EquipList, C2G_OtherPlayer_EquipList.class);
		MessageFactory.addMessage(G2C_OtherPlayer_EquipList, G2C_OtherPlayer_EquipList.class);
		MessageFactory.addMessage(C2G_OtherPlayer_Attribute, C2G_OtherPlayer_Attribute.class);
		MessageFactory.addMessage(G2C_OtherPlayer_Attribute, G2C_OtherPlayer_Attribute.class);
		MessageFactory.addMessage(C2G_Player_LeaveWorld, C2G_Player_LeaveWorld.class);
		MessageFactory.addMessage(C2G_OtherPlayer_Simple_Attribute, C2G_OtherPlayer_Simple_Attribute.class);
		MessageFactory.addMessage(G2C_OtherPlayer_Simple_Attribute, G2C_OtherPlayer_Simple_Attribute.class);
		MessageFactory.addMessage(C2G_Player_Heartbeat, C2G_Player_Heartbeat.class);
		MessageFactory.addMessage(G2C_Player_Heartbeat, G2C_Player_Heartbeat.class);
		MessageFactory.addMessage(C2G_Player_KillerInfo, C2G_Player_KillerInfo.class);
		MessageFactory.addMessage(G2C_Player_KillerInfo, G2C_Player_KillerInfo.class);
	}
}
