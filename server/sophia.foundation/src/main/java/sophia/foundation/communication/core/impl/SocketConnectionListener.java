/**
 * 
 */
package sophia.foundation.communication.core.impl;


import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.ConnectionListener;


public class SocketConnectionListener extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(SocketConnectionListener.class.getName());
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		SocketConnection connection = (SocketConnection) session.getAttribute(SocketConnection.Connection_Key);
		connection.getListener().exceptionThrown(connection, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		SocketConnection connection = (SocketConnection) session.getAttribute(SocketConnection.Connection_Key);
		
		ConnectionListener listener = connection.getListener();
		
		if (listener == null) {
			logger.info("shit............");
			return;
		}
		
		listener.messageReceived(connection, (ActionEventBase)message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(session.getId() + " closed");
		}
		SocketConnection connection = (SocketConnection) session.getAttribute(SocketConnection.Connection_Key);
		connection.getListener().disconnected(connection, false);
		
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(session.getId() + " opened");
		}
		SocketConnection connection = (SocketConnection) session.getAttribute(SocketConnection.Connection_Key);
		connection.getListener().connected(connection);
	}
	
}
