/**
 * 
 */
package sophia.foundation.communication.practice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.Acceptor;
import sophia.foundation.communication.core.AcceptorListener;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.core.ServerEndpoint;
import sophia.foundation.communication.core.TransportType;
import sophia.foundation.communication.core.impl.ServerSocketEndpoint;

import com.google.common.util.concurrent.AbstractIdleService;


public class AbstractSocketCommunicationService extends AbstractIdleService
		implements SocketCommunicationService {
	private static final Logger logger = Logger.getLogger(SocketCommunicationService.class.getName());
	
	protected String host = Default_Host;

	protected int[] bindPorts = Default_BindPorts;

	protected AcceptorListener acceptorListener;

	protected Acceptor<SocketAddress> acceptor;
	
	protected SocketCommunicationServiceListener communicationServiceListener;
	
	protected SessionManager sessionMgr = new SessionManager();
	protected IdentityBindedSessionManager bindedSessionMgr = new IdentityBindedSessionManager();

	protected AbstractSocketCommunicationService() {

	}
	
	protected AbstractSocketCommunicationService(SocketCommunicationServiceListener socketCommunicationServiceListener) {
		if (socketCommunicationServiceListener == null) {
			throw new NullPointerException();
		}
		
		this.communicationServiceListener = socketCommunicationServiceListener;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public int[] getBindPorts() {
		return bindPorts;
	}

	@Override
	public void setBindPorts(int[] bindPorts) {
		this.bindPorts = bindPorts;
	}

	@Override
	public int getSessionCount() {
		return sessionMgr.getSessionNumber();
	}

	@Override
	protected void startUp() throws Exception {
		int bindPortNumber = bindPorts.length;

		Collection<SocketAddress> addresses = new ArrayList<SocketAddress>();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bindPortNumber; i++) {
			InetSocketAddress address = new InetSocketAddress(
					bindPorts[i]);
			addresses.add(address);
			sb.append(bindPorts[i]).append(",");
		}

		ServerEndpoint<SocketAddress> endpint = new ServerSocketEndpoint(
				addresses, TransportType.RELIABLE);

		acceptor = endpint.createAcceptor();
		acceptor.listen(acceptorListener);
		
		if (logger.isDebugEnabled()) {
			logger.debug("host=" + host + ",ports=" + sb.toString());
		}
	}

	@Override
	protected void shutDown() throws Exception {
		acceptor.shutDown();
	}
	
	@Override
	public void sendActonEventMessage(ActionEventBase actionEventMessage) throws IOException {
		Identity identity = actionEventMessage.getIdentity();
		bindedSessionMgr.sendMessageByIdentity(actionEventMessage, identity);
	}

	@Override
	public void attachSession(Connection session, Identity identity) {
		bindedSessionMgr.addSession(identity, session);
	}

	@Override
	public void detachSession(Identity identity, Connection session) {
		bindedSessionMgr.removeSession(identity, session);
	}

	@Override
	public Collection<Connection> getAttachSessionCollection(Identity identity) {
		return null;
	}
	
	@Override
	public boolean checkActionEventMessage(Identity identity){
		return bindedSessionMgr.checkSpeedUpMessage(identity);
	}
	
	@Override
	public void closeSession(Identity identity){
		bindedSessionMgr.closeSession(identity);
	}

	@Override
	public SocketCommunicationServiceListener getCommunicationServiceListener() {
		return communicationServiceListener;
	}

	@Override
	public void setCommunicationServiceListener(
			SocketCommunicationServiceListener communicationServiceListener) {
		this.communicationServiceListener = communicationServiceListener;
	}

	@Override
	public void sendActionEventMessageToAllSession(ActionEventBase message) {
		sessionMgr.sendMessageToAllSession(message);
	}

	@Override
	public void sendActionEventMessageToAllIdentityBindedSession(
			ActionEventBase actionEventMessage) {
		bindedSessionMgr.sendMessageToAllSession(actionEventMessage);
	}

	@Override
	public void sendActionEventMessageToAllUnbindedSession(
			ActionEventBase message) {
		Collection<Connection> allSessionCollection = sessionMgr.getSessionCollection();
		Collection<Connection> bindedSessionCollection = bindedSessionMgr.getSessionCollection();
		
		for(Connection session : allSessionCollection) {
			if (!bindedSessionCollection.contains(session)) {
				try {
					session.sendMessage(message);
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void sendActionEventMessageToSession(Connection session,
			ActionEventBase message) {
		try {
			session.sendMessage(message);
		} catch (IOException e) {
		}
	}

	@Override
	public Connection getSession(Identity identity) {
		return bindedSessionMgr.getSession(identity);
	}
}
