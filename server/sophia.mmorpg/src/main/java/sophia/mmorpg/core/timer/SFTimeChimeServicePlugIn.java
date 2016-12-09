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
package sophia.mmorpg.core.timer;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.Service.State;

import sophia.game.core.PlugIn;

public final class SFTimeChimeServicePlugIn implements PlugIn<SFTimeChimeService> {
	private static final Logger logger = Logger.getLogger(SFTimeChimeServicePlugIn.class.getName());
	
	private SFTimeChimeService timeChimeService;
	
	public SFTimeChimeServicePlugIn() {
		
	}
	
	@Override
	public SFTimeChimeService getModule() {
		return timeChimeService;
	}

	@Override
	public void initialize() {
		timeChimeService = new SFTimeChimeService();
	}

	@Override
	public void start() {
		State state = timeChimeService.startAndWait();
		if (state != State.RUNNING) {
			logger.error("SFTimeChimeService start failed.");
			throw new RuntimeException("SFTimeChimeService start failed.");
		} else {
			logger.info("SFTimeChimeService was started.");
		}
	}

	@Override
	public void stop() {
		State state = timeChimeService.stopAndWait();
		if (state != State.TERMINATED) {
			logger.error("SFTimeChimeService stop failed.");
		} else {
			logger.info("SFTimeChimeService was terminated.");
		}
	}

	@Override
	public void cleanUp() {
		
	}
}
