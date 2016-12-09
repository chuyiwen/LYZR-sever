/**
 * 
 */
package sophia.foundation.communication.core.impl;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import sophia.foundation.communication.core.Acceptor;
import sophia.foundation.communication.core.AcceptorListener;
import sophia.foundation.communication.core.ServerEndpoint;


class SocketAcceptor implements Acceptor<SocketAddress> {
	private final ServerSocketEndpoint serverSocketEndpoint;

	private final IoAcceptor minaAcceptor;

	private volatile boolean shutdown = false;

	public SocketAcceptor(ServerSocketEndpoint serverSocketEndpoint, IoAcceptor minaAcceptor) {
		this.serverSocketEndpoint = serverSocketEndpoint;
		this.minaAcceptor = minaAcceptor;
	}

	@Override
	public ServerEndpoint<SocketAddress> getEndpoint() {
		return serverSocketEndpoint;
	}

	@Override
	public void listen(AcceptorListener listener) throws IOException {
		synchronized (this) {
			checkShutdown();
			minaAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactoryImpl()));
			minaAcceptor.getFilterChain().addLast("ExecutorFilter", new ExecutorFilter(Runtime.getRuntime().availableProcessors() + 1));
			
			((NioSocketAcceptor)minaAcceptor).setReuseAddress(true);
			
			SocketSessionConfig config = (SocketSessionConfig) minaAcceptor.getSessionConfig();
			config.setBothIdleTime(60 * 60 * 5);
			config.setWriteTimeout(0);
			config.setKeepAlive(true);
			config.setReuseAddress(true);
			config.setTcpNoDelay(true);
			config.setSendBufferSize(1024 * 8);
			config.setReadBufferSize(1024 * 2);

			minaAcceptor.setHandler(new AcceptHandler(listener));

			minaAcceptor.bind(serverSocketEndpoint.getAddresses());
		}
	}

	@Override
	public void shutDown() {
		synchronized (this) {
			shutdown = true;
			minaAcceptor.unbind();
		}
	}

	private void checkShutdown() {
		if (shutdown) {
			throw new IllegalStateException("Acceptor has been shutdown");
		}
	}

	static final class AcceptHandler extends SocketConnectionListener {
		private final AcceptorListener acceptorListener;

		public AcceptHandler(final AcceptorListener acceptorListener) {
			this.acceptorListener = acceptorListener;
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			SocketConnection connection = new SocketConnection(acceptorListener.newConnection(), session);
			session.setAttribute(SocketConnection.Connection_Key, connection);
		}
	}
}
