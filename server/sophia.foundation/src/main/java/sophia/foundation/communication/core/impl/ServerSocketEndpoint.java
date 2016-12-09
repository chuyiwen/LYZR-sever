/**
 * 
 */
package sophia.foundation.communication.core.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;

import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import sophia.foundation.communication.core.Acceptor;
import sophia.foundation.communication.core.ServerEndpoint;
import sophia.foundation.communication.core.TransportType;


public class ServerSocketEndpoint implements ServerEndpoint<SocketAddress> {
	//public static final Executor defaultExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	private final ArrayList<SocketAddress> addresses = new ArrayList<SocketAddress>();

	private final TransportType transportType;

	public ServerSocketEndpoint(SocketAddress address, TransportType transportType) {
		addresses.add(address);
		this.transportType = transportType;
	}

	public ServerSocketEndpoint(Collection<SocketAddress> addresses, TransportType transportType) {
		this.addresses.addAll(addresses);
		this.transportType = transportType;
	}

	public ServerSocketEndpoint(Collection<SocketAddress> addresses, TransportType transportType, Executor executor, int numProcessors) {
		this.addresses.addAll(addresses);
		this.transportType = transportType;
		// TODO: 黄晓源
	}

	@Override
	public Acceptor<SocketAddress> createAcceptor() throws IOException {
		AbstractIoAcceptor minaAcceptor;

		if (transportType.equals(TransportType.RELIABLE)) {
			minaAcceptor = new NioSocketAcceptor();
		} else {
			minaAcceptor = new NioDatagramAcceptor();
		}

		SocketAcceptor socketAcceptor = new SocketAcceptor(this, minaAcceptor);

		return socketAcceptor;
	}

	@Override
	public Collection<SocketAddress> getAddresses() {
		return addresses;
	}
}
