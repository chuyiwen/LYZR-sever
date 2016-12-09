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
package sophia.mmorpg.player.team.actionEvent.kickedOut;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 踢出队伍成员
 */
public final class C2G_KickedOutTeamMemberActionEvent extends ActionEventBase {

	private String kickdOutPlayerId;

	public String getKickdOutPlayerId() {
		return kickdOutPlayerId;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.kickdOutPlayerId = getString(buffer);
	}

}
