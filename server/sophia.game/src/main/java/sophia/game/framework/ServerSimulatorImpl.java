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
package sophia.game.framework;

import org.apache.log4j.Logger;

import sophia.game.core.GameFrame;
import sophia.game.core.GameFrame.Status;
import sophia.game.core.GameFrameStatusListener;
import sophia.game.core.PlugIn;

import com.google.common.util.concurrent.AbstractIdleService;

public final class ServerSimulatorImpl extends AbstractIdleService implements ServerSimulator {
	private static final Logger logger = Logger.getLogger(ServerSimulatorImpl.class.getName());
	
	private final GameFrame gameFrame = new GameFrame(new InnerGameFrameStatusListener());
	
	@Override
	public void initialize() {
		gameFrame.initialize(new InnerGameSimulator());
	}
	
	@Override
	protected void shutDown() throws Exception {
		gameFrame.shutDown();
	}

	@Override
	protected void startUp() throws Exception {
		gameFrame.startUp();
	}

	@Override
	public void registerPlugIn(PlugIn<?> plugIn) {
		gameFrame.registerPlugIn(plugIn);
	}
	
	@Override
	public PlugIn<?> requestPlugIn(Class<PlugIn<?>> plugInClass) {
		return gameFrame.requestPlugIn(plugInClass);
	}

	@Override
	public Object requestModule(Class<?> moduleClass) {
		return gameFrame.requestModule(moduleClass);
	}
	
	private class InnerGameSimulator implements GameSimulator {

		@Override
		public void initialize() {
			if (logger.isDebugEnabled())
				logger.debug("ServerSimulator initialize...");
		}

		@Override
		public void start() {
			if (logger.isDebugEnabled())
				logger.debug("ServerSimulator start...");
		}

		@Override
		public void stop() {
			if (logger.isDebugEnabled())
				logger.debug("ServerSimulator stop...");
		}

		@Override
		public void cleanUp() {
			if (logger.isDebugEnabled())
				logger.debug("ServerSimulator cleanUP...");
		}
	}
	
	private class InnerGameFrameStatusListener implements GameFrameStatusListener {
		@Override
		public void statusChanged(GameFrame gameFrame, Status status) {
			if (logger.isDebugEnabled()) {
				logger.debug("changed status " + status);
			}
		}
	}

}
