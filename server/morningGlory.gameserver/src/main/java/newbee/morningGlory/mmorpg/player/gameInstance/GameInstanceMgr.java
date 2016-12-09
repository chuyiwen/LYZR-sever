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
package newbee.morningGlory.mmorpg.player.gameInstance;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceRestore;

import org.apache.log4j.Logger;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.gameInstance.GameInstanceManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class GameInstanceMgr implements GameInstanceManager {
	
	private static final Logger logger = Logger.getLogger(GameInstanceMgr.class.getName());
	
	private static final long removeGameInstanceInterval = 1 * 60 * 60 * 1000L;
	
	// gameInstanceId, GameInstance
	private final ConcurrentMap<String, GameInstance> idToGameInstanceMap = new ConcurrentHashMap<>();
	// 玩家进程记录管理,在玩家第一次查询时载入数据,在玩家下线时持久化数据
	private GameInstanceScheduleMgr scheduleManager = new GameInstanceScheduleMgr();
	
	/** 玩家副本离线缓存*/
	// playerId, MGGameInstanceRestore
	private ConcurrentHashMap<String, MGGameInstanceRestore> gameInstanceCache = new ConcurrentHashMap<>();

	public ConcurrentHashMap<String, MGGameInstanceRestore> getGameInstanceCache() {
		return gameInstanceCache;
	}

	public GameInstanceMgr() {

	}

	public void destroy() {
		if(logger.isDebugEnabled()) {
			logger.debug("idToGameInstanceMap clear");
		}
		
		idToGameInstanceMap.clear();
		scheduleManager.destroy();
	}

	public void addGameInstance(GameInstance gameInstance) {
		Preconditions.checkNotNull(gameInstance);
		if (logger.isDebugEnabled()) {
			logger.debug("增加副本:" + gameInstance.getId());
		}

		idToGameInstanceMap.putIfAbsent(gameInstance.getId(), gameInstance);
	}

	public void removeGameInstance(GameInstance gameInstance) {
		Preconditions.checkNotNull(gameInstance);
		if (logger.isDebugEnabled()) {
			logger.debug("销毁副本:" + gameInstance.getId());
		}
		
		idToGameInstanceMap.remove(gameInstance.getId());
	}

	public GameInstance getGameInstace(String gameInstanceId) {
		if (Strings.isNullOrEmpty(gameInstanceId)) {
			return null;
		}
		
		return idToGameInstanceMap.get(gameInstanceId);
	}

	public ConcurrentMap<String, GameInstance> getIdToGameInstanceMap() {
		return idToGameInstanceMap;
	}

	public int getNumber() {
		return idToGameInstanceMap.size();
	}

	public GameInstanceScheduleMgr getScheduleManager() {
		return scheduleManager;
	}

	public void saveGameInstanceData() throws Exception {
		scheduleManager.saveGameInstanceData();
	}

	@Override
	public boolean hasGameInstanceCache(Player player) {
		return gameInstanceCache.containsKey(player.getId());
	}
	
	public void addGameInstanceCache(Player player, MGGameInstanceRestore gameInstanceRestore) {
		gameInstanceCache.put(player.getId(), gameInstanceRestore);
	}
	
	public MGGameInstanceRestore removeGameInstanceCache(Player player) {
		return gameInstanceCache.remove(player.getId());
	}
	
	public void kickTimeoutGameInstance() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		long now = System.currentTimeMillis();
		Set<String> keySet = gameInstanceCache.keySet();
		for (String playerId : keySet) {
			Player player = playerManager.getPlayer(playerId);
			if (player == null) {
				logger.error("kickTimeoutGameInstance, player is null, playerId=" + playerId);
			} else if (now - MGPropertyAccesser.getLastLogoutTime(player.getProperty()) >= removeGameInstanceInterval) {
				PlayerGameInstanceComponent playerGameInstanceComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
				playerGameInstanceComponent.leaveWorldAndClearGameInstance();
				if(logger.isDebugEnabled()) {
					logger.debug("一个小时时间到， 清除缓存中玩家，playerId="+ playerId +" 副本数据");
				}
			} else {
				continue;
			}
			
			MGGameInstanceRestore mgGameInstanceRestore = gameInstanceCache.remove(playerId);
			if (mgGameInstanceRestore != null) {
				clearGameInstanceCache(player, mgGameInstanceRestore);
			}
			
		}
	}
	
	private void clearGameInstanceCache(Player player, MGGameInstanceRestore mgGameInstanceRestore) {
		Map<String, String> refIdToGameInstanceId = mgGameInstanceRestore.getRefIdToGameInstanceId();
		for (String gameInstanceRefId : refIdToGameInstanceId.keySet()) {
			String gameInstanceId = refIdToGameInstanceId.get(gameInstanceRefId);
			GameInstance gameInstance = getGameInstace(gameInstanceId);
			if (gameInstance == null) {
				continue;
			}
			
			Collection<Player> playerCollection = gameInstance.getPlayerCollection();
			if (playerCollection != null) {
				// 把自己从副本玩家列表中移除
				gameInstance.getPlayerCollection().remove(player);
			}
			
			if (playerCollection == null || playerCollection.isEmpty()) {
				MorningGloryContext.getGameInstanceSystemComponent().clearGameInstanceResource(gameInstance);
			}
		}
	}
}
