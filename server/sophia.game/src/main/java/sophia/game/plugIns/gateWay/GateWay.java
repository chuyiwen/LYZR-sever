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
package sophia.game.plugIns.gateWay;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.util.DebugUtil;
import sophia.game.GameRoot;
import sophia.game.component.Component;
import sophia.game.component.GameObject;

public final class GateWay extends GameObject {
	
	private static final Logger logger = Logger.getLogger(GateWay.class);
	
	private Authenticate authenticate;
	
	protected GateWay() {
		setId("GateWay");
	}
	
	@Override
	public void ready() {
		super.ready();
		
		for(Component component : getComponents()) {
			if (component instanceof Authenticate) {
				authenticate = (Authenticate) component;
				break;
			}
		}
		
		if (authenticate == null) {
			throw new RuntimeException("GateWay can not find Authenticate component.");
		}
	}
	
	private void sendResposeMessage(Connection connection,
			ActionEventBase actionEventMessage) {
		try {
			connection.sendMessage(actionEventMessage);
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}
	}
	
	public void receivedActionEvent(Connection connection, ActionEventBase actionEvent) {
		ActionEventBase ae = authenticate.verify(actionEvent, connection);
		Identity identity = ae.getIdentity();
		if (identity != null) {
			GameRoot.getSimulatorCommunicationService().attachSession(connection, identity);
			connection.setAttribute("identity", identity);
		} else { // 验证失败
			logger.error("verify Failure!!! identity == null"
					+ actionEvent.getActionEventId() + " "
					+ actionEvent.toString());
		}
		sendResposeMessage(connection, ae);
	}
	
	public final void startUp() {
		if (logger.isDebugEnabled()) {
			logger.debug("GateWay is running.");
		}
	}
	
	public final void shutDown() {
		if (logger.isDebugEnabled()) {
			logger.debug("GateWay was terminated.");
		}
	} 
}
