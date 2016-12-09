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
package sophia.mmorpg.monster.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class MonsterEventDefines {

	public static final short C2G_Monster_OwnerTransfer = MMORPGEventDefines.Monster_Message_Begin + 1;
	public static final short G2C_Monster_OwnerTransfer = MMORPGEventDefines.Monster_Message_Begin + 2;
	public static final short C2G_Monster_ClearError = MMORPGEventDefines.Monster_Message_Begin + 3;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Monster_OwnerTransfer, C2G_Monster_OwnerTransfer.class);
		MessageFactory.addMessage(G2C_Monster_OwnerTransfer, G2C_Monster_OwnerTransfer.class);
		MessageFactory.addMessage(C2G_Monster_ClearError, C2G_Monster_ClearError.class);
	}

}
