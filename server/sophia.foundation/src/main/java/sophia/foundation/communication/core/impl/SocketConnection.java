/**
 * 
 */
package sophia.foundation.communication.core.impl;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.core.ConnectionListener;


class SocketConnection implements Connection {
	private static final Logger logger = Logger.getLogger(SocketConnection.class.getName());

	public static final String Connection_Key = "SocketConnection";

	private final IoSession session;

	private final ConnectionListener listener;

	SocketConnection(ConnectionListener listener, IoSession session) {
		this.listener = listener;
		this.session = session;
	}

	@Override
	public void close(boolean graceful) throws IOException {
		if (!session.isConnected()) {
			if (logger.isDebugEnabled()) {
				IOException ioe = new IOException("SocketConnection.close: session not connected");
				logger.debug(ioe.getMessage(), ioe);
			}
		}

		session.close(graceful);
	}

	@Override
	public Object getAttribute(Object name) {
		return session.getAttribute(name);
	}

	@Override
	public long getCreationTime() {
		return session.getCreationTime();
	}

	@Override
	public long getId() {
		return session.getId();
	}

	@Override
	public long getLastIoTime() {
		return session.getLastIoTime();
	}

	@Override
	public long getLastReadTime() {
		return session.getLastReadTime();
	}

	@Override
	public long getLastWriteTime() {
		return session.getLastWriteTime();
	}

	@Override
	public long getReadBytes() {
		return session.getReadBytes();
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return session.getRemoteAddress();
	}

	@Override
	public boolean isClosing() {
		return session.isClosing();
	}

	@Override
	public boolean isConnected() {
		return session.isConnected();
	}

	@Override
	public void sendMessage(ActionEventBase message) throws IOException {
		if (!session.isConnected()) {
			IOException ioe = new IOException("SocketConnection.close: session not connected");
			throw ioe;
		}

		session.write(message);
	}

	@Override
	public void setAttributeIfAbsent(Object name, Object value) {
		session.setAttributeIfAbsent(name, value);
	}
	
	@Override
	public void setAttribute(Object name, Object value) {
		session.setAttribute(name, value);
	}

	public final IoSession getSession() {
		return session;
	}

	public final ConnectionListener getListener() {
		return listener;
	}

	@Override
	public String getIP() {
		if (session != null && session.getRemoteAddress() != null)
			return session.getRemoteAddress().toString();
		return "";
	}
}
