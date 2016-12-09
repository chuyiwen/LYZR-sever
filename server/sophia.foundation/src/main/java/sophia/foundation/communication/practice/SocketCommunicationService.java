/**
 * 
 */
package sophia.foundation.communication.practice;

import java.util.Collection;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;

import com.google.common.util.concurrent.Service;


public interface SocketCommunicationService extends Service {
	public static final String Host_Property = "sophia.communication2.practice.SocketCommunicationService.host";
	public static final String Default_Host = "localhost";

	public static final String BindPorts_Property = "sophia.communication2.practice.SocketCommunicationService.bindPorts";
	public static final int[] Default_BindPorts = { 8888 };
	
	String getHost();
	void setHost(String host);
	
	int[] getBindPorts();
	void setBindPorts(int[] bindPorts);
	
	public SocketCommunicationServiceListener getCommunicationServiceListener();
	void setCommunicationServiceListener(SocketCommunicationServiceListener communicationServiceListener);
	
	public int getSessionCount();
	
	void sendActionEventMessageToAllSession(ActionEventBase message);
	
	void sendActionEventMessageToAllIdentityBindedSession(ActionEventBase actionEventMessage);
	
	void sendActionEventMessageToAllUnbindedSession(ActionEventBase message);
	
	void sendActionEventMessageToSession(Connection session, ActionEventBase message);
	
	void sendActonEventMessage(ActionEventBase actionEventMessage) throws Exception;
	
	void attachSession(Connection session, Identity identity);
	void detachSession(Identity identity, Connection session);
	
	Collection<Connection> getAttachSessionCollection(Identity identity);
	
	boolean checkActionEventMessage(Identity identity);
	void closeSession(Identity identity);
	Connection getSession(Identity identity);
}
