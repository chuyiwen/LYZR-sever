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
package newbee.morningGlory.mmorpg.gameInstance;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import newbee.morningGlory.MorningGloryContext;

import org.apache.log4j.Logger;

import sophia.mmorpg.gameInstance.GameInstance;

import com.google.common.base.Preconditions;

/**
 * 副本-开放时间管理 !副本开放推送 !副本关闭推送
 */
public final class MGGameInstanceOpeningTimeMgr {
	private static final Logger logger = Logger.getLogger(MGGameInstanceOpeningTimeMgr.class.getName());

	private final ConcurrentMap<String, MGGameInstanceOpeningTimeLimit> idToGameInstanceOpeningTimeLimit = new ConcurrentHashMap<>();

	public MGGameInstanceOpeningTimeMgr() {
	}

	public void destroy() {
		idToGameInstanceOpeningTimeLimit.clear();
	}

	public void removeGameInstanceOpeningTimeLimit(String gameInstanceId) {
		Preconditions.checkNotNull(gameInstanceId);
		if (logger.isDebugEnabled()) {
			logger.debug("销毁副本定时器实例:" + gameInstanceId);
		}
		
		idToGameInstanceOpeningTimeLimit.remove(gameInstanceId);
	}

	public void addGameInstanceOpeningTimeLimit(MGGameInstanceOpeningTimeLimit gameInstanceOpeningTimeLimit) {
		Preconditions.checkNotNull(gameInstanceOpeningTimeLimit);
		if (logger.isDebugEnabled()) {
			logger.debug("增加副本定时器实例:" + gameInstanceOpeningTimeLimit.getGameInstance().getId());
		}
		
		idToGameInstanceOpeningTimeLimit.putIfAbsent(gameInstanceOpeningTimeLimit.getGameInstance().getId(), gameInstanceOpeningTimeLimit);
	}

	public void tick() {
		Iterator<Entry<String, MGGameInstanceOpeningTimeLimit>> it = idToGameInstanceOpeningTimeLimit.entrySet().iterator();
		ConcurrentMap<String,GameInstance> idToGameInstanceMap = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr().getIdToGameInstanceMap();
		while(it.hasNext()) {
			Entry<String, MGGameInstanceOpeningTimeLimit> entry = it.next();
			String gameInstanceId = entry.getKey();
			if(logger.isDebugEnabled()) {
				logger.debug("副本定时器 gameInstanceId = " + gameInstanceId);
			}
			
			MGGameInstanceOpeningTimeLimit gameInstanceOpeningTimeLimit = entry.getValue();
			GameInstance gameInstance = idToGameInstanceMap.get(gameInstanceId);
			if(gameInstance == null) {
				if(logger.isDebugEnabled()) {
					logger.debug("gameInstanceId = " + gameInstanceId +" 的副本已经被清除");
				}
				
				it.remove();
				continue;
			}
			
			try {
				gameInstanceOpeningTimeLimit.tick();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	
	}

	public void managedGameInstance(MGGameInstance gameInstance, long openingTime, long duringTime, byte type) {
		MGGameInstanceOpeningTimeLimit gameInstanceOpeningTimeLimit = new MGGameInstanceOpeningTimeLimit();
		long closeTime = openingTime + duringTime;
		if (duringTime >= 86400) {
			closeTime = Integer.MAX_VALUE;
		}
		gameInstanceOpeningTimeLimit.setGameInstance(gameInstance);
		gameInstanceOpeningTimeLimit.setCloseTime(closeTime);
		gameInstanceOpeningTimeLimit.setOpenTime(openingTime);
		gameInstanceOpeningTimeLimit.setOpeningTimeLimit(duringTime);

		addGameInstanceOpeningTimeLimit(gameInstanceOpeningTimeLimit);
	}

	public boolean opening(String gameInstanceRefId) {
		// 暂时未实现
		boolean ret = false;

		return ret;
	}

	/**
	 * 副本开放全服推送(分发的方式)
	 * 
	 * @param playerList
	 * @param gameInstanceRefId
	 * @param openingTime
	 *            (单位：秒)
	 * @param duringTime
	 *            (单位：秒)
	 * @param type
	 */
	// TODO FIXME 暂时没用代码
	// private void distributeOpening(List<Player> playerList, GameInstanceRef
	// gameInstanceRef, long openingTime, long duringTime, byte type) {
	// for (Player player : playerList) {
	// try {
	// distributeOpening(player, gameInstanceRef, openingTime, duringTime,
	// type);
	// } catch (Exception e) {
	// logger.error("", e);
	// }
	// }
	// }

	// TODO FIXME 暂时没用代码
	// public void opening(Player player, GameInstanceRef gameInstanceRef, long
	// openingTime, long duringTime, byte type) {
	// MGGameInstanceProvider gameInstanceProvider =
	// MGGameInstanceProvider.getInstance();
	// GameInstanceMgr gameInstanceMgr =
	// MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr();
	//
	// MGGameInstance gameInstance =
	// gameInstanceProvider.get(MGGameInstance.class, gameInstanceRef.getId());
	// PlayerGameInstanceComponent playerGameInstanceComponent =
	// (PlayerGameInstanceComponent)
	// player.getTagged(PlayerGameInstanceComponent.Tag);
	//
	// boolean ret =
	// playerGameInstanceComponent.addGameInstanceIdIfAbsent(gameInstanceRef.getId(),
	// gameInstance.getId());
	// if (!ret) {
	// return;
	// }
	//
	// gameInstanceMgr.addGameInstance(gameInstance);
	// gameInstance.create(player, gameInstanceRef);
	//
	// MGGameInstanceOpeningTimeLimit gameInstanceOpeningTimeLimit = new
	// MGGameInstanceOpeningTimeLimit();
	// long closeTime = openingTime + duringTime;
	// gameInstanceOpeningTimeLimit.setGameInstance(gameInstance);
	// gameInstanceOpeningTimeLimit.setCloseTime(closeTime);
	// gameInstanceOpeningTimeLimit.setOpenTime(openingTime);
	// gameInstanceOpeningTimeLimit.setOpeningTimeLimit(duringTime);
	//
	// addGameInstanceOpeningTimeLimit(gameInstanceOpeningTimeLimit);
	//
	// }

}
