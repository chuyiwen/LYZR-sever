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
package sophia.mmorpg.player.persistence.immediatelySave;

import org.apache.log4j.Logger;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;

public final class PlayerImmediateDaoFacade {
	private static final Logger logger = Logger.getLogger(PlayerImmediateDaoFacade.class);

	public static void update(Player player) {
		MMORPGContext.getPlayerComponent().getSaveImmediateService().saveImmediateData(player);
	}

	public static void insert(Player player) {
		MMORPGContext.getPlayerComponent().getSaveImmediateService().insertImmediateData(player);
	}
}
