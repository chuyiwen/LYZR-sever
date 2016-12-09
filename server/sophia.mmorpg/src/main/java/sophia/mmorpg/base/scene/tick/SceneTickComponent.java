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
package sophia.mmorpg.base.scene.tick;

import org.apache.log4j.Logger;

import sophia.foundation.task.PeriodicTaskHandle;
import sophia.foundation.task.Task;
import sophia.foundation.task.TaskManager;
import sophia.game.GameContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service.State;

public class SceneTickComponent extends ConcreteComponent<GameScene> {

	private static final Logger logger = Logger
			.getLogger(SceneTickComponent.class);
	
	private static final String tickGameEventName = SceneTick_GE.class.getSimpleName();

	protected static final int DefaultTickTimeMillis = 150;

	private TickService tickService = new TickService();
	
	private SceneTick_GE tickGameEvent = new SceneTick_GE();

	@Override
	public void ready() {
		State state = tickService.startAndWait();
		if (state == State.RUNNING) {
			if (logger.isDebugEnabled()) {
				logger.debug(" GameSceneTick was running.");
			}
		} else {
			logger.error(" GameSceneTick start failed.");
		}

		super.ready();
	}
	
	@Override
	public void suspend() {
		stopTickService();
		super.suspend();
	}

	@Override
	public void destroy() {
		stopTickService();
		super.destroy();
	}
	
	public void stopTickService() {
		State state = tickService.stopAndWait();

		if (state == State.TERMINATED) {
			if (logger.isDebugEnabled()) {
				logger.debug(" GameSceneTick was terminated.");
			}
		} else {
			logger.error(" GameSceneTick stop failed.");
		}
	}

	private final class TickService extends AbstractIdleService {
		private PeriodicTaskHandle periodicTaskHandle;

		@Override
		protected void shutDown() throws Exception {
			if (periodicTaskHandle != null) {
				periodicTaskHandle.cancel();
			}

			if (logger.isDebugEnabled()) {
				logger.debug(getParent().getId() + " scene shutDown");
			}
		}

		@Override
		protected void startUp() throws Exception {
			TaskManager taskManager = GameContext.getTaskManager();
			periodicTaskHandle = taskManager.schedulePeriodicTask(new Task() {
				public void run() {
					try {
						tickGameEvent.setTimestamp(System.currentTimeMillis());
						sendGameEvent(tickGameEventName, tickGameEvent,
								getConcreteParent().getId());
					} catch (Throwable t) {
						logger.error("scene" + getParent().getId()
								+ " tick error!", t);
					}
				}
			}, 2000l, DefaultTickTimeMillis);

			if (logger.isDebugEnabled()) {
				logger.debug(getParent().getId() + " scene startUp");
			}
		}

	}
}
