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
package newbee.morningGlory;

import org.apache.log4j.Logger;

import newbee.morningGlory.character.CharecterGMNotify;
import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.mmorpg.ladder.MGLadderMgr;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarApplyMgr;
import newbee.morningGlory.mmorpg.sortboard.SortboardMgr;
import newbee.morningGlory.save.SaveGameDataService;
import sophia.foundation.core.FoundationContext;
import sophia.foundation.task.Task;
import sophia.foundation.util.DebugUtil;
import sophia.game.GameContext;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.friend.FriendSystemManager;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.PlayerManagerComponent;
import sophia.mmorpg.stat.StatService;

import com.google.common.util.concurrent.Service.State;

public final class GameService {
	
	private static final Logger logger = Logger.getLogger(GameService.class);
	
	public static void startHttpService() {
		FoundationContext.getTaskManager().scheduleTask(new Task() {
			@Override
			public void run() throws Exception {
				HttpService.startup(); // 这个东西不能在主线程启动..
			}
		});
	}

	public static void startStatLogService() {
		boolean startStatLogService = GameContext.getProperties().getBooleanProperty("sophia.statlog.start", true);
		if (startStatLogService) {
			State statLogServiceState = StatService.getInstance().startAndWait();
			if (statLogServiceState != State.RUNNING) {
				logger.error("StatLogService start failed.");
				throw new RuntimeException("StatLogService start failed.");
			} else {
				logger.info("StatLogService was started.");
			}
		}
	}
	
	public static void stopStatLogService() {
		boolean startStatLogService = GameContext.getProperties().getBooleanProperty("sophia.statlog.start", true);
		if (startStatLogService) {
			State statLogServiceState = StatService.getInstance().stopAndWait();
			if (statLogServiceState != State.TERMINATED) {
				logger.error("StatLogService stop failed.");
			} else {
				logger.info("StatLogService was terminated.");
			}
		}
	}

	public static void startSaveGameDataService() {
		State statSaveDataServiceState = SaveGameDataService.getInstance().startAndWait();
		if (statSaveDataServiceState != State.RUNNING) {
			logger.error("SaveGameDataService start failed.");
			throw new RuntimeException("SaveGameDataService start failed.");
		} else {
			logger.info("SaveGameDataService was started.");
		}
	}

	public static void stopSaveGameDataService() {
		State statSaveDataServiceState = SaveGameDataService.getInstance().stopAndWait();
		if (statSaveDataServiceState != State.TERMINATED) {
			logger.error("SaveGameDataService stop failed.");
		} else {
			logger.info("SaveGameDataService was terminated.");
		}
	}

	public static void startSendCreateLoginService() {
		State statSendCreateLoginService = CharecterGMNotify.getInstance().startAndWait();
		if (statSendCreateLoginService != State.RUNNING) {
			logger.error("CharecterGMNotifyService start failed.");
			throw new RuntimeException("CharecterGMNotifyService start failed.");
		} else {
			logger.info("CharecterGMNotifyService was started.");
		}
	}

	public static void stopSendCreateLoginService() {
		State statSendCreateLoginService = CharecterGMNotify.getInstance().stopAndWait();
		if (statSendCreateLoginService != State.TERMINATED) {
			logger.error("CharecterGMNotifyService stop failed.");
		} else {
			logger.info("CharecterGMNotifyService was terminated.");
		}
	}

	public static void startCommunicationService() {
		State communicationServiceState = GameContext.getSimulatorCommunicationService().startAndWait();
		if (communicationServiceState != State.RUNNING) {
			logger.error("CommunicationService start failed.");
			throw new RuntimeException("CommunicationService start failed.");
		} else {
			logger.info("CommunicationService was started.");
		}
	}

	public static void stopCommunicationService() {
		State communicationServiceState = GameContext.getSimulatorCommunicationService().stopAndWait();
		if (communicationServiceState != State.TERMINATED) {
			logger.error("CommunicationService stop failed.");
		} else {
			logger.info("CommunicationService was terminated.");
		}
	}
	
	public static void startPlayerPeriodSaveService() {
		PlayerManagerComponent playerManagerComponent = MMORPGContext.getPlayerComponent();
		State st = playerManagerComponent.getSaveService().startAndWait();
		if (st != State.RUNNING) {
			logger.error("PlayerPeriodSaveService start failed.");
			throw new RuntimeException("PlayerPeriodSaveService start failed.");
		} else {
			logger.info("PlayerPeriodSaveService was started.");
		}
	}
	
	public static void stopPlayerPeriodSaveService() {
		// 执行定时保存并关闭
		PlayerManagerComponent playerManagerComponent = MMORPGContext.getPlayerComponent();
		State st = playerManagerComponent.getSaveService().stopAndWait();
		if (st != State.TERMINATED) {
			logger.error("PlayerPeriodSaveService stop failed.");
		} else {
			logger.info("PlayerPeriodSaveService was terminated.");
		}
	}
	
	public static void startPlayerImmediateSaveService() {
		PlayerManagerComponent playerManagerComponent = MMORPGContext.getPlayerComponent();
		State st = playerManagerComponent.getSaveImmediateService().startAndWait();
		if (st != State.RUNNING) {
			logger.error("PlayerImmediateSaveService start failed.");
			throw new RuntimeException("PlayerImmediateSaveService start failed.");
		} else {
			logger.info("PlayerImmediateSaveService was started.");
		}
	}
	
	public static void stopPlayerImmediateSaveService() {
		PlayerManagerComponent playerManagerComponent = MMORPGContext.getPlayerComponent();
		State st = playerManagerComponent.getSaveImmediateService().stopAndWait();
		if (st != State.TERMINATED) {
			logger.error("PlayerImmediateSaveService stop failed.");
		} else {
			logger.info("PlayerImmediateSaveService was terminated.");
		}
	}
	
	public static void stopCommonTimer() {
		try {
			CastleWarApplyMgr.getInstance().destoryCastleWarTimer();
			SceneActivityMgr.destoryTimer();
			SortboardMgr.getInstance().stopTimer();
			LimitTimeActivityMgr.getInstance().stopTimer();
			MGLadderMgr.getInstance().destoryTimer();
			FriendSystemManager.cancelFriendSystemTimer();
			PlayerManager.destoryTimer();
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}
	}
}
