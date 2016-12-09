/**
 * 
 */
package sophia.foundation.communication.practice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.mina.util.ConcurrentHashSet;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;


public class SessionManager {
	private final ConcurrentHashSet<Connection> sessionSet = new ConcurrentHashSet<Connection>();
	
	public SessionManager() {
		
	}
	
	public void addSession(Connection session) {
		if (session == null) {
			throw new NullPointerException();
		}
		
		sessionSet.add(session);
	}
	
	public void removeSession(Connection session) {
		sessionSet.remove(session);
	}
	
	public Collection<Connection> getSessionCollection() {
		Collection<Connection> collection = new ArrayList<Connection>(sessionSet);
		return collection;
	}
	
	public int getSessionNumber() {
		return sessionSet.size();
	}
	
	public void sendMessageToAllSession(ActionEventBase message) {
		for(Connection session : sessionSet) {
			try {
				session.sendMessage(message);
			} catch (IOException e) {
			}
		}
	}
}
