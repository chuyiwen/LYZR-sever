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
package sophia.mmorpg.gameInstance;

import java.util.Collection;
import java.util.Map;

import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.RuntimeResult;

public interface GameInstance {
	String getId();

	/**
	 * {@link GameInstanceState}
	 * 
	 * @return
	 */
	byte getState();

	byte create(Player player, GameInstanceRef gameInstanceRef);

	RuntimeResult open();

	RuntimeResult enter(Player player);
	
	byte close();

	Player getCreater();

	GameScene getCrtGameScene();

	GameInstanceRef getGameInstanceRef();

	boolean isMultiPlayerGameInstance();

	void addPlayer(Player player);

	Collection<Player> getPlayerCollection();

	void addKillRecord(String playerId, String monsterRefId, short count);

	int nextLayer();

	short getLayerId();

	Map<String, ComeFromScene> getComeFromSceneRefId();

	Map<String, Short> getKillRecord(String playerId);
	
	void setState(byte state);
}
