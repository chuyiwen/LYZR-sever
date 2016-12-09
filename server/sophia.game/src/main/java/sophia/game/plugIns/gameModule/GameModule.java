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
package sophia.game.plugIns.gameModule;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.component.Component;
import sophia.game.component.GameObject;
import sophia.game.plugIns.communication.SocketDisconnectListener;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;

public final class GameModule extends GameObject {
	private static final Logger logger = Logger.getLogger(GameModule.class.getName());
	
	private final Service service = new InnerService();
	
	private SocketActionEventLister actionEventLister;
	private SocketDisconnectListener disconnectLister;
	
	protected GameModule() {
		setId("GameModule");
	}
	
	public final void startUp() {
		State state = service.startAndWait();
		
		if (state == State.RUNNING) {
			if (logger.isDebugEnabled()) {
				logger.debug("MMORPGGame module was running.");
			}
		} else {
			logger.error("MMORPGGame module startUp failed.");
		}
	}
	
	public final void shutDown() {
		State state = service.stopAndWait();
		
		if (state == State.TERMINATED) {
			if (logger.isDebugEnabled()) {
				logger.debug("MMORPGGame module was terminated.");
			}
		} else {
			logger.error("MMORPGGame module shutDown failed.");
		}
	}
	
	@Override
	public void ready() {
		super.ready();
		
		for(Component component : getComponents()) {
			if (component instanceof SocketActionEventLister) {
				actionEventLister = (SocketActionEventLister) component;
			}
			if (component instanceof SocketDisconnectListener) {
				disconnectLister = (SocketDisconnectListener) component;
			}
		}
		
		if (actionEventLister == null) {
			throw new RuntimeException("GameModule can not find SocketActionEventLister component.");
		}
		
		if (disconnectLister == null) {
			throw new RuntimeException("GameModule can not find SocketDisconnectLister component");
		}
	}



	public void receivedActionEvent(ActionEventBase actionEvent) {
		Preconditions.checkNotNull(actionEvent, "actionEventLister can not be null.");
		
		actionEventLister.receivedActionEvent(actionEvent);
	}
	
	public void handleDisconnect(Identity identity) {
		Preconditions.checkNotNull(identity, "identity can not be null.");
		
		disconnectLister.handleDisconnect(identity);
	}
	
	private final class InnerService extends AbstractIdleService implements Service {

		@Override
		protected void shutDown() throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("MMORPGModule stoped.");
			}
		}

		@Override
		protected void startUp() throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("MMORPGModule started.");
			}
		}
	}
}
