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
package sophia.mmorpg.auth.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public final class AuthEventDefines {

	private static final short AuthEvent_Message = MMORPGEventDefines.User_Message_Begin;
	/** 用户身份验证 */
	public static final short C2G_AuthEvent = AuthEvent_Message + 1;

	public static final void registerActionEvents() {
		MessageFactory.addMessage(C2G_AuthEvent, C2G_AuthEvent.class);
	}
}
