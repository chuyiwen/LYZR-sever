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

import newbee.morningGlory.mmorpg.player.gameInstance.GameInstanceMgr;

import org.apache.log4j.Logger;

import sophia.game.component.AbstractComponent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.player.PlayerEnterSceneCheckFacade;

public final class MGGameInstanceSystemComponent extends AbstractComponent {

	private static final Logger logger = Logger.getLogger(MGGameInstanceSystemComponent.class.getName());

	private final GameInstanceMgr gameInstanceMgr = new GameInstanceMgr();

	private final MGGameInstanceOpeningTimeMgr gameInstanceOpeningTimeMgr = new MGGameInstanceOpeningTimeMgr();

	private SFTimer timer;

	private SFTimer gameInstanceCacheTimer;

	@Override
	public void ready() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.secondInterval(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				gameInstanceOpeningTimeMgr.tick();
			}

			@Override
			public void handleServiceShutdown() {

			}
		});

		gameInstanceCacheTimer = timerCreater.minuteCalendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				getGameInstanceMgr().kickTimeoutGameInstance();
			}

			@Override
			public void handleServiceShutdown() {
			}
		});

		if (logger.isDebugEnabled()) {
			logger.debug(" GameInstnceTick was running.");
			logger.debug(" GameInstnceCacheTick was running");
		}
		
		PlayerEnterSceneCheckFacade.setGameInstanceManager(gameInstanceMgr);
		super.ready();
	}

	@Override
	public void suspend() {
		if (timer != null) {
			timer.cancel();
		}
		
		if(gameInstanceCacheTimer != null) {
			gameInstanceCacheTimer.cancel();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(" GameInstnceTick was terminated.");
			logger.debug(" GameInstnceCacheTick was terminated.");
		}
		
		gameInstanceMgr.destroy();
		gameInstanceOpeningTimeMgr.destroy();
		super.destroy();
	}

	public MGGameInstanceSystemComponent() {

	}

	public GameInstanceMgr getGameInstanceMgr() {
		return gameInstanceMgr;
	}

	public MGGameInstanceOpeningTimeMgr getGameInstanceOpeningTimeMgr() {
		return gameInstanceOpeningTimeMgr;
	}

	public void clearGameInstanceResource(GameInstance gameInstance) {
		logger.debug("clearGameInstanceResource");
		this.gameInstanceOpeningTimeMgr.removeGameInstanceOpeningTimeLimit(gameInstance.getId());
		this.gameInstanceMgr.removeGameInstance(gameInstance);
		
		// 销毁副本的场景
		GameScene crtGameScene = gameInstance.getCrtGameScene();
		if (crtGameScene != null) {
			MMORPGContext.getGameAreaComponent().getGameArea().removeGameInstanceScene(crtGameScene);
			if (logger.isDebugEnabled()) {
				logger.debug("销毁副本," + gameInstance + ", 销毁副本场景,sceneId=" + crtGameScene.getId());
			}
		}
		
		gameInstance.close();
	}
}
