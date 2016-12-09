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
package sophia.mmorpg;

import sophia.foundation.data.SaveableObject;
import sophia.foundation.data.SaveableObjectSaveSlaver;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.plugIns.gameModule.GameModule;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.communication.SocketActionEventListerComponent;
import sophia.mmorpg.core.timer.SFTimeChimeService;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.event.MMORPGEventDefines;
import sophia.mmorpg.gameArea.GameAreaComponent;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.PlayerManagerComponent;
import sophia.mmorpg.player.persistence.PlayerSaveSlaver;
import sophia.mmorpg.player.persistence.PlayerSaveableObject;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateSaveSlaver;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateSaveableObject;
import sophia.mmorpg.player.scene.PlayerAOIComponent;
import sophia.mmorpg.player.team.PlayerTeamManagerComponent;

public final class MMORPGContext {
	private static final String playerComponentTag = "PlayerComponent";

	private static final String gameAreaComponentTag = "GameAreaComponent";
	
	private static final String playerTeamManagerComponentTag = "PlayerTeamManagerComponentTag";

	private static SFTimeChimeService timeChimeService;

	private static PlayerManagerComponent playerComponent = null;

	private static GameAreaComponent gameAreaComponent = null;
	
	private static PlayerTeamManagerComponent playerTeamManagerComponent = null;
	
	private static int serverId = 0; 
	
	private static volatile boolean serverRunning = false;

	private MMORPGContext() {

	}

	public static final void initlize() {
		
		MMORPGEventDefines.registerActionEvents();
		MMORPGSuccessCode.initialize();
		MMORPGErrorCode.initialize();

		timeChimeService = (SFTimeChimeService) GameRoot.requestModule(SFTimeChimeService.class);
		GameModule gameModule = GameContext.getGameModule();

		playerComponent = (PlayerManagerComponent) gameModule.createComponent(PlayerManagerComponent.class, playerComponentTag);
		gameModule.createComponent(SocketActionEventListerComponent.class);

		Class<? extends SaveableObject> clazz = PlayerSaveableObject.class;

		SaveableObjectSaveSlaver<?> playerSaveableObject = new PlayerSaveSlaver();
		@SuppressWarnings("unchecked")
		SaveableObjectSaveSlaver<SaveableObject> temp = (SaveableObjectSaveSlaver<SaveableObject>) playerSaveableObject;
		GameContext.getDataService().getObjectManager().addSaveableObjectSaveSlaver(clazz, temp);

		Class<? extends SaveableObject> immClazz = PlayerImmediateSaveableObject.class;
		SaveableObjectSaveSlaver<?> playerImmediateSaveableObject = new PlayerImmediateSaveSlaver();
		@SuppressWarnings("unchecked")
		SaveableObjectSaveSlaver<SaveableObject> immTemp = (SaveableObjectSaveSlaver<SaveableObject>) playerImmediateSaveableObject;
		GameContext.getDataService().getObjectManager().addSaveableObjectSaveSlaver(immClazz, immTemp);
		gameAreaComponent = (GameAreaComponent) gameModule.createComponent(GameAreaComponent.class, gameAreaComponentTag);
		playerTeamManagerComponent = (PlayerTeamManagerComponent) gameModule.createComponent(PlayerTeamManagerComponent.class, playerTeamManagerComponentTag);
	}

	public static SFTimeChimeService getTimeChimeService() {
		return timeChimeService;
	}

	public static SFTimerCreater getTimerCreater() {
		return timeChimeService.getTimerCreater();
	}

	public static PlayerManagerComponent getPlayerComponent() {
		return playerComponent;
	}

	public static GameAreaComponent getGameAreaComponent() {
		return gameAreaComponent;
	}
	
	public static PlayerTeamManagerComponent playerTeamManagerComponent() {
		return playerTeamManagerComponent;
	}

	public static int getServerId() {
		return serverId;
	}

	public static void setServerId(int serverId) {
		MMORPGContext.serverId = serverId;
	}

	public static int getMaxOnlinePlayerCount() {
		return PlayerManager.getMaxOnlinePlayerCount();
	}

	public static void setMaxOnlinePlayerCount(int maxOnlinePlayerCount) {
		PlayerManager.setMaxOnlinePlayerCount(maxOnlinePlayerCount);
	}

	public static int getPlayerAOICacheCount() {
		return PlayerAOIComponent.getMaxSyncCacheCount();
	}

	public static void setPlayerAOICacheCount(int playerAOICacheCount) {
		PlayerAOIComponent.setMaxSyncCacheCount(playerAOICacheCount);
	}

	public static int getPreloadPlayerCount() {
		return PlayerManager.getPreloadPlayerCount();
	}
	
	public static void setPreloadPlayerCount(int preloadPlayerCount) {
		PlayerManager.setPreloadPlayerCount(preloadPlayerCount);
	}
	
	public static int getMaxCachePlayerCount() {
		return PlayerManager.getMaxCachePlayerCount();
	}
	
	public static void setMaxCachePlayerCount(int maxCachePlayerCount) {
		PlayerManager.setMaxCachePlayerCount(maxCachePlayerCount);
	}
	
	public static int getMaxBoradcastAOIPlayerCount() {
		return GameSceneHelper.getMaxBoradcastAOIPlayerCount();
	}
	
	public static void setMaxBoradcastAOIPlayerCount(int broadcastAOIPlayerCount) {
		GameSceneHelper.setMaxBoradcastAOIPlayerCount(broadcastAOIPlayerCount);
	}

	public static boolean isServerRunning() {
		return serverRunning;
	}

	public static void setServerRunning(boolean serverRunning) {
		MMORPGContext.serverRunning = serverRunning;
	}
}
