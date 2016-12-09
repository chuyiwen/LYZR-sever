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
package newbee.morningGlory.system;

import org.apache.log4j.Logger;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.PlayerManager;

public final class CloseSignalHandler {
	public static final Logger logger = Logger.getLogger(CloseSignalHandler.class);
	
	public static void initialize() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.info("ShutdownHook, save player data");
				PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
				playerManager.kickoutAllOnlinePlayer();
				super.run();
			}
		});
		
	}
}
