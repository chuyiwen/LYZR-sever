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

import java.util.Collection;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.player.gameInstance.PlayerGameInstanceComponent;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.Player;

/**
 * 副本-（进入后）时间限制，玩家在副本的时间，不能超过该时间限制
 */
public final class MGGameInstanceOpeningTimeLimit {
	private static final Logger logger = Logger.getLogger(MGGameInstanceOpeningTimeLimit.class.getName());
	private MGGameInstance gameInstance;

	/** 开放时间（单位：秒） **/
	private long openTime;
	/** 持续时间（单位：秒） **/
	private long openingTimeLimit;
	/** 关闭时间（单位：秒） **/
	private long closeTime;

	public MGGameInstanceOpeningTimeLimit() {

	}

	public MGGameInstanceOpeningTimeLimit(MGGameInstance gameInstance) {
		this.gameInstance = gameInstance;
	}

	public void tick() {
		if (!checkClose()) {
			return;
		}
		
		Collection<Player> playerList = gameInstance.getPlayerCollection();
		if (playerList == null || playerList.isEmpty()) {
			MorningGloryContext.getGameInstanceSystemComponent().clearGameInstanceResource(gameInstance);
			if (logger.isDebugEnabled()) {
				logger.debug("time limit close gameInstance, " + gameInstance);
			}
			return;
		}

		// 传送副本内玩家出副本
		for (Player player : playerList) {
			try {
				PlayerGameInstanceComponent playerGameInstanceComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
				playerGameInstanceComponent.goBackComeFromScene(gameInstance);
			} catch (Exception e) {
				logger.error("玩家离开副本出错, player=" + player);
				e.printStackTrace();
			}
		}
		
		playerList.clear();
	}

	private boolean checkClose() {
		long nowSecond = System.currentTimeMillis() / 1000;
		
		if(logger.isDebugEnabled()) {
			logger.debug("nowSeconds = " + nowSecond + " closeTime = " +closeTime);
		}
		
		if (!this.gameInstance.isMultiPlayerGameInstance() && this.closeTime <= nowSecond) {
			return true;
		}
		
		return false;
	}

	public MGGameInstance getGameInstance() {
		return gameInstance;
	}

	public void setGameInstance(MGGameInstance gameInstance) {
		this.gameInstance = gameInstance;
	}

	public long getOpenTime() {
		return openTime;
	}

	public void setOpenTime(long openTime) {
		this.openTime = openTime;
	}

	public long getOpeningTimeLimit() {
		return openingTimeLimit;
	}

	public void setOpeningTimeLimit(long openingTimeLimit) {
		this.openingTimeLimit = openingTimeLimit;
	}

	public long getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}
}
