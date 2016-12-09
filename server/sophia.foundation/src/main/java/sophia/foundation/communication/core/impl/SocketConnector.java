/**
 * 
 */
package sophia.foundation.communication.core.impl;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import sophia.foundation.communication.core.ClientEndpoint;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.core.ConnectionListener;
import sophia.foundation.communication.core.Connector;

class SocketConnector implements Connector<SocketAddress> {
	private static final Logger logger = Logger.getLogger(SocketConnector.class.getName());

	private final IoConnector connector;

	private ConnectorConnListner connListener = null;

	private final ClientSocketEndpoint endpoint;

	private ConnectFuture connectFuture;

	private Connection connection;

	SocketConnector(ClientSocketEndpoint endpoint, IoConnector connector) {
		this.endpoint = endpoint;
		this.connector = connector;
	}

	@Override
	public void connect(ConnectionListener listener) throws IOException {
		synchronized (this) {
			if (connListener != null) {
				RuntimeException e = new IllegalStateException("Connection already in progress");
				logger.warn(e.getMessage(), e);
				throw e;
			}

			connListener = new ConnectorConnListner(listener);
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactoryImpl()));
			connector.setHandler(connListener);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("connecting to " + endpoint);
		}

		try {
			ConnectFuture future = connector.connect(endpoint.getAddress());
			future.awaitUninterruptibly();
			synchronized (this) {
				connectFuture = future;
				connection = (Connection) connectFuture.getSession().getAttribute(SocketConnection.Connection_Key);
			}
		} catch (RuntimeIoException e) {

		}

	}

	@Override
	public synchronized Connection getConnection() {
		return connection;
	}

	@Override
	public ClientEndpoint<SocketAddress> getEndpoint() {
		return endpoint;
	}

	@Override
	public synchronized boolean isConnected() {
		if (connectFuture == null) {
			return false;
		}
		return connectFuture.isConnected();
	}

	@Override
	public void shutDown() {
		if (logger.isDebugEnabled()) {
			logger.debug("shutdown called");
		}

		synchronized (this) {
			if (connListener == null) {
				RuntimeException e = new IllegalStateException("No connection in progress");
				logger.warn(e.getMessage(), e);
				throw e;
			}

			connListener.cancel();
		}
	}

	@Override
	public boolean waitForConnect(long timeout) throws IOException, InterruptedException {
		ConnectFuture future;
		synchronized (this) {
			future = connectFuture;
		}
		if (future == null) {
			throw new IllegalStateException("No connect attempt in progress");
		}
		if (!future.isConnected()) {
			future.await(timeout);
		}

		boolean ready = future.isDone();
		if (ready) {
			try {
				future.getSession();
			} catch (RuntimeIoException e) {
				Throwable t = e.getCause();
				if (t instanceof IOException) {
					throw (IOException) t;
				}
			}
		}
		return ready;
	}

	static final class ConnectorConnListner extends SocketConnectionListener {
		private final ConnectionListener listener;

		private boolean cancelled = false;

		private boolean connected = false;

		ConnectorConnListner(ConnectionListener listener) {
			this.listener = listener;
		}

		void cancel() {
			synchronized (this) {
				if (connected) {
					RuntimeException e = new IllegalStateException("Already connected");
					logger.warn(e.getMessage(), e);
					throw e;
				}
				if (cancelled) {
					RuntimeException e = new IllegalStateException("Already cancelled");
					logger.warn(e.getMessage(), e);
					throw e;
				}
				cancelled = true;
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			synchronized (this) {
				if (cancelled) {
					if (logger.isDebugEnabled()) {
						logger.debug("cancelled; ignore created session " + session);
					}
					session.close(true);
					return;
				}
				connected = true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("created session " + session);
			}

			SocketConnection connection = new SocketConnection(listener, session);
			session.setAttribute(SocketConnection.Connection_Key, connection);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			connected = false;
			super.sessionClosed(session);
		}
		
		
	}// Class ConnectorConnListner

}
