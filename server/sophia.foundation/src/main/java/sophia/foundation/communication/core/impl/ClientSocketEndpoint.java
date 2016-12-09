/**
 * 
 */
package sophia.foundation.communication.core.impl;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.AbstractIoConnector;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import sophia.foundation.communication.core.ClientEndpoint;
import sophia.foundation.communication.core.Connector;
import sophia.foundation.communication.core.TransportType;



public class ClientSocketEndpoint implements ClientEndpoint<SocketAddress>{
	private static final Logger logger = Logger.getLogger(ClientSocketEndpoint.class.getName());
	
	public static final long Connect_Timeout = 30000;
	
	private final SocketAddress address;
	private final TransportType transportType;
	
	public ClientSocketEndpoint(SocketAddress address, TransportType transportType) {
		this.address = address;
		this.transportType = transportType;
	}

	@Override
	public Connector<SocketAddress> createConnector() throws IOException {
		AbstractIoConnector minaConnector;
		if (transportType.equals(TransportType.RELIABLE)) {
			minaConnector = new NioSocketConnector(4);
			minaConnector.setConnectTimeoutMillis(Connect_Timeout);

			((NioSocketConnector) minaConnector).getSessionConfig()
					.setTcpNoDelay(true);
		} else {
			minaConnector = new NioDatagramConnector();
			minaConnector.setConnectTimeoutMillis(Connect_Timeout);
		}
		SocketConnector connector = new SocketConnector(this, minaConnector);
		if (logger.isDebugEnabled()) {
			logger.debug("returning " + connector);
		}
		return connector;
	}

	@Override
	public SocketAddress getAddress() {
		return address;
	}
}
