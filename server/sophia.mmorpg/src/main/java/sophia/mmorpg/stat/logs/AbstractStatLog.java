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
package sophia.mmorpg.stat.logs;

import sophia.mmorpg.auth.AuthIdentity;
import sophia.mmorpg.player.Player;
import sophia.stat.StatLog;

public abstract class AbstractStatLog extends StatLog {

	public abstract byte getStatLogType();

	public void setPlayer(Player player) {
		if (player != null) {
			super.setIdentityName(player.getIdentity().getName());
			super.setPlayerId(player.getId());
			super.setPlayerName(player.getName());
			AuthIdentity identity = (AuthIdentity) player.getIdentity();
			super.setQdCode1(identity.getQdCode1());
			super.setQdCode2(identity.getQdCode2());
		}

		init(getStatLogType());
	}
}
