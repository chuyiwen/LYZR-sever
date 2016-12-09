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
package sophia.mmorpg.base.scene.mgr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.player.Player;

public final class PlayerMgrComponent extends ConcreteComponent<GameScene>{
	
	private static final Logger logger = Logger.getLogger(PlayerMgrComponent.class);

	private Map<String, Player> playerMap = new ConcurrentHashMap<>();	// playerID , Player
	
	public Player getPlayer(String playerId) {
		return playerMap.get(playerId);
	}
	
	public void addPlayer(Player player) {
		playerMap.put(player.getId(), player);
	}
	
	public void removePlayer(Player player) {
		playerMap.remove(player.getId());
	}
	
	public void sceneTick(GameEvent<?> event) {
		if (playerMap.isEmpty()) {
			return;
		}
		
		for (Player player : playerMap.values()) {
			try {
				sendGameEvent(event, player.getId());
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}
	
	public Map<String, Player> getPlayerMap() {
		return playerMap;
	}
	
	public boolean hasPlayer() {
		return !playerMap.isEmpty();
	}
}
