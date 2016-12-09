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
package newbee.morningGlory.mmorpg.player.activity.resDownload.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class ResDownloadDefines {
	
	public static final short C2G_GetReward = MGEventDefines.ResDownload_Message_Begin + 1;
	public static final short C2G_CanGetReward = MGEventDefines.ResDownload_Message_Begin + 3;
	public static final short G2C_CanGetReward = MGEventDefines.ResDownload_Message_Begin + 4;
	
	public static void registerActionEvents() {

		MessageFactory.addMessage(C2G_GetReward, C2G_GetReward.class);
		MessageFactory.addMessage(C2G_CanGetReward, C2G_CanGetReward.class);
		MessageFactory.addMessage(G2C_CanGetReward, G2C_CanGetReward.class);
		
	}
}
