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
package newbee.morningGlory.mmorpg.player.activity.sevenLogin.event;

import newbee.morningGlory.event.MGEventDefines;

public class MGSevenLoginDefines {
	
	public static final short C2G_SevenLogin_ReceiveState = MGEventDefines.Activity_Message_Begin + 9;
	public static final short G2C_SevenLogin_ReceiveState = MGEventDefines.Activity_Message_Begin + 10;
	public static final short C2G_SevenLogin_HadReceive = MGEventDefines.Activity_Message_Begin + 12;
	public static final short G2C_SevenLogin_HaveReceive = MGEventDefines.Activity_Message_Begin + 11;
	public static final short C2G_SevenLogin_HaveReceive = MGEventDefines.Activity_Message_Begin + 47;
	
	
	public static void registerActionEvents(){
//		MessageFactory.addMessage(C2G_SevenLogin_ReceiveState,C2G_SevenLogin_ReceiveState.class);
//		MessageFactory.addMessage(G2C_SevenLogin_ReceiveState,G2C_SevenLogin_ReceiveState.class);
//		MessageFactory.addMessage(G2C_SevenLogin_HaveReceive,G2C_SevenLogin_HaveReceive.class);
//		MessageFactory.addMessage(C2G_SevenLogin_HadReceive,C2G_SevenLogin_HadReceive.class);
//		MessageFactory.addMessage(C2G_SevenLogin_HaveReceive,C2G_SevenLogin_HaveReceive.class);
	}
}
