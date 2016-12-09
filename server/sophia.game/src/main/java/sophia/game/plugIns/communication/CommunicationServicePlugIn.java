/**
 * 
 */
package sophia.game.plugIns.communication;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.practice.SocketCommunicationService;
import sophia.foundation.communication.practice.SocketCommunicationServiceListener;
import sophia.foundation.communication.practice.simulatorPattern.ActionEventMessageHandler;
import sophia.foundation.communication.practice.simulatorPattern.SimulatorCommunicationService;
import sophia.foundation.communication.practice.simulatorPattern.SimulatorCommunicationServiceImpl;
import sophia.foundation.core.FoundationContext;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.PropertiesWrapper;
import sophia.game.core.Dependency;
import sophia.game.core.PlugIn;
import sophia.game.plugIns.gameModule.GameModule;
import sophia.game.plugIns.gateWay.GateWay;


public class CommunicationServicePlugIn implements PlugIn<SimulatorCommunicationService> {
	private static final Logger logger = Logger.getLogger(CommunicationServicePlugIn.class.getName());
	
	@Dependency private GateWay gateWay;
	@Dependency private GameModule gameModule;
	
	public void setGateWay(GateWay gateWay) {
		this.gateWay = gateWay;
	}

	public void setGameModule(GameModule gameModule) {
		this.gameModule = gameModule;
	}
	

	private SimulatorCommunicationService communicationService;
	
	public SimulatorCommunicationService getCommunicationService() {
		return communicationService;
	}

	@Override
	public SimulatorCommunicationService getModule() {
		return communicationService;
	}

	@Override
	public void initialize() {
		communicationService = new SimulatorCommunicationServiceImpl(
				new CommunicationServiceListener(), new ActionEventHandler());
		PropertiesWrapper properties = FoundationContext.getProperties();
		String host = properties.getProperty(SocketCommunicationService.Host_Property, SocketCommunicationService.Default_Host);
		int[] bindPorts = properties.getIntArrayProperty(SocketCommunicationService.BindPorts_Property, SocketCommunicationService.Default_BindPorts);
		communicationService.setHost(host);
		communicationService.setBindPorts(bindPorts);
		FoundationContext.addService(communicationService);
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void cleanUp() {
	}
	
	
	public class CommunicationServiceListener implements
			SocketCommunicationServiceListener {

		@Override
		public void disconnect() {

		}
	}

	public class ActionEventHandler implements ActionEventMessageHandler {
		
		@Override
		public void messageReceived(Connection connection,
				ActionEventBase actionEventMessage) {
			Identity identity = actionEventMessage.getIdentity();
			try {
				if (identity == null) {
					if (!gateWay.getActionEvents().containsKey(actionEventMessage.getActionEventId())) {
						// 非法消息，断开连接
						logger.error("invalid message, actionEventId=" + actionEventMessage.getActionEventId());
						return;
					}
					gateWay.receivedActionEvent(connection, actionEventMessage);
				} else {
					gameModule.receivedActionEvent(actionEventMessage);
				}
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}

		@Override
		public void exceptionThrown(Connection connection, Throwable exception) {
			StringBuilder sb = new StringBuilder();
			sb.append(connection.getId()).append(", ");

			String identityName = null;
			Identity identity = (Identity) connection.getAttribute("identity");
			if (identity != null) {
				identityName = identity.getName();
				sb.append(identityName).append(", ");
			}
			sb.append("exceptionThrown");
			logger.error(sb.toString());
			if (logger.isDebugEnabled()) {
				logger.debug(DebugUtil.printStack(exception));
			}
		}

		@Override
		public void disconnected(Connection connection, boolean graceful) {
			Identity identity = (Identity) connection.getAttribute("identity");
			if (identity != null) {
				gameModule.handleDisconnect(identity);
			}
		}
	}
}
