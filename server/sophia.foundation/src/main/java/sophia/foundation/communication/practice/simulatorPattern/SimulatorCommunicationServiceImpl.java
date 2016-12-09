/**
 * 
 */
package sophia.foundation.communication.practice.simulatorPattern;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.AcceptorListener;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.core.ConnectionListener;
import sophia.foundation.communication.practice.AbstractSocketCommunicationService;
import sophia.foundation.communication.practice.SocketCommunicationServiceListener;
import sophia.foundation.util.DebugUtil;

public class SimulatorCommunicationServiceImpl extends AbstractSocketCommunicationService implements SimulatorCommunicationService {
	private static final Logger logger = Logger.getLogger(SimulatorCommunicationServiceImpl.class.getName());

	public static final String Identity_Key = "identity";

	protected ActionEventMessageHandler actionEventMessagehandler;

	public SimulatorCommunicationServiceImpl() {

	}

	public SimulatorCommunicationServiceImpl(SocketCommunicationServiceListener socketCommunicationServiceListener, ActionEventMessageHandler actionEventMessageHandler) {
		super(socketCommunicationServiceListener);

		if (actionEventMessageHandler == null) {
			throw new NullPointerException();
		}
		this.actionEventMessagehandler = actionEventMessageHandler;

		this.acceptorListener = new AcceptorListenerImpl();
	}

	class AcceptorListenerImpl implements AcceptorListener {

		@Override
		public ConnectionListener newConnection() {
			return new ConnectionListenerImpl();
		}

		@Override
		public void disconnected() {
			SimulatorCommunicationServiceImpl.this.communicationServiceListener.disconnect();
		}
	}

	class ConnectionListenerImpl implements ConnectionListener {

		@Override
		public void connected(Connection connection) {
			//sessionMgr.addSession(connection);
		}

		@Override
		public void messageReceived(Connection connection, ActionEventBase message) {
			if (logger.isDebugEnabled()) {
				
				if (message.getIdentity() != null) {
					logger.debug("messageReceived identityName=" + message.getIdentity().getName());
				}
				
				logger.debug("messageReceived " + message.toString());
			}

			actionEventMessagehandler.messageReceived(connection, message);
		}

		@Override
		public void exceptionThrown(Connection connection, Throwable exception) {
			// ignore "java.io.IOException: Connection reset by peer"
			if (exception != null && exception.getMessage() != null && exception.getMessage().startsWith("Connection reset by peer") || exception != null
					&& exception.getLocalizedMessage() != null && exception.getLocalizedMessage().startsWith("远程主机强迫关闭了一个现有的连接")) {
				return;
			}
			
			if (logger.isInfoEnabled()) {
				logger.info("exceptionThrown, " + DebugUtil.printStack(exception));
			}
			
			actionEventMessagehandler.exceptionThrown(connection, exception);
		}

		@Override
		public void disconnected(Connection connection, boolean graceful) {
			//sessionMgr.removeSession(connection);
			Identity identity = (Identity) connection.getAttribute(Identity_Key);
			if (identity != null) {
				actionEventMessagehandler.disconnected(connection, graceful);
				bindedSessionMgr.removeSession(identity, connection);
			}
		}
	}

	@Override
	public ActionEventMessageHandler getActionEventMessageHandler() {
		return actionEventMessagehandler;
	}

	@Override
	public void setActionEventMessageHandler(ActionEventMessageHandler handler) {
		if (handler == null) {
			throw new NullPointerException("handler can not be null.");
		}

		this.actionEventMessagehandler = handler;
	}
}
